package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.content.Intent;

import com.kabestin.android.wristbarcode.view2.R;

public class OpeningMenuAboutItem implements OptionsItemHandler, ActivityResultHandler {
	
	public boolean handleOptionsItem(Activity source)
	{
		source.showDialog(R.id.about_content);
		return true;
	}
	
	public void handleActivityResult(Activity source, Intent sourceIntent) 
	{
	}

}
