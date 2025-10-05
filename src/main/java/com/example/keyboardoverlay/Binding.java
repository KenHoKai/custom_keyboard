package com.example.keyboardoverlay;

import java.util.ArrayList;
import java.util.List;

public class Binding {
    private int nativeKeyCode;
    private List<String> actions = new ArrayList<>();

    public Binding() {}
    public Binding(int nativeKeyCode) { this.nativeKeyCode = nativeKeyCode; }

    public int getNativeKeyCode() { return nativeKeyCode; }
    public void setNativeKeyCode(int c) { this.nativeKeyCode = c; }
    public List<String> getActions() { return actions; }
    public void setActions(List<String> a) { this.actions = a; }

    @Override
    public String toString() {
        return "Key_" + nativeKeyCode + " -> " + String.join(", ", actions);
    }
}
