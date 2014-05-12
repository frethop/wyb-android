package com.kabestin.android.wristbarcode.control;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.kabestin.android.wristbarcode.view.R;
import com.kabestin.android.wristbarcode.view.WBMain;

public class BarcodeListOnLongClickAction implements OnLongClickListener {
	
	WBMain parent;
	int position;
	
	public BarcodeListOnLongClickAction(WBMain aParent, int aPosition) {
		parent = aParent;
		position = aPosition;
	}

	@Override
	public boolean onLongClick(View v) {
		
		//ListView lview = (ListView) parent.findViewById(R.id.barcode_list);
		//lview.setItemsCanFocus(true);
		//lview.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		
		ViewSwitcher switcher = (ViewSwitcher) v.findViewById(R.id.barcode_name_editor);
		new AnimationUtils();
        switcher.setAnimation(AnimationUtils.makeInAnimation(parent.getApplicationContext(), true));
	    switcher.showNext(); 
	    
	    int w = v.getWidth();
		
		TextView tv = (TextView) v.findViewById(R.id.barcode_name);
		String name = tv.getText().toString();
		EditText et = (EditText) v.findViewById(R.id.barcode_editable_name);
		et.setWidth(w-100);
		et.setText(name);
		et.setFocusable(true);
		et.setFocusableInTouchMode(true);
        //et.requestFocus();
		
		ImageButton b = (ImageButton) v.findViewById(R.id.barcode_delete_button);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new DeleteOnClickAction(parent, position));
		b = (ImageButton) v.findViewById(R.id.barcode_pin_button);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new PinOnClickAction(parent, position));
		b = (ImageButton) v.findViewById(R.id.barcode_done_button);
		b.setVisibility(View.VISIBLE);
		b.setOnClickListener(new BarcodeListOnClickListener(parent, parent.getBarcodeList().get(position)));
		
		v.setOnClickListener(null);

		return false;
	}

}
