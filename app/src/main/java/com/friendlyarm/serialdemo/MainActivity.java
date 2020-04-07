package com.friendlyarm.serialdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private Serial serialPort0 = new Serial();
    private Serial serialPort1 = new Serial();

    private FileDescriptor mFd0 = null;
    private FileInputStream mFileInputStream0;
    private FileOutputStream mFileOutputStream0;

    private FileDescriptor mFd1 = null;
    private FileInputStream mFileInputStream1;
    private FileOutputStream mFileOutputStream1;

    //serial sending thread
    private SendingThread0 mSendingThread0;
    private SendingThread1 mSendingThread1;
    //serial receiving thread
    private ReadingThread0 mReadingThread0;
    private ReadingThread1 mReadingThread1;
    //sending문자열
    private byte[] mWBuffer0;
    private byte[] mRBuffer0;
    private byte[] mWBuffer1;
    private byte[] mRBuffer1;

    private TextView mReception0;
    private EditText mSendText0;
    private TextView mReception1;
    private EditText mSendText1;


    private Button mBtOpen;
    private Button mBtClose;
    private Button mBtSend0;
    private Button mBtSend1;

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

        mSendText0 = findViewById(R.id.edt_send_ch0);
        mReception0 = findViewById(R.id.tv_receive_ch0);

        mBtOpen = findViewById(R.id.bt_open);
        mBtOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFd0 == NULL) {
                    mFd0 = serialPort0.Open(0, 9600, 0);
                    if( mFd0 != NULL) {
                        mFileInputStream0 = new FileInputStream(mFd0);
                        mFileOutputStream0 = new FileOutputStream(mFd0);

                        if( mReadingThread0 == null ) {
                            mReadingThread0 = new ReadingThread0();
                            mReadingThread0.start();
                        }
                    }
                }
//                if(mFd1 == NULL) {
//                    mFd1 = serialPort0.Open(1, 9600, 0);
//                    if( mFd1 != NULL) {
//                        mFileInputStream1 = new FileInputStream(mFd1);
//                        mFileOutputStream1 = new FileOutputStream(mFd1);
//
//                        if( mReadingThread1 == null ) {
//                            mReadingThread1 = new ReadingThread1();
//                            mReadingThread1.start();
//                        }
//                    }
//                }
            }
        });

        mBtClose = findViewById(R.id.bt_close);
        mBtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReadingThread0.interrupt();
                if(mFd0 !=null ) {
                    serialPort0.Close();
                    mFd0 = null;
                }
//                mReadingThread1.interrupt();
//                if(mFd1 !=null ) {
//                    serialPort1.Close();
//                    mFd1 = null;
//                }
            }
        });

        mBtSend0 = findViewById(R.id.bt_send1);
        mBtSend0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mFd0 == null )
                    return;
                if(mSendingThread0 == null)
                    return;

                mWBuffer0 = mSendText0.getText().toString().getBytes();
                mSendingThread0 = new SendingThread0();
                mSendingThread0.start();
            }
        });

        mBtSend1 = findViewById(R.id.bt_send2);
        mBtSend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mFd1 == null )
                    return;
                if(mSendingThread1 == null)
                    return;

                mWBuffer1 = mSendText1.getText().toString().getBytes();
                mSendingThread1 = new SendingThread1();
                mSendingThread1.start();
            }
        });

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

    private class SendingThread0 extends Thread {
        @Override
        public void run() {
//			while (!isInterrupted()) {
            try {
                if (mFileOutputStream0 != null) {
                    mFileOutputStream0.write(mWBuffer0);
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

    private class SendingThread1 extends Thread {
        @Override
        public void run() {
//			while (!isInterrupted()) {
            try {
                if (mFileOutputStream1 != null) {
                    mFileOutputStream1.write(mWBuffer1);
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
    private class ReadingThread0 extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[256];
                    if (mFileInputStream0 == null) return;
                    size = mFileInputStream0.read(buffer);
                    if (size > 0) {
                        onDataReceived0(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void onDataReceived0(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mReception0 != null) {
                    //mReception.append(new String(buffer, 0, size));
                    String sRecived = "<=" + byteArrayToHex(buffer, size);
                    mReception0.append( sRecived);
                }
            }
        });
    }

    private class ReadingThread1 extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[256];
                    if (mFileInputStream1 == null) return;
                    size = mFileInputStream1.read(buffer);
                    if (size > 0) {
                        onDataReceived1(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void onDataReceived1(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mReception1 != null) {
                    //mReception.append(new String(buffer, 0, size));
                    String sRecived = "<=" + byteArrayToHex(buffer, size);
                    mReception1.append( sRecived);
                }
            }
        });
    }
}
