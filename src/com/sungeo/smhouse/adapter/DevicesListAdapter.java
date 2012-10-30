package com.sungeo.smhouse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sungeo.smhouse.R;
import com.sungeo.smhouse.data.DevicesInfo;
import com.sungeo.smhouse.data.MainApplication;

import java.util.ArrayList;

public class DevicesListAdapter extends BaseAdapter{
    private boolean mIsEditName;
    private boolean mIsDelete;
    private int mSelecteItem;
    private ArrayList<DevicesInfo> mDevInfo = new ArrayList<DevicesInfo>(0);
    private Context mContext;
    public DevicesListAdapter(Context context, ArrayList<DevicesInfo> objects) {
        mContext = context;
        mDevInfo = objects;
        mIsEditName = false;
    }
    
    public DevicesListAdapter(Context context, ArrayList<DevicesInfo> objects, boolean edit) {
        mContext = context;
        mDevInfo = objects;
        mIsEditName = edit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
        View view=convertView;  
        if(view==null){  
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            view=inflater.inflate(R.layout.array_list_item, null);  
        }  

        if (mDevInfo == null) {
            return null;
        }

        TextView indexText = (TextView) view.findViewById(R.id.index_text);
        indexText.setText((position + 1) + ". ");
        
        TextView elementText = (TextView) view.findViewById(R.id.element_text);
        EditText editText = (EditText) view.findViewById(R.id.edit_dev_name);
        
        if (mIsEditName) {
            editText.setVisibility(View.VISIBLE);
            elementText.setVisibility(View.GONE);
            editText.setText(mDevInfo.get(position).getmDevName());
        } else {
            editText.setVisibility(View.GONE);
            elementText.setVisibility(View.VISIBLE);
            elementText.setText(mDevInfo.get(position).getmDevName());
        }
        
        TextView secEleText = (TextView) view.findViewById(R.id.album_text);
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(mDevInfo.get(position).getmLinks().size());
        strBuf.append("Áª");
        secEleText.setText(strBuf.toString());
        
        ImageView arrowImg = (ImageView) view.findViewById(R.id.arrow_img);
        Button delBtn = (Button) view.findViewById(R.id.dev_del_btn);
        if (ismIsDelete()) {
            arrowImg.setVisibility(View.GONE);
            secEleText.setVisibility(View.GONE);
            delBtn.setVisibility(View.VISIBLE);
            delBtn.setTag(position);
            delBtn.setOnClickListener(mOnClickListener);
        } else {
            arrowImg.setVisibility(View.VISIBLE);
            secEleText.setVisibility(View.VISIBLE);
            delBtn.setVisibility(View.GONE);
        }

        FrameLayout layout = (FrameLayout) view.findViewById(R.id.array_item_layout);
        if (mSelecteItem == position) {
            layout.setBackgroundColor(0xffffcc00);
        } else {
            layout.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    public void setSelected(int selected) {
        this.mSelecteItem = selected;
    }
    public int getSelected() {
        return mSelecteItem;
    }

    @Override
    public int getCount() {
        if (mDevInfo == null) {
            return 0;
        }
        return mDevInfo.size();
    }

    @Override
    public Object getItem(int position) {
        if (mDevInfo == null) {
            return null;
        }
        
        return mDevInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    
    public void setmIsDelete(boolean mIsDelete) {
        this.mIsDelete = mIsDelete;
        notifyDataSetChanged();
    }

    public boolean ismIsDelete() {
        return mIsDelete;
    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            int size = mDevInfo.size();
            if (position < 0 || position >= size) {
                return;
            }
            
            mDevInfo.remove(position);
            MainApplication.getInstance().deleteRecordFromXmlFile(position);
            notifyDataSetChanged();
        }
    };
}
