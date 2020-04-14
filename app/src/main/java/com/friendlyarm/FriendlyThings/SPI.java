package com.friendlyarm.FriendlyThings;
import android.util.Log;
import android.widget.Toast;

import com.friendlyarm.FriendlyThings.HardwareController;
import com.friendlyarm.FriendlyThings.SPIEnum;
import com.friendlyarm.FriendlyThings.GPIOEnum;
import com.friendlyarm.FriendlyThings.FileCtlEnum;

public class SPI {
    private static final String TAG = "SPI";
    private int spi_mode = 0;
    private int spi_bits = 8;
    private int spi_delay = 0;
    private int spi_speed = 500000;
    private int spi_byte_order = SPIEnum.LSBFIRST;

    private static final String devName = "/dev/spidev32766.0";
    private int spi_fd = -1;

    public void begin() {
        spi_fd = HardwareController.open( devName, FileCtlEnum.O_RDWR );
        if (spi_fd >= 0) {
            Log.d(TAG, "open " + devName + "ok!");
        } else {
            Log.d(TAG, "open " + devName + "failed!");
            spi_fd = -1;
        }
    }

    public void end() {
        if (spi_fd != -1) {
            HardwareController.close(spi_fd);
            spi_fd = -1;
        }
    }

    public void chipSelect(int cs) {

    }

    public int transfer(byte[] tx, byte[] rx) {
        if (spi_fd < 0) {
            return 0;
        }

        return HardwareController.SPItransferBytes(spi_fd, tx, rx, spi_delay, spi_speed, spi_bits);
    }
}

