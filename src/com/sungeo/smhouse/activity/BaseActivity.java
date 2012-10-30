package com.sungeo.smhouse.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.data.MainApplication;
import com.sungeo.smhouse.service.BluetoothService;
import com.sungeo.smhouse.util.MsgHandler;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseActivity extends Activity{
    public static BaseActivity sCurActivity;
    protected MainApplication mMainApp;
    protected MsgHandler mMsgHandler;
    private Toast mToast;
    protected boolean mExitFlag = false;
    protected static boolean sConfirmExit = false;
    
    public abstract void refreshUi(Message msg);// 状态同步消息传递
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainApp = MainApplication.getInstance();
        
        initMsgHandler();
        if (mMainApp.mBtService == null) {
            mMainApp.mBtService = new BluetoothService(mMsgHandler);
        } else {
            mMainApp.mBtService.setHandler(mMsgHandler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sCurActivity = this;
        sConfirmExit = false;
        cancelToast();
    }
    
    private void initMsgHandler() {
        mMsgHandler = new MsgHandler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int msgType = msg.what;
                switch (msgType) {
                    case MSG_TYPE_STR:
                        String str = (String)msg.obj;
                        showToast(str);
                        break;
                    case MSG_TYPE_START_FIND:
                        openBt();
                        break;
                    case MSG_TYPE_START_CONNECT:
                        connect();
                        break;
                    case MSG_TYPE_CONNECT_SUCESS:
                        showToast("连接成功！");
                        saveMacAddress();
                    default:
                        if (sCurActivity != null) {
                            sCurActivity.refreshUi(msg);
                        }
                        break;
                }
            }
        };
    }
    
    protected void showToast(String str) {
        if (mToast == null)
            mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        else
            mToast.setText(str);
        
        mToast.show();
    }
    
    protected void cancelToast() {
        if (mToast == null)
            return;
        mToast.cancel();
        mToast = null;
    }
    
    protected void setTitle(String title) {
        TextView tv = (TextView) findViewById(R.id.childTextViewTitle);
        if (tv != null) {
            tv.setText(title);
        }
    }
    
    public void onExitApp() {
        if (sConfirmExit == true) {
            mExitFlag = true;
            
            cancelToast();
            finish();
            if (mMainApp.mBtAdapter != null) {
                mMainApp.mBtAdapter.disable();
            }
            if (mMainApp.mBtService != null) {
                mMainApp.mBtService.stop();
            }
            mMainApp.mIsFindBt = false;
            finish();
        } else {
            startExitCheck();
        }
    }
    
    private void startExitCheck() {
        showToast("再按一次退出程序");
        
        sConfirmExit = true;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                sConfirmExit = false;
                cancelToast();
            }

        }, 3000);
    }
    
    protected void openBt() {
        if (!mMainApp.mBtAdapter.isEnabled()) {
            // 弹出对话框提示用户是后打开
            // Intent enabler = new
            // Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enabler, REQUEST_ENABLE);
            // 不做提示，强行打开
            mMainApp.mBtAdapter.enable();
        } else {
            Message msg = mMsgHandler.obtainMessage();
            msg.what = MsgHandler.MSG_TYPE_START_CONNECT;
            mMsgHandler.sendMessage(msg);
        }
    }
    
    protected void connect() {
        if (mMainApp.mBtService == null) {
            return;
        }
        mMainApp.mBtService.start();

        if (mMainApp.mBtMacAddre == null) {
            mMainApp.mBtAdapter.startDiscovery();
        } else {
            BluetoothDevice device = null;
            if (BluetoothAdapter.checkBluetoothAddress(mMainApp.mBtMacAddre)) {
                device = mMainApp.mBtAdapter.getRemoteDevice(mMainApp.mBtMacAddre);
            } else {
                showToast("无效的蓝牙地址");
                return;
            }

            if (device == null) {
                return;
            }
            mMainApp.mBtService.connect(device, false);
        }
    }
    
    protected void saveMacAddress() {
        SharedPreferences myShare = getSharedPreferences("bt_mac_file", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myShare.edit();

        editor.putString("bt_mac", mMainApp.mBtMacAddre);
        editor.commit();
    }
    
    protected void clearMacAddress() {
        SharedPreferences myShare = getSharedPreferences("bt_mac_file", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myShare.edit();
        editor.clear();
        editor.commit();
        showToast("清除成功！");
    }
    
    protected void stopService() {
        if (mMainApp.mBtService != null) {
            mMainApp.mBtService.stop();
        }
  }
}
