package com.friendlyarm.serialdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyarm.FriendlyThings.GPIOEnum;
import com.friendlyarm.FriendlyThings.HardwareController;
import com.friendlyarm.FriendlyThings.Serial;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "RS485 Test";
    protected static final FileDescriptor NULL = null;

    private Serial FingerModule = new Serial();

    private FileDescriptor mFd = null;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;


    //serial sending thread
    private SendingThread mSendingThread;
    //serial receiving thread
    private ReadingThread mReadingThread;
    //sending문자열
    private byte[] mWBuffer;
    private byte[] mRBuffer;

    private EditText mReception;
    private EditText mSendText;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView mTvSensor;

    private Switch mSwRelay0;
    private Switch mSwRelay1;
    private Switch mSwRelay2;
    private Switch mSwRelay3;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvSensor = findViewById(R.id.tv_sensor);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor == null) {
            Toast.makeText(this, "No light Sensor Found!", Toast.LENGTH_SHORT).show();
        }


        mSwRelay0 = findViewById(R.id.sw_relay0);
        mSwRelay0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY0, GPIOEnum.HIGH);
                else
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY0, GPIOEnum.LOW);
                Toast.makeText(MainActivity.this, "relay0="+isChecked, Toast.LENGTH_SHORT ).show();
            }
        });
        mSwRelay3 = findViewById(R.id.sw_relay3);
        mSwRelay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY3, GPIOEnum.HIGH);
                else
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY3, GPIOEnum.LOW);
                Toast.makeText(MainActivity.this, "relay3="+isChecked, Toast.LENGTH_SHORT ).show();
            }
        });
        mSwRelay1 = findViewById(R.id.sw_relay1);
        mSwRelay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY1, GPIOEnum.HIGH);
                else
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY1, GPIOEnum.LOW);
                Toast.makeText(MainActivity.this, "relay1="+isChecked, Toast.LENGTH_SHORT ).show();
            }
        });
        mSwRelay2 = findViewById(R.id.sw_relay2);
        mSwRelay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked )
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY2, GPIOEnum.HIGH);
                else
                    HardwareController.setGPIOValue(GPIOEnum.PIN_RELAY2, GPIOEnum.LOW);
                Toast.makeText(MainActivity.this, "relay2="+isChecked, Toast.LENGTH_SHORT ).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            mTvSensor.setText(event.values[0] + " lux");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    String byteArrayToHex(byte[] a, int nSize) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<nSize;i++)
            sb.append(String.format("%02x ", a[i]&0xff));
        sb.append(String.format("\r\n"));
        return sb.toString();
    }

    private class SendingThread extends Thread {
        @Override
        public void run() {
//			while (!isInterrupted()) {
            try {
                if (mFileOutputStream != null) {
                    mFileOutputStream.write(mWBuffer);
                } else {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
//			}
        }
    }

    private class ReadingThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[256];
                    if (mFileInputStream == null) return;
                    size = mFileInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mReception != null) {
                    //mReception.append(new String(buffer, 0, size));
                    String sRecived = "<=" + byteArrayToHex(buffer, size);
                    mReception.append( sRecived);
                }
            }
        });
    }
}
