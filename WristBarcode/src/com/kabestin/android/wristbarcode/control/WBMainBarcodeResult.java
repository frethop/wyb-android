package com.kabestin.android.wristbarcode.control;

import android.app.Activity;

import com.google.zxing.integration.android.IntentResult;
import com.kabestin.android.wristbarcode.view2.WBMain;

public class WBMainBarcodeResult {
	
	Activity parent;
	IntentResult result;
	
	public WBMainBarcodeResult(Activity aParent, IntentResult iResult)
	{
		parent = aParent;
		result = iResult;
	}
	
	public void handleResult()
	{
		((WBMain)parent).setScanResult(result);
		parent.showDialog(0);
	}
	
	
}
