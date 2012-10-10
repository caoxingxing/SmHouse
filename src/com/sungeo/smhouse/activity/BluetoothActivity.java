package com.sungeo.smhouse.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.service.BluetoothService;
import com.sungeo.smhouse.util.MsgHandler;

public class BluetoothActivity extends BaseActivity{
    private ListView mListView;
    private Button mCancelBtn;
    private ArrayAdapter<String> mDevicesAdapter;
    
    private final int RECONNECT_COUNT = 10;
    private int mReConnectCounter = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);
        
        setTitle("蓝牙设备");
        initMacAddress();
        if (mMainApp.mBtMacAddre != null) {
            showDevicesList();
        }
        
        mListView = (ListView) findViewById(R.id.bluetooth_listview);
        mDevicesAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mListView.setAdapter(mDevicesAdapter);
        
        mListView.setOnItemClickListener(mOnItemClickListener);
        
        initBackBtn();
        mCancelBtn = (Button) findViewById(R.id.cancel_btn);
        mCancelBtn.setOnClickListener(mOnClickListener);
        
        registerReceiver();

        mMainApp.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    @Override
    public void onResume() {
        super.onResume();

        if (mMainApp.mBtService.getState() == BluetoothService.STATE_CONNECTED) {
            return;
        }
        Message msg = mMsgHandler.obtainMessage();
        msg.what = MsgHandler.MSG_TYPE_START_FIND;
        mMsgHandler.sendMessage(msg);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            cancelToast();
            if (mMainApp.mBtAdapter != null) {
                mMainApp.mBtAdapter.disable();
            }
            if (mMainApp.mBtService != null) {
                mMainApp.mBtService.stop();
            }
            
            finish();
            return true;
        }
        return false;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        deconnectBt();
    }
    
    @Override
    public void onDestroy() {
        cancelToast();
        super.onDestroy();
    }
    
    private void initBackBtn() {
        Button btn = (Button) findViewById(R.id.childBackButton);
        btn.setVisibility(View.GONE);
    }
    
    private void showDevicesList() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void deconnectBt() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        mReceiver = null;
    }

    private int getReConnectCounter() {
        return mReConnectCounter;
    }
    
    private void clearReConnectCounter() {
        mReConnectCounter = 0;
    }
    
    private void setReConnectCounter() {
        mReConnectCounter ++;
    }
    
    private int getReConnectNum() {
        return RECONNECT_COUNT;
    }

    private void initMacAddress() {
        SharedPreferences sharedata = getSharedPreferences("bt_mac_file", Activity.MODE_PRIVATE);

        mMainApp.mBtMacAddre = sharedata.getString("bt_mac", null);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
           deconnectBt();
           showDevicesList();
        }
    };
    
    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mMainApp.mBtAdapter.cancelDiscovery();
            int len = 0;
            if (mDevicesAdapter == null)
                return;
            len = mDevicesAdapter.getCount();
            if (position < 0 || position >= len)
                return;

            String tempStr = mDevicesAdapter.getItem(position);
            String[] tmp = tempStr.split("\n");
            int length = 0;
            if (tmp == null) {
                return;
            }
            length = tmp.length;
            if (length != 2) {
                return;
            }
            mMainApp.mBtMacAddre = tmp[1];
            connect();
        }
    };

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // 找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device == null)
                    return;
                
                Log.v("sungeobt", "find device:" + device.getName()
                            + device.getAddress());
                mDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                showToast("发现蓝牙设备");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {// 搜索完成
                setTitle("搜索完成");
                showToast("搜索完成");
                if (mDevicesAdapter.getCount() == 0) {
                    showToast("没有找到蓝牙设备");
                    mMsgHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            showDevicesList();
                        }}, 2000);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                showToast("绑定状态改变");
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                if (mode != BluetoothAdapter.SCAN_MODE_NONE) {
                    
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("蓝牙打开成功！");
                    connect();
                }
            }
        }
    };

    @Override
    public void refreshUi(Message msg) {
        switch (msg.what) {
            case MsgHandler.MSG_TYPE_CONNECT_SUCESS:
                showDevicesList();
                break;
            case MsgHandler.MSG_TYPE_CONNECT_FAIL:
                if (getReConnectCounter() >= getReConnectNum()) {
                    showToast("连接失败，请退出程序重新连接");
                    clearReConnectCounter();
                    return;
                } else {
                    setReConnectCounter();
                }
                showToast("连接失败，重新连接！");
                stopService();
                connect();
                break;
            default:
                break;
        }
    }
}
