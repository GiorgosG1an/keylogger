package com.github.giorgosg1an;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;


public class KeyLogger implements KeyEventDispatcher{
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (e.getID() == KeyEvent.KEY_PRESSED) {
            String timestamp = TimestampUtil.now();
            String keyText = KeyEvent.getKeyText(e.getKeyCode());

            System.out.println("[" + timestamp + "] Key: " + keyText);

            // Exit on ESC
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.out.println("Exiting keylogger.");
                System.exit(0);
            }
        }
        return false;
    }
}
