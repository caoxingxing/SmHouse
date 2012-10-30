package com.sungeo.smhouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.data.LinkInfo;
import com.sungeo.smhouse.data.MainApplication;

import java.util.ArrayList;

public class LinksListAdapter extends BaseAdapter{
    private boolean mIsLearn = false;
    private Context mContext;
    private ArrayList<LinkInfo> mLinkInfo = new ArrayList<LinkInfo>(0);
    private ArrayList<EditText> mEditTexts = new ArrayList<EditText>(0);
    
    public LinksListAdapter(Context context) {
        mContext = context;
    }
    
    public LinksListAdapter(Context context, ArrayList<LinkInfo> linkInfo) {
        mContext = context;
        mLinkInfo = linkInfo;
    }
    
    @Override
    public int getCount() {
        return mLinkInfo.size();
    }

    @Override
    public Object getItem(int position) {
        int size = mLinkInfo.size();
        if (position < 0 || position >= size) {
            return null;
        }
        
        return mLinkInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;  
        if(view==null){  
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            view=inflater.inflate(R.layout.link_list_items, null);  
        }  

        if (mLinkInfo == null) {
            return null;
        }

        TextView indexText = (TextView) view.findViewById(R.id.link_text);
        EditText indexEdit = (EditText) view.findViewById(R.id.edit_link_name);
        
        Button openBtn = (Button) view.findViewById(R.id.open_btn);
        Button closeBtn = (Button) view.findViewById(R.id.close_btn);
        openBtn.setOnClickListener(mOpenOnClickListener);
        closeBtn.setOnClickListener(mCloseOnClickListener);
        openBtn.setTag(position);
        closeBtn.setTag(position);

        if (mIsLearn) {
            openBtn.setText("学习开");
            closeBtn.setText("学习关");
            indexText.setVisibility(View.GONE);
            indexEdit.setVisibility(View.VISIBLE);
            indexEdit.setText(mLinkInfo.get(position).getmLinkName());
            //mEditTexts.add(indexEdit);
            indexEdit.setTag(position);
            addEditText(indexEdit, position);
        } else {
            openBtn.setText("打  开");
            closeBtn.setText("关  闭");
            indexText.setVisibility(View.VISIBLE);
            indexEdit.setVisibility(View.GONE);
            indexText.setText(mLinkInfo.get(position).getmLinkName());
        }
        return view;
    }

    public void setmIsLearn(boolean flag) {
        mIsLearn = flag;
        mEditTexts.clear();
        notifyDataSetChanged();
    }
    
    public ArrayList<LinkInfo> getmLinkInfo() {
        return mLinkInfo;
    }
    
    private void addEditText(EditText editText, int position) {
        boolean flag = false;
        int size = mEditTexts.size();
        for (int i = 0; i < size; i ++) {
            int tempPos = (Integer) mEditTexts.get(i).getTag();
            if (tempPos == position) {
                flag = true;
                break;
            }
        }
        
        if (!flag) {
            mEditTexts.add(editText);
        }
    }
    
    public void saveLinksName() {
        if (!mIsLearn) {
            return;
        }
        int size = mEditTexts.size();
        int len = mLinkInfo.size();
        if (size != len) {
            return;
        }
        for (int i = 0; i < size; i ++) {
            String tempName = mEditTexts.get(i).getText().toString();
            mLinkInfo.get(i).setmLinkName(tempName);
        }
    }
    
    View.OnClickListener mOpenOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            int size = mLinkInfo.size();
            if (position < 0 || position >= size) {
                return;
            }

            byte[] codeByte = mLinkInfo.get(position).getmOpenCmd();
            MainApplication.getInstance().sendCode(codeByte);
        }
    };
    
    View.OnClickListener mCloseOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            int size = mLinkInfo.size();
            if (position < 0 || position >= size) {
                return;
            }
            byte[] codeByte = mLinkInfo.get(position).getmCloseCmd();
            
            MainApplication.getInstance().sendCode(codeByte);
        }
    };
}
