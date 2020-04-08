package com.friendlyarm.FriendlyThings;


import android.util.Log;

import java.io.FileDescriptor;

public class Serial {
    private static final String LIB_NAME = "serial";

    private FileDescriptor mFd = null;

    public FileDescriptor Open(int devNo, int bps, int flags) {
        mFd = open(devNo, bps, flags);
        return mFd;
    }

    public void Close() {
        close();
    }

    /* Serial Port */
    native FileDescriptor open(int devNo, int baudrate, int flags);
    native void close();

    static {
        try {
            System.loadLibrary("friendlyarm-things");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "libfriendlyarm-things library not found!");
        }
    }
}
