package com.sungeo.smhouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.adapter.LinksListAdapter;
import com.sungeo.smhouse.data.LinkInfo;

import java.util.ArrayList;

public class SetLinkActivity extends BaseActivity{
    private ListView mSetLinkList;
    private LinksListAdapter mLinkAdapter;
    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setlink_layout);
        setTitle("学习");
        
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("device_index", -1);
        mSetLinkList = (ListView) findViewById(R.id.setlink_listview);
        
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
            saveLinksName();
            return true;
        }
        return false;
    }
    
    @Override
    public void refreshUi(Message msg) {
    }

    public void onClickBack(View v) {
        saveLinksName();
    }
    
    private void saveLinksName() {
        mLinkAdapter.saveLinksName();
        mMainApp.mDevices.get(mPosition).setmLinks(mLinkAdapter.getmLinkInfo());
        mMainApp.createXmlFile();
        showToast("保存成功！");
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
        mLinkAdapter.setmIsLearn(true);
        mSetLinkList.setAdapter(mLinkAdapter);
    }
}
