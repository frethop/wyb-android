package com.kabestin.android.wristbarcode.control;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.kabestin.android.wristbarcode.model.WBLocation;
import com.kabestin.android.wristbarcode.view2.WBMain;

public class PinOnClickAction implements OnClickListener {
	
	WBMain parent;
	int position;
	
	public PinOnClickAction(WBMain aParent, int aPosition) {
		parent = aParent;
		position = aPosition;
	}

	@Override
	public void onClick(View v) {
		
		WBLocation loc = new WBLocation();
		parent.getBarcodeList().get(position).setLatitude(loc.getLatitude());
		parent.getBarcodeList().get(position).setLongitude(loc.getLongitude());
		Toast.makeText(parent.getApplicationContext(), 
				"Current location pinned to barcode", 
				Toast.LENGTH_LONG).show();

	}

}
