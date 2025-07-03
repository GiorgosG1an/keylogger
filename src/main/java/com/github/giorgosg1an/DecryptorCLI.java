package com.github.giorgosg1an;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

/**
 * DecryptorCLI is a command-line utility for decrypting keystroke logs
 * that have been encrypted and stored in a file. It prompts the user for
 * a decryption password, derives an AES key using PBKDF2 with a salt
 * read from the log file, and then decrypts each line of the log file.
 *
 * <p>
 * The log file is expected to have the following format:
 * <ul>
 *   <li>The first line contains the salt in the format: <code>SALT::base64-salt</code></li>
 *   <li>Subsequent lines contain encrypted entries in the format: <code>base64-iv::base64-ciphertext</code></li>
 * </ul>
 * </p>
 *
 * <p>
 * Note: If the log file is missing, malformed, or the password is incorrect,
 * decryption will fail and an error message will be displayed.
 * </p>
 * 
 * @author Giannopoulos Georgios
 */
public class DecryptorCLI {
    private static final File LOG_FILE = new File("logs/keystrokes.enc");
    private BufferedReader reader;

    /**
     * Constructs a new instance of {@code DecryptorCLI}.
     * <p>
     * Prompts the user to enter a decryption password using a dialog box,
     * initializes a {@link BufferedReader} to read from the log file,
     * and attempts to decrypt all lines in the file using the provided password.
     * <p>
     * If an exception occurs during this process, the stack trace is printed
     * and the application exits with a status code of 1.
     *
     * @throws Exception if an error occurs during initialization or decryption.
     */
    public DecryptorCLI() throws Exception{

        try {
            String password = JOptionPane.showInputDialog(null, "Enter decryption password: ");
            this.reader = new BufferedReader(new FileReader(LOG_FILE));

            decryptAllLine(password);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Decrypts all lines from the input using the provided password.
     * <p>
     * The method expects the first line to contain a salt in the format "SALT::<base64-salt>",
     * which is used to derive the decryption key. Each subsequent line should contain an
     * initialization vector (IV) and a ciphertext, separated by "::", both encoded in Base64.
     * Each line is decrypted using AES/CBC/PKCS5Padding and the resulting plaintext is printed
     * to the standard output.
     * </p>
     *
     * @param password the password used to derive the decryption key
     * @throws IllegalStateException if the salt line is missing or malformed
     * @throws Exception if any error occurs during decryption
     */
    public void decryptAllLine(String password) {
        try {
            String saltLine = reader.readLine();
            if (saltLine == null || !saltLine.startsWith("SALT::")) {
                throw new IllegalStateException("Missing or Malformed salt line");
            }

            byte[] salt = Base64.getDecoder().decode(saltLine.split("::")[1]);
            SecretKeySpec key = deriveKey(password.toCharArray(), salt);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length != 2) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                byte[] ivBytes = Base64.getDecoder().decode(parts[0]);
                byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);

                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

                byte[] decryptedBytes = cipher.doFinal(cipherBytes);

                String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

                
                System.out.println(decryptedText);
            }
            
        } catch (Exception e) {
            System.err.println("An error occured. Details: \n" + e.getMessage());
        }
    }

    /**
     * Derives an AES SecretKeySpec from the provided password and salt using PBKDF2 with HMAC-SHA256.
     *
     * @param password the password to use for key derivation
     * @param salt the salt to use for key derivation
     * @return a SecretKeySpec suitable for AES encryption
     * @throws Exception if key derivation fails
     */
    private SecretKeySpec deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    
public static void main(String[] args) {
    try {
        new DecryptorCLI();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
