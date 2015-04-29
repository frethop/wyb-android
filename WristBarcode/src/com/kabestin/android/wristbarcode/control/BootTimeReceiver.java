package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kabestin.android.wristbarcode.view2.WBMain;

public class BootTimeReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	SharedPreferences prefs = context.getSharedPreferences("wybPrefs",context.MODE_MULTI_PROCESS);
    	if (prefs.getBoolean("startAtBoot", false)) {
	    	Intent myIntent = new Intent(context, WBService.class);
	    	context.startService(myIntent);
    	}
    }
}