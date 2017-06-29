package com.example.yinpengcheng.nfcpractise;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    TextView promt;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        promt = (TextView)findViewById(R.id.promt);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            promt.setText("设备不支持nfc");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()){
            promt.setText("在系统设置中启动nfc功能");
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            processIntent(intent);
        }
    }

    private String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0){
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++){
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            Log.d(TAG,"buffer = "+buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    private void processIntent(Intent intent){
        //TODO
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        for (String tech : tagFromIntent.getTechList()){
            Log.d(TAG,"tech = "+tech);
        }
        boolean auth = false;

    }
}
