
package com.sungeo.smhouse.data;

import java.util.ArrayList;


public class DevicesInfo {
    private String mDevName;
    private ArrayList<LinkInfo> mLinks = new ArrayList<LinkInfo>(0);

    public void setmDevName(String mDevName) {
        this.mDevName = mDevName;
    }

    public String getmDevName() {
        return mDevName;
    }

    public void setmLinks(ArrayList<LinkInfo> mLinks) {
        this.mLinks = mLinks;
    }

    public ArrayList<LinkInfo> getmLinks() {
        return mLinks;
    }
    
    public void clear() {
        mDevName = null;
        mLinks.clear();
    }
}
