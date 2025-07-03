package com.github.giorgosg1an;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * EncryptedLogger provides secure logging functionality by encrypting log entries
 * using AES encryption with a key derived from a user-provided password and a random salt.
 * 
 * <p>
 * The logger writes encrypted log entries to a file, with each entry containing a randomly
 * generated IV and the encrypted data, both Base64-encoded. The salt used for key derivation
 * is stored at the top of the log file.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with a password to initialize the encryption key and log file.</li>
 *   <li>Call {@link #encryptAndWrite(String)} to securely log plaintext messages.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Security details:
 * <ul>
 *   <li>Key derivation uses PBKDF2WithHmacSHA256 with a random 16-byte salt and 65536 iterations.</li>
 *   <li>Encryption uses AES in CBC mode with PKCS5 padding and a random 16-byte IV per entry.</li>
 *   <li>Salt and IVs are Base64-encoded for storage.</li>
 * </ul>
 * </p>
 *
 * @author Giannopoulos Georgios
 */
public class EncryptedLogger {
    private final SecretKeySpec key;
    private static final File LOG_FILE = new File("logs/keystrokes.enc");

    /**
     * Constructs an EncryptedLogger instance using the provided password.
     * <p>
     * This constructor generates a random salt and derives a cryptographic key from the given password and salt.
     * It ensures the log file's parent directories exist, and if the log file does not already exist,
     * it creates the file and writes the salt (Base64-encoded) as the first line, prefixed with "SALT::".
     * </p>
     *
     * @param password the password used to derive the encryption key
     * @throws Exception if an error occurs during key derivation, file creation, or writing to the log file
     */
    public EncryptedLogger(char[] password) throws Exception {
        
        byte[] salt = generateRandomSalt();
        this.key = deriveKey(password, salt);

        LOG_FILE.getParentFile().mkdirs();

        if (!LOG_FILE.exists()) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
                
            String encodedSalt = Base64.getEncoder().encodeToString(salt);
            writer.write("SALT::" + encodedSalt);
            writer.newLine(); 
            }
        }
    }

    /**
     * Generates a random 16-byte salt using a cryptographically secure random number generator.
     *
     * @return a byte array containing the generated salt
     */
    private byte[] generateRandomSalt() {
        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Derives a 128-bit AES secret key from the provided password and salt using PBKDF2 with HMAC-SHA256.
     *
     * @param password the password to use for key derivation
     * @param salt the salt to use for key derivation
     * @return a SecretKeySpec representing the derived AES key
     * @throws Exception if the key derivation process fails
     */
    private SecretKeySpec deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128); // 128 bit AES

        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Generates a random 16-byte Initialization Vector (IV) for cryptographic operations.
     * 
     * @return a new {@link IvParameterSpec} containing a securely generated random IV
     */
    private IvParameterSpec generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Encrypts the given plaintext using AES encryption in CBC mode with PKCS5 padding,
     * and writes the encrypted data to the log file. The method generates a random IV for
     * each encryption, encodes both the IV and the ciphertext in Base64, and writes them
     * to the log file separated by a double colon ("::").
     *
     * @param plaintext the plain text string to be encrypted and logged
     * @throws Exception if an error occurs during encryption or file writing
     */
    public void encryptAndWrite(String plaintext) throws Exception {
        IvParameterSpec iv = generateRandomIV();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        String encodedIV = Base64.getEncoder().encodeToString(iv.getIV());
        String encodedCipher = Base64.getEncoder().encodeToString(encrypted);

        String outputLine = encodedIV + "::" + encodedCipher;

        LOG_FILE.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(outputLine);
            writer.newLine();
        }
    }
}
