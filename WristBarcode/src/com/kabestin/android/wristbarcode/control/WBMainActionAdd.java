package com.kabestin.android.wristbarcode.control;

import java.util.ArrayList;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class WBMainActionAdd implements OptionsItemHandler {

	public WBMainActionAdd() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleOptionsItem(Activity source) {
		IntentIntegrator integrator = new IntentIntegrator(source);
		ArrayList<String> codes = new ArrayList<String>();
		codes.add("AZTEC");
		codes.add("PDF417");
		codes.add("DATAMATRIX");
		codes.add("MAXICODE");
		codes.add("ONED");
		codes.add("MULTI");
		integrator.initiateScan(codes);
		return true;
	}

}
