package com.sungeo.smhouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.adapter.DevicesListAdapter;

public class DeviceMgrActivity extends BaseActivity{
    private Button mEditDelBtn;
    private ListView mDevMgrList;
    private DevicesListAdapter mDevAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devmgr_layout);
        setTitle("设备管理");
        
        mDevMgrList = (ListView) findViewById(R.id.devmgr_listview);
        
        mDevMgrList.setOnItemClickListener(mOnItemClickListener);
        loadDataToList();
        initEditBtn();
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
    
    public void editDeviceInfo(View v) {
        if (mDevAdapter == null) {
            return;
        }
        
        boolean flag = mDevAdapter.ismIsDelete();
        if (flag) {
            mEditDelBtn.setText("编辑");
        } else {
            mEditDelBtn.setText("取消");
        }
        
        mDevAdapter.setmIsDelete(!flag);
    }
    
    private void initEditBtn() {
        mEditDelBtn = (Button) findViewById(R.id.editdev_btn);
        mEditDelBtn.setVisibility(View.VISIBLE);
    }
    
    private void loadDataToList() {
        mDevAdapter = new DevicesListAdapter(this, mMainApp.mDevices);
        mDevMgrList.setAdapter(mDevAdapter);
    }
    
    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Intent intent = new Intent();
            intent.putExtra("device_index", position);
            intent.setClass(DeviceMgrActivity.this, SetLinkActivity.class);
            startActivity(intent);
        }
    };
}
