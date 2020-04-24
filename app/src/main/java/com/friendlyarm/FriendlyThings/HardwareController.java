package com.friendlyarm.FriendlyThings;

import android.util.Log;



public class HardwareController {

    static public native int open();
    static public native void close(int fd);
    /* GPIO */
    static public native int setGPIOValue(int pin, int value);
    static public native int getGPIOValue(int pin);

    /* SPI */
    public interface InterruptCallback{
        void onNewValue(int value);
    }
    static public native void getSpiInt( InterruptCallback callback );
    static public native int SPItransferBytes(int spi_fd, byte[] writeData, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);

    static {
        try {
            System.loadLibrary("friendlyarm-things");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "libfriendlyarm-things library not found!");
        }
    }
}
