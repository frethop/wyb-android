package com.kabestin.android.wristbarcode.control;

import android.view.View;
import android.view.View.OnClickListener;

import com.kabestin.android.wristbarcode.model.ViewHolder;
import com.kabestin.android.wristbarcode.view.WBMain;

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
		parent.redrawList();
		parent.restartService();

	}

}
