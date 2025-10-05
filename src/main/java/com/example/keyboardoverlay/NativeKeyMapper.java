package com.example.keyboardoverlay;

import org.jnativehook.keyboard.NativeKeyEvent;

public class NativeKeyMapper {
    public static String getKeyText(int nativeCode) {
        try { return NativeKeyEvent.getKeyText(nativeCode); } catch (Exception e) { return "Key_"+nativeCode; }
    }
}
