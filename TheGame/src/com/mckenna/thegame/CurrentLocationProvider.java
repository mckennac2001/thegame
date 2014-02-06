package com.mckenna.thegame;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.LocationSource;

public class CurrentLocationProvider implements LocationSource, LocationListener
{
	private final String TAG = "CurrentLocationProvider";
    private OnLocationChangedListener listener;
    private LocationManager locationManager;

    public CurrentLocationProvider(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void activate(OnLocationChangedListener listener)
    {
    	MyLog.d(TAG, "activate");
        this.listener = listener;
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if(gpsProvider != null)
        {
            locationManager.requestLocationUpdates(gpsProvider.getName(), 0, 10, this);
        }

        LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);;
        if(networkProvider != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 5, 0, this);
        }
    }

    @Override
    public void deactivate()
    {
    	MyLog.d(TAG, "deactivate");
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
    	MyLog.d(TAG, "onLocationChanged Provider=" + location.getProvider());
        if(listener != null)
        {
            listener.onLocationChanged(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    	MyLog.d(TAG, "onProviderDisabled Provider=" + provider);
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider)
    {
    	MyLog.d(TAG, "onProviderEnabled Provider=" + provider);
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    	MyLog.d(TAG, "onStatusChanged " + provider + ", Status=" + status);
        // TODO Auto-generated method stub

    }
}
