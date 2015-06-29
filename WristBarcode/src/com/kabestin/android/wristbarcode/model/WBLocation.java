package com.kabestin.android.wristbarcode.model;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.kabestin.android.wristbarcode.view2.WBMain;

public class WBLocation {
	
	double lat, lon;
	LocationManager locationManager=null;

	public WBLocation() {
		lat = -1.0;
		lon = -1.0;
	}
	
	public WBLocation(LocationManager lm) {
		lat = -1.0;
		lon = -1.0;
		locationManager = lm;
	}
	
	public double getLatitude()
	{
		if (lat == -1.0) getLocation();
		return lat;
	}
	
	public double getLongitude()
	{
		if (lon == -1.0) getLocation();
		return lon;
	}
	
	private void getLocation() {
		// Get the location manager
		if (locationManager == null) locationManager = WBMain.locationManager;

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);

		try {
			String bestProvider = locationManager.getBestProvider(criteria,false);
			Location location = locationManager.getLastKnownLocation(bestProvider);
			LocationListener loc_listener = new LocationListener() {

				public void onLocationChanged(Location l) {
				}

				public void onProviderEnabled(String p) {
				}

				public void onProviderDisabled(String p) {
				}

				public void onStatusChanged(String p, int status, Bundle extras) {
				}
			};
			locationManager.requestLocationUpdates(bestProvider, 0, 0,
					loc_listener);
			location = locationManager.getLastKnownLocation(bestProvider);
			lat = location.getLatitude();
			lon = location.getLongitude();
		} catch (Exception e) {
			e.printStackTrace();
			lat = -1.0;
			lon = -1.0;
		}
	}


}
