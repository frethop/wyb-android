package com.kabestin.android.wristbarcode.model;

import android.graphics.drawable.GradientDrawable.Orientation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentResult;

public class Barcode {
	
	static public enum BarcodeOrientation	{
		NORTH, EAST, SOUTH, WEST
	}
	
	String codeName;
	String content;
	String formatName;
	BarcodeFormat format;
	double latitude, longitude;
	
	public Barcode (IntentResult scanResult) {
		codeName = null;
		content = scanResult.getContents();
		formatName = scanResult.getFormatName();
		format = name2format(formatName);
		latitude = longitude = 0;
	}

	public Barcode (String name, IntentResult scanResult, double aLatitude, double aLongitude) {
		codeName = name;
		content = scanResult.getContents();
		formatName = scanResult.getFormatName();
		format = name2format(formatName);
		latitude = aLatitude;
		longitude = aLongitude;
	}
	
	public Barcode (String name, String formatName, String content) {
		codeName = name;
		this.content = content;
		this.formatName = formatName;
		format = name2format(formatName);
		latitude = longitude = 0;
	}
	
	public Barcode (String name, String formatName, String content, double aLatitude, double aLongitude) {
		codeName = name;
		this.content = content;
		this.formatName = formatName;
		format = name2format(formatName);
		latitude = aLatitude;
		longitude = aLongitude;
	}
	
	private BarcodeFormat name2format (String sFormat) {
		if (formatName.toLowerCase().equals("aztec"))
			return BarcodeFormat.AZTEC;
		else if (formatName.toLowerCase().equals("codeabar"))
			return BarcodeFormat.CODABAR;
		else if (formatName.toLowerCase().equals("codabar"))
			return BarcodeFormat.CODABAR;
		else if (formatName.toLowerCase().equals("code_39"))
			return BarcodeFormat.CODE_39;
		else if (formatName.toLowerCase().equals("code_93"))
			return BarcodeFormat.CODE_93;
		else if (formatName.toLowerCase().equals("code_128"))
			return BarcodeFormat.CODE_128;
		else if (formatName.toLowerCase().equals("data_matrix"))
			return BarcodeFormat.DATA_MATRIX;
		else if (formatName.toLowerCase().equals("ean_8"))
			return BarcodeFormat.EAN_8;
		else if (formatName.toLowerCase().equals("ean_13"))
			return BarcodeFormat.EAN_13;
		else if (formatName.toLowerCase().equals("itf"))
			return BarcodeFormat.ITF;
		else if (formatName.toLowerCase().equals("maxicode"))
			return BarcodeFormat.MAXICODE;
		else if (formatName.toLowerCase().equals("pdf_417"))
			return BarcodeFormat.PDF_417;
		else if (formatName.toLowerCase().equals("qr_code"))
			return BarcodeFormat.QR_CODE;
		else if (formatName.toLowerCase().equals("qrcode"))
			return BarcodeFormat.QR_CODE;
		else if (formatName.toLowerCase().equals("rss_14"))
			return BarcodeFormat.RSS_14;
		else if (formatName.toLowerCase().equals("rss_expanded"))
			return BarcodeFormat.RSS_EXPANDED;
		else if (formatName.toLowerCase().equals("upc_a"))
			return BarcodeFormat.UPC_A;
		else if (formatName.toLowerCase().equals("upc_e"))
			return BarcodeFormat.UPC_E;
		else if (formatName.toLowerCase().equals("UPC_EAN_EXTENSION"))
			return BarcodeFormat.UPC_EAN_EXTENSION;

		return null;
	}
	
