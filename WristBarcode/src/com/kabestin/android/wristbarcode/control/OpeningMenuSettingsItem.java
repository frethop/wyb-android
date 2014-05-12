package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kabestin.android.wristbarcode.view.OpeningPreferences;
import com.kabestin.android.wristbarcode.view.R;
import com.kabestin.android.wristbarcode.view.WBMain;

public class OpeningMenuSettingsItem implements OptionsItemHandler, ActivityResultHandler {
	
	public boolean handleOptionsItem(Activity source)
	{
    	Bundle bundle = new Bundle();
    	Intent prefsIntent = new Intent(source, OpeningPreferences.class);
    	prefsIntent.putExtras(bundle);
    	source.startActivityForResult(prefsIntent, R.id.action_settings);
        return true;
	}
	
	public void handleActivityResult(Activity source, Intent sourceIntent) 
	{
		((WBMain) source).rereadPrefs();
		((WBMain) source).restartService();
	}

}
