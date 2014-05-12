package com.kabestin.android.wristbarcode.control;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class WBMainActionAdd implements OptionsItemHandler {

	public WBMainActionAdd() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleOptionsItem(Activity source) {
		IntentIntegrator integrator = new IntentIntegrator(source);
		integrator.initiateScan();
		return true;
	}

}
