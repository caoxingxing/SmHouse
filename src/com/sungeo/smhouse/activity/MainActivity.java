
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

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.adapter.DevicesListAdapter;
import com.sungeo.smhouse.data.LinkInfo;
import com.sungeo.smhouse.service.BluetoothService;
import com.sungeo.smhouse.util.AnimCommon;
import com.sungeo.smhouse.util.MsgHandler;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private ListView mListView;
    private DevicesListAdapter mDevAdapter;
    private final int MENU_ITEM_ALL_OPEN = 0;
    private final int MENU_ITEM_ALL_CLOSE = 1;
    private final int MENU_ITEM_ABOUT = 3;
    private boolean mSendBusy = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("设备列表");

        initSetBtn();
        mListView = (ListView) findViewById(R.id.device_listview);

        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataToList();
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
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean ret = true;

        menu.add(0, MENU_ITEM_ALL_OPEN, MENU_ITEM_ALL_OPEN, "全开");
        menu.add(1, MENU_ITEM_ALL_CLOSE, MENU_ITEM_ALL_CLOSE, "全关");
        menu.add(2, MENU_ITEM_ABOUT, MENU_ITEM_ABOUT, "重新连接");

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
        } else if (itemId == MENU_ITEM_ABOUT) {
            Message msg = mMsgHandler.obtainMessage();
            msg.what = MsgHandler.MSG_TYPE_START_FIND;
            mMsgHandler.sendMessage(msg);
        }
        return ret;
    }

    @Override
    public void onPause() {
        boolean flag = mMainApp.mAnimation;
        if (flag) {
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.DONUT) {
                (new AnimCommon(this)).overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            mMainApp.mAnimation = false;
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        cancelToast();
        mMainApp.mBtAdapter.disable();
        mMainApp.mBtService.stop();
        super.onDestroy();
    }

    @Override
    public void refreshUi(Message msg) {
    }

    private void initSetBtn() {
        Button setBtn = (Button) findViewById(R.id.childBackButton);
        setBtn.setBackgroundResource(R.drawable.title_edit_btn);
        setBtn.setTextColor(0xff000000);
        setBtn.setEnabled(true);
        setBtn.setText("设置");
    }

    public void onClickBack(View v) {
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
