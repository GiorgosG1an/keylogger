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

public class DecryptorCLI {
    private static final File LOG_FILE = new File("logs/keystrokes.enc");
    private BufferedReader reader;

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

    public void decryptAllLine(String password) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length != 2) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                byte[] ivBytes = Base64.getDecoder().decode(parts[0]);
                byte[] cipherBytes = Base64.getDecoder().decode(parts[1]);

                SecretKeySpec key = deriveKey(password.toCharArray());
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

    private SecretKeySpec deriveKey(char[] password) throws Exception {
        byte[] salt = "educationalSalt!".getBytes();
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
