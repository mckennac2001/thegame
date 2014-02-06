package com.mckenna.thegame;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MyLocationMapFragmentActivity extends FragmentActivity implements LocationListener, LocationSource
{
	
private final String TAG = "MyLocationMapFragmentActivity";
private GoogleMap mMap;
private LocationManager locationManager;
private OnLocationChangedListener mListener;
private enum LocationProviderInUse {NETWORK, GPS, BOTH};
private LocationProviderInUse locationProviderInUse;

@Override
protected void onCreate(Bundle savedInstanceState) 
{
	MyLog.d(TAG, "onCreate");
	
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_just_map);

    //get the location manager
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    //locationManager.addGpsStatusListener(this);

    setUpMapIfNeeded();
    
    if(locationManager != null)
    {
    	requestLocationUpdates();
    }
    else
    {
        //Show a generic error dialog since LocationManager is null for some reason
    	GameAlerts.showError(this, "LocationManager is null");
    }

    GameAlerts.showError(this, "MyLocationMapFragmentActivity onCreate");
    
    MapUtils.centerMapOnLastKnownPosition(locationManager);
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.centerMapOnLastKnownPosition(locationManager), 8));
}

//private void requestLocationUpdates(String preferedProvider) {
private void requestLocationUpdates() {	
	
    //boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    //boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 5F, this);
    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 10F, this);
    //locationProviderInUse = LocationProviderInUse.BOTH;
    
    /*
    if(preferedProvider.equals(LocationManager.NETWORK_PROVIDER)) {
    	if (networkIsEnabled) {
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
    	} else if(gpsIsEnabled) {
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
    	} else {
    		GameAlerts.showError(this, "GPS and Network are disabled");
    	}
    } else if(preferedProvider.equals(LocationManager.GPS_PROVIDER)) {
    	if(gpsIsEnabled) {
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
    	} else if (networkIsEnabled) {
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
    	} else {
    		GameAlerts.showError(this, "GPS and Network are disabled");
    	}
    } */
}
/*
public void onGpsStatusChanged(int i) {
	switch (i) {
	case GpsStatus.GPS_EVENT_STARTED:
		//GameAlerts.showMessage(this, 1000, "GPS Info", "GPS_EVENT_STARTED");
		break;
	case GpsStatus.GPS_EVENT_STOPPED:
		//GameAlerts.showMessage(this, 1000, "GPS Info", "GPS_EVENT_STOPPED");
		break;
	case GpsStatus.GPS_EVENT_FIRST_FIX:
		//GameAlerts.showMessage(this, 1000, "GPS Info", "GPS_EVENT_FIRST_FIX");
		break;
	case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
		//GameAlerts.showMessage(this, 1000, "GPS Info", "GPS_EVENT_SATELLITE_STATUS");
		break;
	}
}
*/
@Override
public void onPause()
{
	MyLog.d(TAG, "onResume");
	
    if(locationManager != null)
    {
        locationManager.removeUpdates(this);
    }
    super.onPause();
}

@Override
public void onResume()
{
    MyLog.d(TAG, "onResume");
    super.onResume();
    
    setUpMapIfNeeded();

    if(locationManager != null)
    {
        mMap.setMyLocationEnabled(true);
    } 
}


/**
 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
 * installed) and the map has not already been instantiated.. This will ensure that we only ever
 * call {@link #setUpMap()} once when {@link #mMap} is not null.
 * <p>
 * If it isn't installed {@link SupportMapFragment} (and
 * {@link com.google.android.gms.maps.MapView
 * MapView}) will show a prompt for the user to install/update the Google Play services APK on
 * their device.
 * <p>
 * A user can return to this Activity after following the prompt and correctly
 * installing/updating/enabling the Google Play services. Since the Activity may not have been
 * completely destroyed during this process (it is likely that it would only be stopped or
 * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
 * {@link #onResume()} to guarantee that it will be called.
 */
private void setUpMapIfNeeded() {
    // Do a null check to confirm that we have not already instantiated the map.
    if (mMap == null) 
    {
        // Try to obtain the map from the SupportMapFragment.
//        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
        // Check if we were successful in obtaining the map.

        if (mMap != null) 
        {
            setUpMap();
            //This is how you register the LocationSource
            mMap.setLocationSource(this);
        }
    }
}

/**
 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
 * just add a marker near Africa.
 * <p>
 * This should only be called once and when we are sure that {@link #mMap} is not null.
 */
private void setUpMap() 
{
    mMap.setMyLocationEnabled(true);
}



@Override
public void activate(OnLocationChangedListener listener) 
{
	MyLog.d(TAG, "activate");
    mListener = listener;
}

@Override
public void deactivate() 
{
	MyLog.d(TAG, "deactivate");
    mListener = null;
}


@Override
public void onLocationChanged(Location location) 
{
	Toast.makeText(this, "onLocationChanged getProvider=" + location.getProvider().toString(), Toast.LENGTH_SHORT).show();
	MyLog.d(TAG, "onLocationChanged getProvider=" + location.getProvider().toString());
	
	//if this is a gps update, we can stop listening to the network
	if ((location.getProvider().equals(LocationManager.GPS_PROVIDER)) 
			&& (locationProviderInUse != LocationProviderInUse.GPS)) {
		//remove both listeners and readd GPS again. We no longer need the network.
		//it was just to center the map until the GPS listener started working
	    locationManager.removeUpdates(this);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 10F, this);
	    locationProviderInUse = LocationProviderInUse.GPS;
	}
	
    if( mListener != null )
    {
        mListener.onLocationChanged( location );

        //Move the camera to the user's location once it's available!
        if (mMap != null) {
        	mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
}
/*
@Override
public void onLocationChanged(Location location) 
{
    if( mListener != null )
    {
        mListener.onLocationChanged( location );

        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;

        if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
        {
             //Move the camera to the user's location if they are off-screen!
             mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
}*/

@Override
public void onProviderDisabled(String provider) 
{
    // TODO Auto-generated method stub
    //Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
	MyLog.d(TAG, "onProviderDisabled provider=" + provider);
}

@Override
public void onProviderEnabled(String provider) 
{
    // TODO Auto-generated method stub
    //Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    MyLog.d(TAG, "onProviderEnabled provider=" + provider);
}

@Override
public void onStatusChanged(String provider, int status, Bundle extras) 
{
    // TODO Auto-generated method stub
    //Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
    MyLog.d(TAG, "onStatusChanged provider=" + provider + " status=" + status);
}
}