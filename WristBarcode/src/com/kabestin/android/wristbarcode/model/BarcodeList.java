package com.kabestin.android.wristbarcode.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;

import com.kabestin.android.wristbarcode.view.WBMain;

public class BarcodeList extends ArrayList<Barcode> {
	
	WBLocation location = null;
	
	public BarcodeList()
	{
		// Nothing for now
	}
	
	public BarcodeList(WBLocation loc)
	{
		location = loc;
	}
	
	private static final String APP_DATA_PATH = "/Android/data/com.kabestin.android.wristbarcode/temp/";
	static private File getTempDir() {
	    return new File(Environment.getExternalStorageDirectory(), APP_DATA_PATH);
	}
	
	public void generateData()
	{
		Barcode b = new Barcode("Fake Barcode", "UPC_A", "1234567890");
		add(b);
		b = new Barcode("Fake Barcode 2", "QR_CODE", "Fake Barcode");
		add(b);
	}
	
	public class byNameAscending implements java.util.Comparator {
		public int compare(Object barcode1, Object barcode2) {
			return ((Barcode)barcode1).getName().compareTo(((Barcode) barcode2).getName());
		}
	} 
	
	public class byLocation implements java.util.Comparator<Barcode> {
		double lat, lon;
		public byLocation() {
			if (location == null) location = new WBLocation();
			lat = location.getLatitude();
			lon = location.getLongitude();
		}
		public int compare(Barcode barcode1, Barcode barcode2) {
			
			//use Location.distanceTo();  //http://developer.android.com/reference/android/location/Location.html
			float[] results1 = new float[1];
			float[] results2 = new float[1];
			
			Location.distanceBetween(lat, lon,
					barcode1.getLatitude(), barcode1.getLongitude(),
					results1);
			Location.distanceBetween(lat, lon,
					barcode2.getLatitude(), barcode2.getLongitude(),
					results2);
			return (new Float(results1[0])).compareTo(new Float(results2[0]));	
		}
	} 
	
	public void sortCodes()
	{
		//String sort = WBMain.properties == null?"no":WBMain.properties.get("sortByLocation");
		Boolean sortByLocation = WBMain.prefs == null?false:WBMain.prefs.getBoolean("sortByLocation",false);
		//if (sort == null) sort = "no";
		if (sortByLocation) 
			sortCodes(new byLocation());
		else 
			sortCodes(new byNameAscending());
	}
	
	public void sortCodes(boolean byLocs)
	{
		if (byLocs) 
			sortCodes(new byLocation());
		else 
			sortCodes(new byNameAscending());
	}

	private void sortCodes(Comparator<? super Barcode> comparator) 
	{
		Collections.sort(this, comparator); 
	}
	
	public boolean add(Barcode barcode) {
		super.add(barcode);
		sortCodes(new byNameAscending());
		return true;
	}
		
	public void read(FileInputStream input)
	{
		BufferedReader barcodeFileReader = new BufferedReader(new InputStreamReader(input));
		
		String line;
		String name, format, content;
		Float lat, lon;
		int num;
		Barcode bc;
		
		try {
			line = barcodeFileReader.readLine();
			if (line == null) return;
			num = new Integer(line).intValue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		for (int i=0; i<num; i++) {
			try {
				name = barcodeFileReader.readLine();
				format = barcodeFileReader.readLine();
				content = barcodeFileReader.readLine();
				lat = new Float(barcodeFileReader.readLine());
				lon = new Float(barcodeFileReader.readLine());
				bc = new Barcode(name, format, content, lat.floatValue(), lon.floatValue());
				add(bc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		sortCodes();
	}
	
	public void write(FileOutputStream output)
	{
		BufferedWriter barcodeFileWriter = new BufferedWriter(new OutputStreamWriter(output));
		
		try {
			int num = size();
			barcodeFileWriter.write(""+num+"\n");
			for (int i=0; i<num; i++) {
				barcodeFileWriter.write(get(i).getName()+"\n");
				barcodeFileWriter.write(get(i).getFormatName()+"\n");
				barcodeFileWriter.write(get(i).getContent()+"\n");
				barcodeFileWriter.write(get(i).getLatitude()+"\n");
				barcodeFileWriter.write(get(i).getLongitude()+"\n");
			}
			barcodeFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
