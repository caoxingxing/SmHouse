
package com.sungeo.smhouse.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sungeo.smhouse.R;

public class SettingActivity extends BaseActivity {
    private TextView mDevNumTxt;
    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        mDevNumTxt = (TextView) findViewById(R.id.txtDeviceNum);
        Button cbb = (Button) findViewById(R.id.childBackButton);
        cbb.setText("");
        cbb.setBackgroundResource(R.drawable.home);
        
        setTitle("设置");
    }

    @Override
    public void onResume() {
        super.onResume();
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("共");
        strBuf.append(mMainApp.mDevices.size());
        strBuf.append("个设备");
        mDevNumTxt.setText(strBuf.toString());
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
        /*boolean flag = mMainApp.mAnimation;
        if (flag) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimCommon(this)).overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
            mMainApp.mAnimation = false;
        }*/

        super.onPause();
    }

    @Override
    public void refreshUi(Message msg) {

    }

    public void onClickBack(View v) {
        mMainApp.mAnimation = true;
        finish();
    }

    public void onClickSetDevice(View v) {
        Intent intent = new Intent();
        intent.setClass(this, DeviceMgrActivity.class);
        startActivity(intent);
    }

    public void onClickDelMac(View v) {
        showAlarmBox();
    }

    public void onClickAddDevice(View v) {
        Intent intent = new Intent();
        intent.setClass(this, AddDevActivity.class);
        startActivity(intent);
    }

    private void showAlarmBox() {
        if (mDialog != null && !mDialog.isShowing() || mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("清除蓝牙地址").setMessage("确认清除？");
            
            builder.setPositiveButton("确定", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearMacAddress();
                }
            });
            // 关闭对话框
            builder.setNeutralButton("取消", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            mDialog = builder.create();
        }

        mDialog.show();
    }
}
