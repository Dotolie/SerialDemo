package com.friendlyarm.FriendlyThings;

import android.util.Log;



public class HardwareController {

    static public native int open();
    static public native void close(int fd);
    /* GPIO */
    static public native int setGPIOValue(int pin, int value);
    static public native int getGPIOValue(int pin);

    /* SPI */
//    static public native int setSPIWriteBitsPerWord( int spi_fd, int bits );
//    static public native int setSPIReadBitsPerWord( int spi_fd, int bits );
//    static public native int setSPIBitOrder( int spi_fd, int order);
//    static public native int setSPIClockDivider( int spi_fd, int divider);
//    static public native int setSPIMaxSpeed( int spi_fd, int spi_speed);
//    static public native int setSPIDataMode( int spi_fd, int mode);
//    static public native int SPItransferOneByte( int spi_fd, byte byteData, int spi_delay, int spi_speed, int spi_bits);
    static public native int SPItransferBytes(int spi_fd, byte[] writeData, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);
//    static public native int writeBytesToSPI(int spi_fd, byte[] writeData, int spi_delay, int spi_speed, int spi_bits);
//    static public native int readBytesFromSPI(int spi_fd, byte[] readBuff, int spi_delay, int spi_speed, int spi_bits);


    static {
        try {
            System.loadLibrary("friendlyarm-things");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "libfriendlyarm-things library not found!");
        }
    }
}
