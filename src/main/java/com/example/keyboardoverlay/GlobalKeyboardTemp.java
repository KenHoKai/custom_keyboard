package com.example.keyboardoverlay;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyboardTemp {
    private static NativeKeyListener current;
    public static synchronized void addOneShotListener(NativeKeyListener l) {
        current = l;
        GlobalScreen.addNativeKeyListener(current);
    }
    public static synchronized void removeThisListener(NativeKeyListener l) {
        if (current == l) {
            try { GlobalScreen.removeNativeKeyListener(current); } catch (Exception ignored) {}
            current = null;
        }
    }
}
