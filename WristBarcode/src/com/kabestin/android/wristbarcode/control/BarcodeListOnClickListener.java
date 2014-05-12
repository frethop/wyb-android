package com.kabestin.android.wristbarcode.control;

import java.util.UUID;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.kabestin.android.wristbarcode.model.Barcode;
import com.kabestin.android.wristbarcode.model.ViewHolder;
import com.kabestin.android.wristbarcode.view.R;
import com.kabestin.android.wristbarcode.view.WBMain;

public class BarcodeListOnClickListener implements OnClickListener {
	
    private final static UUID PEBBLE_APP_UUID = UUID.fromString("D8477C50-0205-452A-BB45-30B00606EF8B");
	
	Barcode barcode;
	WBService service;
	WBMain parent;
	
	public BarcodeListOnClickListener(WBMain aParent, Barcode aBarcode){
		barcode = aBarcode;
		parent = aParent;
	}
	
	@Override
	public void onClick(View v) {
		ViewHolder vh = (ViewHolder) v.getTag();
		View container = vh.containerRow;
		LinearLayout row1 = (LinearLayout) container.findViewById(R.id.row_one);
		TextView tv = (TextView) row1.findViewById(R.id.barcode_name);
		if (tv.getVisibility() == View.VISIBLE) {
			parent.restartServiceWithBarcode(vh.mPosition);
			//parent.stopWatchApp(v);
			parent.startWatchApp(v);
		} else {
			RelativeLayout row2 = (RelativeLayout) container.findViewById(R.id.row_two);
			EditText et = (EditText) row1.findViewById(R.id.barcode_editable_name);
			ViewSwitcher switcher = (ViewSwitcher) row1.findViewById(R.id.barcode_name_editor);	
			new AnimationUtils();
	        switcher.setAnimation(AnimationUtils.makeInAnimation(parent.getApplicationContext(), true));
		    switcher.showPrevious();

			String name = et.getText().toString();
			tv.setText(name);
			
			barcode.setName(name);
			parent.getBarcodeList().sortCodes();
			parent.saveBarcodeFile();
			parent.redrawList();
			parent.restartService();
			
			ImageButton b = (ImageButton) row2.findViewById(R.id.barcode_delete_button);
			b.setVisibility(View.GONE);
			b = (ImageButton) row2.findViewById(R.id.barcode_pin_button);
			b.setVisibility(View.GONE);
			b = (ImageButton) row1.findViewById(R.id.barcode_done_button);
			b.setVisibility(View.GONE);
		
		}
	}

}
