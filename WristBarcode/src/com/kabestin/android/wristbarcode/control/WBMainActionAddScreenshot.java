package com.kabestin.android.wristbarcode.control;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class WBMainActionAddScreenshot implements OptionsItemHandler, ActivityResultHandler {
	
	private Uri fileUri;
	Activity source;

	public WBMainActionAddScreenshot() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean handleOptionsItem(Activity source) {
		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("*/*");
		galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		// Chooser of filesystem options.
		Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		// Add the camera options.
		//chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
		//		cameraIntents.toArray(new Parcelable[] {}));
		
		Intent getContentIntent = FileUtils.createGetContentIntent();

	    chooserIntent = Intent.createChooser(getContentIntent, "Select a file");

		source.startActivityForResult(chooserIntent, 1001);
		
		return true;
	}
	
	// With help:
	// http://stackoverflow.com/questions/14861553/zxing-convert-bitmap-to-binarybitmap
	
	public void handleActivityResult(Activity source, Intent sourceIntent) 
	{
		this.source = source;
		
		fileUri = sourceIntent.getData();

		Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath());
		
		int[] intArray = new int[bm.getWidth()*bm.getHeight()];  
		//copy pixel data from the Bitmap into the 'intArray' array  
		bm.getPixels(intArray, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());  

		LuminanceSource lSource = new RGBLuminanceSource(bm.getWidth(), bm.getHeight(),intArray);

		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(lSource));
		Reader reader = new MultiFormatReader();     
		//....doing the actually reading
		try {
			Result result = reader.decode(bitmap);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChecksumException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	


}
