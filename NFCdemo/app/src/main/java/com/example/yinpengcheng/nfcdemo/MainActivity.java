package com.example.yinpengcheng.nfcdemo;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    private static final String TAG = "MainActivity";
    NdefMessage[] msgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        textView.setText("onCreate");
        Toast.makeText(MainActivity.this,"onCreate",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG,"newIntent");
        Toast.makeText(MainActivity.this,"onNewIntent",Toast.LENGTH_SHORT).show();
        if (intent != null && intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)){
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                Log.i(TAG,""+rawMsgs.length);
                textView.setText(""+rawMsgs.length);
                for (int i = 0; i < rawMsgs.length; i++){
                    msgs[i] = (NdefMessage)rawMsgs[i];
                }
            }
        }
    }
}
