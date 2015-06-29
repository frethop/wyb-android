package com.kabestin.android.wristbarcode.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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