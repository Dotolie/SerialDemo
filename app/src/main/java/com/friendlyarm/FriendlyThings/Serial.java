package com.friendlyarm.FriendlyThings;

import java.io.FileDescriptor;

public class Serial {
    private static final String LIB_NAME = "serial";

    private FileDescriptor mFd = null;

    public FileDescriptor Open(int devNo, int bps, int flags) {
        mFd = HardwareController.open(devNo, bps, flags);
        return mFd;
    }

    public void Close() {
        HardwareController.close();
    }
}
