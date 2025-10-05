package com.example.keyboardoverlay;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MacroManager implements NativeKeyListener {
    private final Map<Integer, Binding> bindings = new ConcurrentHashMap<>();
    private final Set<Integer> pressed = ConcurrentHashMap.newKeySet();
    private volatile boolean running = true;
    private Settings settings;
    private ShortcutHandler handler;

    public MacroManager() {
        settings = JsonStorage.load();
    }

    public void start() throws NativeHookException {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);
    }

    public void stop() {
        running = false;
        try { GlobalScreen.unregisterNativeHook(); } catch (Exception ignored) {}
        JsonStorage.save(settings);
    }

    public Settings getSettings() { return settings; }
    public void setShortcutHandler(ShortcutHandler h) { handler = h; }

    public boolean isSprinting() {
        return pressed.contains(org.jnativehook.keyboard.NativeKeyEvent.VC_SHIFT_L) || pressed.contains(org.jnativehook.keyboard.NativeKeyEvent.VC_SHIFT_R);
    }

    public void toggleEnabled() {
        settings.enabled = !settings.enabled;
        JsonStorage.save(settings);
    }

    public void changeDPI(int delta) {
        settings.virtualDPI = Math.max(1.0, settings.virtualDPI + delta);
        JsonStorage.save(settings);
    }

    public void changeIPS(double delta) {
        settings.inchesPerSecond = Math.max(0.0, settings.inchesPerSecond + delta);
        JsonStorage.save(settings);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        pressed.add(nativeEvent.getKeyCode());

        // check shortcuts (simple string matching omitted for brevity)
        // run bindings if present
        Binding b = bindings.get(nativeEvent.getKeyCode());
        if (b != null) {
            for (String act : b.getActions()) {
                // small executor - run on new thread
                final String a = act;
                new Thread(() -> executeAction(a)).start();
            }
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) { pressed.remove(e.getKeyCode()); }
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}

    private void executeAction(String action) {
        // basic: support single key tokens like SPACE, ENTER, or strings
        // (full SendInput key logic omitted here for brevity - can be extended)
        System.out.println("Execute action: " + action);
    }

    // Start mouse mover thread
    {
        Thread mover = new Thread(() -> {
            try {
                final double tickSec = 0.012;
                while (running) {
                    if (!settings.enabled) { Thread.sleep(12); continue; }
                    int dx = 0, dy = 0;
                    if (pressed.contains(NativeKeyEvent.VC_W)) dy -= 1;
                    if (pressed.contains(NativeKeyEvent.VC_S)) dy += 1;
                    if (pressed.contains(NativeKeyEvent.VC_A)) dx -= 1;
                    if (pressed.contains(NativeKeyEvent.VC_D)) dx += 1;
                    if (dx != 0 || dy != 0) {
                        double norm = Math.sqrt(dx*dx + dy*dy);
                        double ux = dx / norm;
                        double uy = dy / norm;
                        double pxPerSec = settings.virtualDPI * settings.inchesPerSecond;
                        double eff = pxPerSec * (isSprinting() ? settings.sprintMultiplier : 1.0);
                        int moveX = (int)Math.round(ux * eff * tickSec);
                        int moveY = (int)Math.round(uy * eff * tickSec);
                        Win32MouseMover.sendRelativeMove(moveX, moveY);
                    }
                    Thread.sleep(12);
                }
            } catch (InterruptedException ex) { }
        }, "MouseMover");
        mover.setDaemon(true);
        mover.start();
    }
}
