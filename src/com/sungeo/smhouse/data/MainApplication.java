
package com.sungeo.smhouse.data;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;

import com.sungeo.smhouse.service.BluetoothService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainApplication extends Application {
    private static final String XML_TAG_DEVICES = "SungeoDevices";
    private static final String XML_TAG_DEV_NAME = "DeviceName";
    private static final String XML_TAG_LINK = "LinkDevice";
    private static final String XML_TAG_LINK_NAME = "LinkName";
    private static final String XML_TAG_OPEN_CMD = "OpenCmd";
    private static final String XML_TAG_FIRST_BYTE = "FirstByte";
    private static final String XML_TAG_TWO_BYTE = "TwoByte";
    private static final String XML_TAG_THREE_BYTE = "ThreeByte";
    private static final String XML_TAG_FOUR_BYTE = "FourByte";
    private static final String XML_TAG_FIVE_BYTE = "FiveByte";
    private static final String XML_TAG_SIX_BYTE = "SixByte";
    private static final String XML_TAG_CLOSE_CMD = "CloseCmd";

    public final long mDelay = 500;
    private static final int CODE_LEN = 3;
    private static MainApplication instance;
    public DisplayMetrics mMetrics;
    public BluetoothService mBtService = null;
    public BluetoothAdapter mBtAdapter = null;
    public String mBtMacAddre = null;
    public boolean mAnimation = false;
    public boolean mIsFindBt = false;
    public ArrayList<DevicesInfo> mDevices = new ArrayList<DevicesInfo>(0);
    private static final String DEVICES_INFO_PATH = Environment.getExternalStorageDirectory()
            .getPath()
            + "/SungeoData/sungeo_devices.xml";

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mMetrics = new DisplayMetrics();
        xmlPullParseXML();
    }

    public void addRecordToXmlFile(DevicesInfo dev) {
        if (dev == null) {
            return;
        }
        File linceseFile = new File(DEVICES_INFO_PATH);
        boolean flag = false;
        boolean exist = linceseFile.exists();
        if (!exist) {
            try {
                flag = linceseFile.createNewFile();
            } catch (IOException e) {
                Log.e("IOException", "exception in createNewFile() method");
            }
        }

        if (!flag) {
            return;
        }

        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(linceseFile);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "can't create FileOutputStream");
        }

        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileos, "UTF-8");
            if (!exist) {
                serializer.startDocument(null, true);
            }

            serializer.startTag(null, XML_TAG_DEVICES);

            serializer.startTag(null, XML_TAG_DEV_NAME);
            serializer.text(dev.getmDevName());
            serializer.endTag(null, XML_TAG_DEV_NAME);

            ArrayList<LinkInfo> tempL = dev.getmLinks();
            int linkSize = tempL.size();
            for (int i = 0; i < linkSize; i++) {
                serializer.startTag(null, XML_TAG_LINK);

                serializer.startTag(null, XML_TAG_LINK_NAME);
                serializer.text(tempL.get(i).getmLinkName());
                serializer.endTag(null, XML_TAG_LINK_NAME);

                serializer.startTag(null, XML_TAG_OPEN_CMD);
                
                serializer.startTag(null, XML_TAG_FIRST_BYTE);
                serializer.endTag(null, XML_TAG_FIRST_BYTE);
                
                serializer.startTag(null, XML_TAG_TWO_BYTE);
                serializer.endTag(null, XML_TAG_TWO_BYTE);
                
                serializer.startTag(null, XML_TAG_THREE_BYTE);
                serializer.endTag(null, XML_TAG_THREE_BYTE);
                
                serializer.startTag(null, XML_TAG_FOUR_BYTE);
                serializer.endTag(null, XML_TAG_FOUR_BYTE);
                
                serializer.startTag(null, XML_TAG_FIVE_BYTE);
                serializer.endTag(null, XML_TAG_FIVE_BYTE);
                
                serializer.startTag(null, XML_TAG_SIX_BYTE);
                serializer.endTag(null, XML_TAG_SIX_BYTE);
                
                serializer.endTag(null, XML_TAG_OPEN_CMD);

                serializer.startTag(null, XML_TAG_CLOSE_CMD);
                serializer.startTag(null, XML_TAG_FIRST_BYTE);
                serializer.endTag(null, XML_TAG_FIRST_BYTE);
                
                serializer.startTag(null, XML_TAG_TWO_BYTE);
                serializer.endTag(null, XML_TAG_TWO_BYTE);
                
                serializer.startTag(null, XML_TAG_THREE_BYTE);
                serializer.endTag(null, XML_TAG_THREE_BYTE);
                
                serializer.startTag(null, XML_TAG_FOUR_BYTE);
                serializer.endTag(null, XML_TAG_FOUR_BYTE);
                
                serializer.startTag(null, XML_TAG_FIVE_BYTE);
                serializer.endTag(null, XML_TAG_FIVE_BYTE);
                
                serializer.startTag(null, XML_TAG_SIX_BYTE);
                serializer.endTag(null, XML_TAG_SIX_BYTE);
                serializer.endTag(null, XML_TAG_CLOSE_CMD);

                serializer.endTag(null, XML_TAG_LINK);
            }
            serializer.endTag(null, XML_TAG_DEVICES);

            if (!exist) {
                serializer.endDocument();
            }

            serializer.flush();
            fileos.close();
        } catch (Exception e) {
            Log.e("Exception", "error occurred while creating xml file");
        }
    }

    public void deleteRecordFromXmlFile(int index) {
        if (mDevices.size() == 0) {
            File linceseFile = new File(DEVICES_INFO_PATH);
            boolean exist = linceseFile.exists();
            if (exist) {
                linceseFile.delete();
            }
            return;
        }
        createXmlFile();
    }

    // 创建xml文件
    public void createXmlFile() {
        File linceseFile = new File(DEVICES_INFO_PATH);
        boolean flag = false;
        boolean exist = linceseFile.exists();
        if (exist) {
            flag = linceseFile.delete();
        }
        if (exist && !flag) {
            return;
        }

        if (!linceseFile.getParentFile().exists()) {
            linceseFile.getParentFile().mkdirs();// 创建父文件夹路径
        }

        try {
            linceseFile.createNewFile();
        } catch (IOException e) {
            Log.e("IOException", "exception in createNewFile() method");
        }
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(linceseFile);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "can't create FileOutputStream");
        }

        int devSize = mDevices.size();

        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fileos, "UTF-8");
            serializer.startDocument(null, true);
            for (int i = 0; i < devSize; i++) {
                serializer.startTag(null, XML_TAG_DEVICES);

                serializer.startTag(null, XML_TAG_DEV_NAME);
                serializer.text(mDevices.get(i).getmDevName());
                serializer.endTag(null, XML_TAG_DEV_NAME);

                ArrayList<LinkInfo> tempL = mDevices.get(i).getmLinks();
                int linkSize = tempL.size();
                for (int j = 0; j < linkSize; j++) {
                    serializer.startTag(null, XML_TAG_LINK);

                    byte[] ocmd = tempL.get(j).getmOpenCmd();
                    byte[] ccmd = tempL.get(j).getmCloseCmd();
                    serializer.startTag(null, XML_TAG_LINK_NAME);
                    serializer.text(tempL.get(j).getmLinkName());
                    serializer.endTag(null, XML_TAG_LINK_NAME);

                    serializer.startTag(null, XML_TAG_OPEN_CMD);
                    
                    serializer.startTag(null, XML_TAG_FIRST_BYTE);
                    serializer.text(String.valueOf(ocmd[0]));
                    serializer.endTag(null, XML_TAG_FIRST_BYTE);
                    
                    serializer.startTag(null, XML_TAG_TWO_BYTE);
                    serializer.text(String.valueOf(ocmd[1]));
                    serializer.endTag(null, XML_TAG_TWO_BYTE);
                    
                    serializer.startTag(null, XML_TAG_THREE_BYTE);
                    serializer.text(String.valueOf(ocmd[2]));
                    serializer.endTag(null, XML_TAG_THREE_BYTE);
                    
                    serializer.startTag(null, XML_TAG_FOUR_BYTE);
                    serializer.endTag(null, XML_TAG_FOUR_BYTE);
                    
                    serializer.startTag(null, XML_TAG_FIVE_BYTE);
                    serializer.endTag(null, XML_TAG_FIVE_BYTE);
                    
                    serializer.startTag(null, XML_TAG_SIX_BYTE);
                    serializer.endTag(null, XML_TAG_SIX_BYTE);
                    serializer.endTag(null, XML_TAG_OPEN_CMD);

                    serializer.startTag(null, XML_TAG_CLOSE_CMD);
                    
                    serializer.startTag(null, XML_TAG_FIRST_BYTE);
                    serializer.text(String.valueOf(ccmd[0]));
                    serializer.endTag(null, XML_TAG_FIRST_BYTE);
                    
                    serializer.startTag(null, XML_TAG_TWO_BYTE);
                    serializer.text(String.valueOf(ccmd[1]));
                    serializer.endTag(null, XML_TAG_TWO_BYTE);
                    
                    serializer.startTag(null, XML_TAG_THREE_BYTE);
                    serializer.text(String.valueOf(ccmd[2]));
                    serializer.endTag(null, XML_TAG_THREE_BYTE);
                    
                    serializer.startTag(null, XML_TAG_FOUR_BYTE);
                    serializer.endTag(null, XML_TAG_FOUR_BYTE);
                    
                    serializer.startTag(null, XML_TAG_FIVE_BYTE);
                    serializer.endTag(null, XML_TAG_FIVE_BYTE);
                    
                    serializer.startTag(null, XML_TAG_SIX_BYTE);
                    serializer.endTag(null, XML_TAG_SIX_BYTE);
                    
                    serializer.endTag(null, XML_TAG_CLOSE_CMD);

                    serializer.endTag(null, XML_TAG_LINK);
                }
                serializer.endTag(null, XML_TAG_DEVICES);
            }

            serializer.endDocument();
            serializer.flush();
            fileos.close();
        } catch (Exception e) {
            Log.e("Exception", "error occurred while creating xml file");
        }
    }

    // xmlPullParser解析xml文件
    private void xmlPullParseXML() {
        File linceseFile = new File(DEVICES_INFO_PATH);
        boolean exist = linceseFile.exists();
        if (!exist) {
            return;
        }
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(linceseFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        if (fis == null) {
            return;
        }
        
        mDevices.clear();
        DevicesInfo tempDevs = null;
        LinkInfo tempLinks = null;
        byte[] ocmd = null;
        byte[] ccmd = null;
        boolean isOpenCmd = true;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(fis, "UTF-8");
            int eventType = xmlPullParser.getEventType();
            try {
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (nodeName.equals(XML_TAG_DEVICES)) {
                                tempDevs = new DevicesInfo();
                            } else if (nodeName.equals(XML_TAG_DEV_NAME)) {
                                tempDevs.setmDevName(xmlPullParser.nextText());
                            } else if (nodeName.equals(XML_TAG_LINK)) {
                                tempLinks = new LinkInfo();
                            } else if (nodeName.equals(XML_TAG_LINK_NAME)) {
                                tempLinks.setmLinkName(xmlPullParser.nextText());
                            } else if (nodeName.equals(XML_TAG_OPEN_CMD)) {
                                isOpenCmd = true;
                                ocmd = new byte[CODE_LEN];
                            } else if (nodeName.equals(XML_TAG_CLOSE_CMD)) {
                                isOpenCmd = false;
                                ccmd = new byte[CODE_LEN];
                            } else if (nodeName.equals(XML_TAG_FIRST_BYTE)) {
                                int temp = Integer.valueOf(xmlPullParser.nextText());
                                if (isOpenCmd) {
                                    ocmd[0] = (byte)temp;
                                } else {
                                    ccmd[0] = (byte)temp;
                                }
                            } else if (nodeName.equals(XML_TAG_TWO_BYTE)) {
                                int temp = Integer.valueOf(xmlPullParser.nextText());
                                if (isOpenCmd) {
                                    ocmd[1] = (byte)temp;
                                } else {
                                    ccmd[1] = (byte)temp;
                                }
                            } else if (nodeName.equals(XML_TAG_THREE_BYTE)) {
                                int temp = Integer.valueOf(xmlPullParser.nextText());
                                if (isOpenCmd) {
                                    ocmd[2] = (byte)temp;
                                } else {
                                    ccmd[2] = (byte)temp;
                                }
                            } else if (nodeName.equals(XML_TAG_FOUR_BYTE)) {
                                
                            }  else if (nodeName.equals(XML_TAG_FIVE_BYTE)) {
                                
                            } else if (nodeName.equals(XML_TAG_SIX_BYTE)) {
                                
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (nodeName.equals(XML_TAG_DEVICES)) {
                                mDevices.add(tempDevs);
                            } else if (nodeName.equals(XML_TAG_DEV_NAME)) {

                            } else if (nodeName.equals(XML_TAG_LINK)) {
                                tempDevs.getmLinks().add(tempLinks);
                            } else if (nodeName.equals(XML_TAG_LINK_NAME)) {
                                
                            } else if (nodeName.equals(XML_TAG_OPEN_CMD)) {
                                tempLinks.setmOpenCmd(ocmd);
                            } else if (nodeName.equals(XML_TAG_CLOSE_CMD)) {
                                tempLinks.setmCloseCmd(ccmd);
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = xmlPullParser.next();
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public boolean sendCode(byte[] code) {
        if (mBtService == null) {
            return false;
        }
        mBtService.write(code);
        return true;
    }

    public byte[] generateCode(byte linkIndex, byte openOrClose, int devIndex) {
        byte[] cmd = new byte[CODE_LEN];

        cmd[0] = (byte) (1000 * Math.random());
        cmd[1] = (byte) (((byte) devIndex) << 7 | openOrClose);
        cmd[CODE_LEN - 1] = linkIndex;

        return cmd;
    }
}
