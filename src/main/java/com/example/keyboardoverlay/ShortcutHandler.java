package com.example.keyboardoverlay;

public interface ShortcutHandler {
    void toggleEnabled();
    void toggleLockOverlay();
    void openSettingsOverlay();
    void minimizeLockOverlay();
    void changeDPI(int delta);
    void changeIPS(double delta);
    void toggleApp();
}
