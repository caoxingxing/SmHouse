package com.sungeo.smhouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.adapter.LinksListAdapter;
import com.sungeo.smhouse.data.LinkInfo;

import java.util.ArrayList;

public class LinkActivity extends BaseActivity{
    private Button mAllOpenBtn, mAllCloseBtn;
    private ListView mLinkListView;
    private LinksListAdapter mLinkAdapter;
    private int mPosition;
    private boolean mIsBusy = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_layout);
        
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("device_position", -1);
        mLinkListView = (ListView) findViewById(R.id.link_ctrl_listview);
        mAllOpenBtn = (Button) findViewById(R.id.open_all_btn);
        mAllCloseBtn = (Button) findViewById(R.id.close_all_btn);
        mAllOpenBtn.setOnClickListener(mOnClickListener);
        mAllCloseBtn.setOnClickListener(mOnClickListener);
        
        loadDataToList();
        int size = mMainApp.mDevices.size();
        if (mPosition < 0 || mPosition >= size) {
            return;
        }
        setTitle(mMainApp.mDevices.get(mPosition).getmDevName());
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
    public void refreshUi(Message msg) {
       
    }

    public void onClickBack(View v) {
        finish();
    }
    
    private void loadDataToList() {
        if (mPosition < 0) {
            return;
        }
        
        int size = mMainApp.mDevices.size();
        if (mPosition >= size) {
            return;
        }
        
        ArrayList<LinkInfo> ll = mMainApp.mDevices.get(mPosition).getmLinks();
        mLinkAdapter = new LinksListAdapter(this, ll);
        mLinkListView.setAdapter(mLinkAdapter);
    }
    
    private void allOpen() {
        if (mIsBusy) {
            showToast("正在执行，请稍候……");
            return;
        }
        ArrayList<LinkInfo> ll = null;
        int size = mMainApp.mDevices.size();
        if (mPosition < 0 || mPosition >= size) {
            return;
        }
        
        ll = mMainApp.mDevices.get(mPosition).getmLinks();
        final int len = ll.size();
        LinkInfo tempLl = null;
        long delay = 0;
        if (len > 0) {
            mIsBusy = true;
        }
        for (int i = 0; i < len; i ++) {
            tempLl = ll.get(i);
            final int tempIndex = i;
            final byte[] cmd = tempLl.getmOpenCmd();
            delay = mMainApp.mDelay*i;
            mMsgHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mMainApp.mBtService.write(cmd);
                    if (tempIndex == (len -1)) {
                        mIsBusy = false;
                    }
                }}, delay);
            tempLl = null;
        }
    }
    
    private void allClose() {
        if (mIsBusy) {
            showToast("正在执行，请稍候……");
            return;
        }
        ArrayList<LinkInfo> ll = null;
        int size = mMainApp.mDevices.size();
        if (mPosition < 0 || mPosition >= size) {
            return;
        }
        
        ll = mMainApp.mDevices.get(mPosition).getmLinks();
        if (ll == null) {
            return;
        }
        
        final int len = ll.size();
        LinkInfo tempLl = null;
        long delay = 0;
        
        if (len > 0) {
            mIsBusy = true;
        }
        
        for (int i = 0; i < len; i ++) {
            tempLl = ll.get(i);
            final int tempIndex = i;
            final byte[] cmd = tempLl.getmCloseCmd();
            delay = mMainApp.mDelay*i;
            mMsgHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mMainApp.mBtService.write(cmd);
                    if (tempIndex == (len -1)) {
                        mIsBusy = false;
                    }
                }}, delay);
            tempLl = null;
        }
    }
    
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            if (btn == mAllOpenBtn) {
                allOpen();
            } else if (btn == mAllCloseBtn) {
                allClose();
            }
        }
    };
}
