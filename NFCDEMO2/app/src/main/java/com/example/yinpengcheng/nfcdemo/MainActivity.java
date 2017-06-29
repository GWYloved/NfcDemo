package com.example.yinpengcheng.nfcdemo;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.IBinder;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    TextView promt;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"onCreate");
        promt = (TextView)findViewById(R.id.promt);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    @Override
    protected void onResume() {
        Log.i(TAG,"onResume");
        super.onResume();
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
        if ( NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())){
            processIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG,"onNewIntent");
        Toast.makeText(MainActivity.this, "onNewIntent + intent = "+intent,Toast.LENGTH_SHORT).show();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
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
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null){
            Log.e(TAG,"no tag!!!!");
            return;
        }
        String[] techList = tag.getTechList();
        Parcel oldParcel = Parcel.obtain();
        tag.writeToParcel(oldParcel, 0);
        int len = oldParcel.readInt();
        byte[] id = new byte[0];
        if (len >= 0){
            id = new byte[len];
            oldParcel.readByteArray(id);
        }
        int[] oldTechList = new int[oldParcel.readInt()];
        oldParcel.readIntArray(oldTechList);
        Bundle[] oldTechExtras = oldParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oldParcel.readInt();
        int isMock = oldParcel.readInt();
        IBinder tagService;
        if (isMock == 0){
            tagService = oldParcel.readStrongBinder();
        }else {
            tagService = null;
        }
        oldParcel.recycle();
        int nfcaIdx = -1;
        int mcIdx = -1;
        short sak = 0;
        boolean isFirstSak = true;

        for (int i = 0; i < techList.length; i++) {
            if (techList[i].equals(NfcA.class.getName())) {
                if (nfcaIdx == -1) {
                    nfcaIdx = i;
                }
                if (oldTechExtras[i] != null
                        && oldTechExtras[i].containsKey("sak")) {
                    sak = (short) (sak
                            | oldTechExtras[i].getShort("sak"));
                    isFirstSak = (nfcaIdx == i) ? true : false;
                }
            } else if (techList[i].equals(MifareClassic.class.getName())) {
                mcIdx = i;
            }
        }

        boolean modified = false;

        // Patch the double NfcA issue (with different SAK) for
        // Sony Z3 devices.
        if (!isFirstSak) {
            oldTechExtras[nfcaIdx].putShort("sak", sak);
            modified = true;
        }

        // Patch the wrong index issue for HTC One devices.
        if (nfcaIdx != -1 && mcIdx != -1 && oldTechExtras[mcIdx] == null) {
            oldTechExtras[mcIdx] = oldTechExtras[nfcaIdx];
            modified = true;
        }


    }
}
