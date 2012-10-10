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

public abstract class BaseActivity extends Activity{
    protected MainApplication mMainApp;
    protected MsgHandler mMsgHandler;
    private Toast mToast;
    
    public abstract void refreshUi(Message msg);// ״̬ͬ����Ϣ����
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainApp = MainApplication.getInstance();
        
        initMsgHandler();
        if (mMainApp.mBtService == null) {
            mMainApp.mBtService = new BluetoothService(mMsgHandler);
        }
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
                        showToast("���ӳɹ���");
                        saveMacAddress();
                    default:
                        refreshUi(msg);
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
    
    protected void openBt() {
        if (!mMainApp.mBtAdapter.isEnabled()) {
            // �����Ի�����ʾ�û��Ǻ��
            // Intent enabler = new
            // Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enabler, REQUEST_ENABLE);
            // ������ʾ��ǿ�д�
            mMainApp.mBtAdapter.enable();
        } else {
            showToast("�����Ѵ��ڿ���״̬��");
            Message msg = mMsgHandler.obtainMessage();
            msg.what = MsgHandler.MSG_TYPE_START_CONNECT;
            mMsgHandler.sendMessage(msg);
        }
    }
    
    protected void connect() {
        mMainApp.mBtService.start();

        if (mMainApp.mBtMacAddre == null) {
            boolean flag = mMainApp.mBtAdapter.startDiscovery();
            if (flag) {
                showToast("��ʼ�����豸����");
            } else {
                showToast("��ʼ����ʧ��");
            }   
        } else {
            BluetoothDevice device = null;
            if (BluetoothAdapter.checkBluetoothAddress(mMainApp.mBtMacAddre)) {
                device = mMainApp.mBtAdapter.getRemoteDevice(mMainApp.mBtMacAddre);
            } else {
                showToast("��Ч��������ַ");
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
        showToast("����ɹ���");
    }
    
    protected void stopService() {
        mMainApp.mBtService.stop();
  }
}
