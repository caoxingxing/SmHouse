package com.sungeo.smhouse.util;

import android.app.Activity;

public class AnimCommon {
	private Activity context;  
	
    public AnimCommon(Activity context){  
        this.context = context;  
    }  
	
    /** 
     * call overridePendingTransition() on the supplied Activity. 
     * @param a  
     * @param b 
     */  
    public void overridePendingTransition(int a, int b){  
        context.overridePendingTransition(a, b);  
    }  
}
