package com.mckenna.thegame;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MapUtils {

	private final static String TAG = "MapUtils";
	
	public MapUtils() {
		// TODO Auto-generated constructor stub
	}

    public static LatLng centerMapOnLastKnownPosition(LocationManager locationManager) 
    {
    	MyLog.d(TAG, "centerMapOnLastKnownPosition");
    	
    	
        Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		
    	//center the map on one of the last know fixes
    	if (networkLocation != null && networkLocation.getLatitude() != 0) {
    		//is gps working?
    		if(gpsLocation != null && gpsLocation.getLatitude() != 0) {
    			//we have 2 fixes, which is newer?
    			if(networkLocation.getTime() > gpsLocation.getTime()) {
    				//network fix was more recent
    				MyLog.d(TAG, "network fix was more recent");
    				return new LatLng(networkLocation.getLatitude(), networkLocation.getLongitude());
    				
    			} else {
    				//gps fix was more recent
    				MyLog.d(TAG, "gps fix was more recent");
    				return new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
    			}
    		} else {
    			//only network is available
    			MyLog.d(TAG, "only network is available");
    			return new LatLng(networkLocation.getLatitude(), networkLocation.getLongitude());
    		}
    	} else if(gpsLocation != null && gpsLocation.getLatitude() != 0) {
    			//only gps is available
    			MyLog.d(TAG, "only gps is available");
    			return new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
    	} else {
    		// Neither are working! Lets default to Rosses Point!
    		MyLog.d(TAG, "neither are working, center or holiday town");
    		return new LatLng(54.305507, -8.564193);
    	}
    }
}
