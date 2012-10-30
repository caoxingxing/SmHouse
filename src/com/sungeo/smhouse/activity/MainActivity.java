
package com.sungeo.smhouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.adapter.DevicesListAdapter;
import com.sungeo.smhouse.data.LinkInfo;
import com.sungeo.smhouse.service.BluetoothService;
import com.sungeo.smhouse.util.MsgHandler;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private TextView mConStaTxt;
    private ListView mListView;
    private DevicesListAdapter mDevAdapter;
    private final int MENU_ITEM_ALL_OPEN = 0;
    private final int MENU_ITEM_ALL_CLOSE = 1;
    private boolean mSendBusy = false;
    private int titleHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindowManager().getDefaultDisplay().getMetrics(mMainApp.mMetrics);
        titleHeight = (int)(50 * mMainApp.mMetrics.density);
        setTitle("设备列表");

        initSetBtn();
        mListView = (ListView) findViewById(R.id.device_listview);
        mConStaTxt = (TextView) findViewById(R.id.connect_status_text);
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataToList();
        if (mMainApp.mBtService.getState() == BluetoothService.STATE_CONNECTED) {
            mConStaTxt.setVisibility(View.GONE);
            return;
        }
        mConStaTxt.setVisibility(View.VISIBLE);
        mConStaTxt.setText(R.string.nosmartdeviceconnected);
        if (!mMainApp.mIsFindBt) {
            Message msg = mMsgHandler.obtainMessage();
            msg.what = MsgHandler.MSG_TYPE_START_FIND;
            mMsgHandler.sendMessage(msg);
            mMainApp.mIsFindBt = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onExitApp();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean ret = true;

        menu.add(0, MENU_ITEM_ALL_OPEN, MENU_ITEM_ALL_OPEN, "全开");
        menu.add(1, MENU_ITEM_ALL_CLOSE, MENU_ITEM_ALL_CLOSE, "全关");

        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        int itemId = item.getItemId();

        if (itemId == MENU_ITEM_ALL_OPEN) {
            allDevOpen();
        } else if (itemId == MENU_ITEM_ALL_CLOSE) {
            allDevClose();
        }
        return ret;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshUi(Message msg) {
        switch (msg.what) {
            case MsgHandler.MSG_TYPE_CONNECT_SUCESS:
                mConStaTxt.setText("连接成功！");
                mConStaTxt.setVisibility(View.GONE);
                RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lParams.setMargins(0, titleHeight, 0, 0);
                mListView.setLayoutParams(lParams);
                break;
            case MsgHandler.MSG_TYPE_CONNECT_FAIL:
                if (sConfirmExit) {
                    break;
                }
                mConStaTxt.setText(R.string.nosmartdeviceconnected);
                stopService();
                connect();
                break;
            case MsgHandler.MSG_TYPE_CONNECT_LOST:
                if (sConfirmExit) {
                    break;
                }
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlParams.setMargins(0, titleHeight, 0, titleHeight);
                mListView.setLayoutParams(rlParams);
                mConStaTxt.setVisibility(View.VISIBLE);
                mConStaTxt.setText(R.string.nosmartdeviceconnected);
                stopService();
                connect();
                break;
            default:
                break;
        }
    }

    private void initSetBtn() {
        Button setBtn = (Button) findViewById(R.id.childBackButton);
        setBtn.setVisibility(View.GONE);
        Button editBtn = (Button) findViewById(R.id.editdev_btn);
        editBtn.setVisibility(View.VISIBLE);
        editBtn.setText("");
        editBtn.setBackgroundResource(R.drawable.settings);
    }

    public void editDeviceInfo(View v) {
        mMainApp.mAnimation = true;
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void loadDataToList() {
        mDevAdapter = new DevicesListAdapter(this, mMainApp.mDevices);
        mListView.setAdapter(mDevAdapter);
    }

    private void showLinkUi(int index) {
        Intent intent = new Intent();
        intent.putExtra("device_position", index);
        intent.setClass(this, LinkActivity.class);
        startActivity(intent);
    }

    private void allDevOpen() {
        if (mSendBusy) {
            showToast("正在执行，请稍候……");
            return;
        }
        int counter = 0;
        long delay = 0;
        LinkInfo tempLl = null;
        ArrayList<LinkInfo> ll = null;
        int size = mMainApp.mDevices.size();
        for (int i = 0; i < size; i++) {
            ll = mMainApp.mDevices.get(i).getmLinks();
            final int len = ll.size();
            if (len > 0) {
                mSendBusy = true;
            }
            for (int j = 0; j < len; j++) {
                tempLl = ll.get(j);
                final int tempIndex = j;
                final byte[] cmd = tempLl.getmOpenCmd();
                delay = mMainApp.mDelay * counter;

                mMsgHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mMainApp.mBtService.write(cmd);
                        if (tempIndex == (len - 1)) {
                            mSendBusy = false;
                        }
                    }
                }, delay);
                counter ++;
                tempLl = null;
            }
            ll = null;
        }
    }

    private void allDevClose() {
        if (mSendBusy) {
            showToast("正在执行，请稍候……");
            return;
        }
        int counter = 0;
        long delay = 0;
        LinkInfo tempLl = null;
        ArrayList<LinkInfo> ll = null;
        int size = mMainApp.mDevices.size();
        for (int i = 0; i < size; i++) {
            ll = mMainApp.mDevices.get(i).getmLinks();
            final int len = ll.size();
            if (len > 0) {
                mSendBusy = true;
            }
            for (int j = 0; j < len; j++) {
                tempLl = ll.get(j);
                final int tempIndex = j;
                final byte[] cmd = tempLl.getmCloseCmd();

                delay = mMainApp.mDelay * counter;

                mMsgHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mMainApp.mBtService.write(cmd);
                        if (tempIndex == (len - 1)) {
                            mSendBusy = false;
                        }
                    }
                }, delay);
                counter ++;
                tempLl = null;
            }
            ll = null;
        }
    }

    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showLinkUi(position);
        }
    };
}