	public String getStartingOrientation() {
		if (formatName.toLowerCase().equals("aztec"))
			return "Top";
		else if (formatName.toLowerCase().equals("codeabar"))
			return "Left";
		else if (formatName.toLowerCase().equals("codabar"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_39"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_93"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_128"))
			return "Left";
		else if (formatName.toLowerCase().equals("data_matrix"))
			return "Top";
		else if (formatName.toLowerCase().equals("ean_8"))
			return "Left";
		else if (formatName.toLowerCase().equals("ean_13"))
			return "Left";
		else if (formatName.toLowerCase().equals("itf"))
			return "Top";
		else if (formatName.toLowerCase().equals("maxicode"))
			return "Top";
		else if (formatName.toLowerCase().equals("pdf_417"))
			return "Top";
		else if (formatName.toLowerCase().equals("qr_code"))
			return "Right";
		else if (formatName.toLowerCase().equals("qrcode"))
			return "Right";
		else if (formatName.toLowerCase().equals("rss_14"))
			return "Top";
		else if (formatName.toLowerCase().equals("rss_expanded"))
			return "Top";
		else if (formatName.toLowerCase().equals("upc_a"))
			return "Top";
		else if (formatName.toLowerCase().equals("upc_e"))
			return "Top";
		else if (formatName.toLowerCase().equals("UPC_EAN_EXTENSION"))
			return "Top";
		else 
			return null;

	}
	
	public String getDesiredOrientation()
	{
		if (formatName.toLowerCase().equals("aztec"))
			return "Top";
		else if (formatName.toLowerCase().equals("codeabar"))
			return "Left";
		else if (formatName.toLowerCase().equals("codabar"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_39"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_93"))
			return "Left";
		else if (formatName.toLowerCase().equals("code_128"))
			return "Left";
		else if (formatName.toLowerCase().equals("data_matrix"))
			return "Top";
		else if (formatName.toLowerCase().equals("ean_8"))
			return "Left";
		else if (formatName.toLowerCase().equals("ean_13"))
			return "Left";
		else if (formatName.toLowerCase().equals("itf"))
			return "Top";
		else if (formatName.toLowerCase().equals("maxicode"))
			return "Top";
		else if (formatName.toLowerCase().equals("pdf_417"))
			return "Top";
		else if (formatName.toLowerCase().equals("qr_code"))
			return "Top";
		else if (formatName.toLowerCase().equals("qrcode"))
			return "Top";
		else if (formatName.toLowerCase().equals("rss_14"))
			return "Top";
		else if (formatName.toLowerCase().equals("rss_expanded"))
			return "Top";
		else if (formatName.toLowerCase().equals("upc_a"))
			return "Left";
		else if (formatName.toLowerCase().equals("upc_e"))
			return "Left";
		else if (formatName.toLowerCase().equals("UPC_EAN_EXTENSION"))
			return "Left";
		else 
			return null;
	}
	
	static public int difference(String o1, String o2)
	{
		int code1 = 0;
		int code2 = 0;
		
		if (o1.equals("Top")) code1 = 3;
		if (o1.equals("Right")) code1 = 2;
		if (o1.equals("Bottom")) code1 = 1;
		if (o1.equals("Left")) code1 = 0;
		if (o2.equals("Top")) code2 = 3;
		if (o2.equals("Right")) code2 = 2;
		if (o2.equals("Bottom")) code2 = 1;
		if (o2.equals("Left")) code2 = 0;
		
		return code1 - code2;
	}

	public String getName() {
		return codeName;
	}

	public void setName(String codeName) {
		this.codeName = codeName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
		this.format = name2format(formatName);
	}
	
	public BarcodeFormat getFormat()
	{
		return format;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude(double aLat)
	{
		latitude = aLat;
	}
	
	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double aLong)
	{
		longitude = aLong;
	}
	
	public boolean equals(Object other) {
		boolean result = false;
	    if (other instanceof Barcode) {
	    	Barcode that = (Barcode) other;
	        result = (this.codeName.equals(that.codeName) &&
	        		  this.formatName.equals(that.formatName) &&
	        		  this.content.equals(that.content));
	    }
	    return result; 
	}
	
	public int hashcode() {
		String mashed = codeName + formatName + content;
		int code = 0;
		for (int i=0; i<mashed.length(); i++) code += mashed.charAt(i);
		return code;
	}
	
}
