package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.app.Dialog;
import android.view.View.OnClickListener;

public interface DialogCreator {
	
	Dialog handleDialogCreation(Activity source,
			OnClickListener creationCallback, 
			OnClickListener cancelCallback);

}
