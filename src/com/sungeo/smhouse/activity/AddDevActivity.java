package com.sungeo.smhouse.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.data.DevicesInfo;
import com.sungeo.smhouse.data.LinkInfo;


public class AddDevActivity extends BaseActivity{
    private EditText mDevNameEdit;
    private Button mOkBtn;
    private RadioGroup mRadioGroup;
    private int mLinkNum = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddev_layout);
        setTitle("添加设备");
        
        initAllBtn();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            saveDevInfo();
            return true;
        }
        return false;
    }
    
    @Override
    public void refreshUi(Message msg) {
        
    }

    public void onClickBack(View v) {
        saveDevInfo();
    }
    
    public void onClickSave(View v) {
        saveDevInfo();
    }
    
    private void saveDevInfo() {
        if (!mOkBtn.isEnabled()) {
            finish();
            return;
        }
        String name = mDevNameEdit.getText().toString();
        if (name.contains(" ") || name.equals("")) {
            showToast("名称不合法");
            return;
        }
        DevicesInfo dev = new DevicesInfo();
        dev.setmDevName(name);
        int size = mMainApp.mDevices.size();
        for (int i = 0; i < mLinkNum; i ++) {
            LinkInfo ll = new LinkInfo();
            ll.setmLinkIndex(i + 1);
            ll.setmLinkName((i + 1) + "联");
            
            ll.setmOpenCmd(mMainApp.generateCode((byte)(i + 1), (byte)1, (byte)size));
            ll.setmCloseCmd(mMainApp.generateCode((byte)(i + 1), (byte)0, (byte)size));
            dev.getmLinks().add(ll);
        }
        mMainApp.mDevices.add(dev);
        mMainApp.createXmlFile();
        showToast("保存成功");
        finish();
    }
    
    private void initAllBtn() {
        mDevNameEdit = (EditText) findViewById(R.id.editTxt_devName);
        mDevNameEdit.addTextChangedListener(mTextChanged);
        
        mOkBtn = (Button) findViewById(R.id.ok_btn);
        mOkBtn.setEnabled(false);
        
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mRadioGroup.setOnCheckedChangeListener(mOnCheChaLis);
    }
    
    private OnCheckedChangeListener mOnCheChaLis = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radioBtn_one:
                    mLinkNum = 1;
                    break;
                case R.id.radioBtn_two:
                    mLinkNum = 2;
                    break;
                case R.id.radioBtn_three:
                    mLinkNum = 3;
                    break;
                    default:
                        break;
            }
        }
        
    };
    
    private TextWatcher mTextChanged = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
               mOkBtn.setEnabled(false);
            } else {
               mOkBtn.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        }
    };
}
