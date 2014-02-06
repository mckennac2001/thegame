package com.mckenna.thegame;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

/* Our custom LocationSource. 
 * We register this class to receive location updates from the Location Manager
 * and for that reason we need to also implement the LocationListener interface. */
public class FollowMeLocationSource implements LocationSource, LocationListener {

    /* Updates are restricted to one every 10 seconds, and only when
     * movement of more than 10 meters has been detected.*/
    private final int minTime = 1000;     // minimum time interval between location updates, in milliseconds
    private final int minDistance = 1;    // minimum distance between location updates, in meters
	private final String TAG = "FollowMeLocationSource";
	
    private OnLocationChangedListener locationChangeListener;
    private LocationUpdateListener locationUpdateListener;
    private LocationManager locationManager;
    private final Criteria criteria = new Criteria();
    private String bestAvailableProvider;
    private GoogleMap map;
    private Context context;
    
    public FollowMeLocationSource(GoogleMap map, Context context, LocationUpdateListener locationUpdateListener) {
    	//store the reference to the activities map
    	this.map = map;
    	this.context = context;
    	
        // Get reference to Location Manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Specify Location Provider criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        
        MapUtils.centerMapOnLastKnownPosition(locationManager);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.centerMapOnLastKnownPosition(locationManager), 8));
    }

    public void getBestAvailableProvider() {
		MyLog.d(TAG, "getBestAvailableProvider");
        /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
         * is to ask the Location Manager for the one that best satisfies our criteria.
         * By passing the 'true' boolean we ask for the best available (enabled) provider. */
        bestAvailableProvider = locationManager.getBestProvider(criteria, true);
    }

    /* Activates this provider. This provider will notify the supplied listener
     * periodically, until you call deactivate().
     * This method is automatically invoked by enabling my-location layer. */
    @Override
    public void activate(OnLocationChangedListener listener) {
		MyLog.d(TAG, "OnLocationChangedListener");
		
        // We need to keep a reference to my-location layer's listener so we can push forward
        // location updates to it when we receive them from Location Manager.
        locationChangeListener = listener;

        // Request location updates from Location Manager
        if (bestAvailableProvider != null) {
            locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
        } else {
            // (Display a message/dialog) No Location Providers currently available.
        	GameAlerts.showError(this.context, "No Location Providers currently available");
        }
    }

    /* Deactivates this provider.
     * This method is automatically invoked by disabling my-location layer. */
    @Override
    public void deactivate() {
		MyLog.d(TAG, "deactivate");
        // Remove location updates from Location Manager
        locationManager.removeUpdates(this);
        locationChangeListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
		MyLog.d(TAG, "onLocationChanged");
        /* Push location updates to the registered listener..
         * (this ensures that my-location layer will set the blue dot at the new/received location) */ 	
        if (locationChangeListener != null) {
            locationChangeListener.onLocationChanged(location);
        }

        /* ..and Animate camera to center on that location !
         * (the reason for we created this custom Location Source !) */
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        
        // Update anyone what wants to know
        if (locationUpdateListener != null) {
        	locationUpdateListener.OnLocationUpdate(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
		MyLog.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
		MyLog.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
		MyLog.d(TAG, "onProviderDisabled");
    }

    public void centerMapOnLastKnownPosition(GoogleMap mMap) 
    {
    	MapUtils.centerMapOnLastKnownPosition(locationManager);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.centerMapOnLastKnownPosition(locationManager), 8));
    	
    }
}