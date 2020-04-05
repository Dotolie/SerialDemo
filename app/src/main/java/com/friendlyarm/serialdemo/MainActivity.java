package com.friendlyarm.serialdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.friendlyarm.FriendlyThings.Serial;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
