package com.sungeo.smhouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.util.AnimCommon;

public class SettingActivity extends BaseActivity{
    private TextView mDevNumTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        mDevNumTxt = (TextView) findViewById(R.id.txtDeviceNum);
        Button cbb = (Button)findViewById(R.id.childBackButton);
        cbb.setVisibility(View.GONE);
        Button ebtn = (Button)findViewById(R.id.editdev_btn);
        ebtn.setVisibility(View.VISIBLE);
        ebtn.setText("Ö÷Ò³");
        setTitle("ÉèÖÃ");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mDevNumTxt.setText(String.valueOf(mMainApp.mDevices.size()));
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        boolean flag = mMainApp.mAnimation;
        if (flag) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {  
                (new AnimCommon(this)).overridePendingTransition(R.anim.left_in,  R.anim.left_out);  
            }  
            mMainApp.mAnimation = false;
        }

        super.onPause();
    }
    
    @Override
    public void refreshUi(Message msg) {
  
    }
    
    public void editDeviceInfo(View v) {
        mMainApp.mAnimation = true;
        finish();
    }
    
    public void onClickSetDevice(View v) {
        Intent intent = new Intent();
        intent.setClass(this, DeviceMgrActivity.class);
        startActivity(intent);
    }
    
    public void onClickDelMac(View v) {
        clearMacAddress();
    }
    
    public void onClickAddDevice(View v) {
        Intent intent = new Intent();
        intent.setClass(this, AddDevActivity.class);
        startActivity(intent);
    }
    
}
