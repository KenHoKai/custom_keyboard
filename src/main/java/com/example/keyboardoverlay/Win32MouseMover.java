package com.example.keyboardoverlay;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.ptr.IntByReference;

public class Win32MouseMover {
    private static final User32 USER32 = User32.INSTANCE;

    public static void sendRelativeMove(int dx, int dy) {
        if (dx == 0 && dy == 0) return;
        WinUser.MOUSEINPUT mi = new WinUser.MOUSEINPUT();
        mi.dx = dx;
        mi.dy = dy;
        mi.mouseData = new WinDef.DWORD(0);
        mi.dwFlags = new WinDef.DWORD(WinUser.MOUSEEVENTF_MOVE);
        mi.time = new WinDef.DWORD(0);
        mi.dwExtraInfo = new BaseTSD.ULONG_PTR(0);

        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi = mi;

        WinUser.INPUT[] arr = (WinUser.INPUT[]) input.toArray(1);
        USER32.SendInput(new WinDef.DWORD(arr.length), arr, input.size());
    }
}
