package com.kabestin.android.wristbarcode.view;

import java.io.File;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

// Preference setting via Android framework
// Take a GradeSet, convert properties to preferences, throw up the activity, convert
// back when done

public class OpeningPreferences extends PreferenceActivity {
		
	SharedPreferences prefs;
	SharedPreferences defPrefs;
	SharedPreferences.Editor editor;
	Context parent;
	Activity actParent;
	
	boolean firstRun = true;
	
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	boolean b;
    	parent = this;
    	actParent = (Activity)this;
		prefs = getSharedPreferences("wybPrefs", MODE_MULTI_PROCESS);
    	defPrefs = PreferenceManager.getDefaultSharedPreferences(WBMain.parent);
		
		// Convert properties to preferences
		editor = defPrefs.edit();
		Map<String, ?> values = prefs.getAll();
		Set<String> keyset = values.keySet();
		for (String str : keyset) {
			try {
				editor.putBoolean(str, (Boolean)(values.get(str)));
			} catch (ClassCastException cce) {
				// do nothing
			}
		}
		editor.commit();
		
		// Display the preferences activity
		addPreferencesFromResource(R.xml.preferences);
		
	}
    
    private static final String APP_DATA_PATH = "/Android/data/com.kabestin.android.gradebox/temp/";
	private File getTempDir() {
	    return new File(Environment.getExternalStorageDirectory(), APP_DATA_PATH);
	}

    public void onBackPressed() {
    	// convert default preferences to shared preferences
		editor = prefs.edit();
    	Map<String, ?> defValues = defPrefs.getAll();    	
    	Set<String> keyset = defValues.keySet();
		for (String str : keyset) {
			try {
				editor.putBoolean(str, (Boolean)(defValues.get(str)));
			} catch (ClassCastException cce) {
				// skip this one
			}
		}	
		editor.commit();
		
		// Return to the ClassDisplay
		Bundle bundle = new Bundle();
		Intent result = new Intent();
    	result.putExtras(bundle);
		setResult(RESULT_OK, result);
		finish();
    }
    
}
