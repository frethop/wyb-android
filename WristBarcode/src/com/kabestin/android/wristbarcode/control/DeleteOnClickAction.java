package com.kabestin.android.wristbarcode.control;

import android.view.View;
import android.view.View.OnClickListener;

import com.kabestin.android.wristbarcode.view2.WBMain;

public class DeleteOnClickAction implements OnClickListener {
	
	WBMain parent;
	int position;
	
	public DeleteOnClickAction(WBMain aParent, int aPosition) {
		parent = aParent;
		position = aPosition; 
	}

	@Override
	public void onClick(View v) {
		
		parent.getBarcodeList().remove(position);
		parent.saveBarcodeFile();
		parent.restartService();
		parent.redrawList();
		parent.rereadBarcodeList();

	}

}
