package com.sungeo.smhouse.data;

public class LinkInfo {
    private String mLinkName;
    private int mLinkIndex;
    private byte[] mOpenCmd;
    private byte[] mCloseCmd;
    public void setmLinkName(String mLinkName) {
        this.mLinkName = mLinkName;
    }
    public String getmLinkName() {
        return mLinkName;
    }
    public void setmLinkIndex(int mLinkIndex) {
        this.mLinkIndex = mLinkIndex;
    }
    public int getmLinkIndex() {
        return mLinkIndex;
    }
    public void setmOpenCmd(byte[] mOpenCmd) {
        this.mOpenCmd = mOpenCmd;
    }
    public byte[] getmOpenCmd() {
        return mOpenCmd;
    }
    public void setmCloseCmd(byte[] mCloseCmd) {
        this.mCloseCmd = mCloseCmd;
    }
    public byte[] getmCloseCmd() {
        return mCloseCmd;
    }
    public void clear() {
        mLinkName = null;
        mLinkIndex = 0;
        mOpenCmd = null;
        mCloseCmd = null;
    }
}
