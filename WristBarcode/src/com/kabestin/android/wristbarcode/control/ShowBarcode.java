package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.app.Dialog;
import android.view.View.OnClickListener;

import com.google.zxing.common.BitMatrix;
import com.kabestin.android.wristbarcode.view.WBMain;

public class ShowBarcode implements DialogCreator {

	@Override
	public Dialog handleDialogCreation(Activity source,
			OnClickListener creationCallback, OnClickListener cancelCallback) {
		
		Dialog dialog = null;
		
		BitMatrix bm = ((WBMain)source).getMatrix();
		
		return dialog;
	}

}
