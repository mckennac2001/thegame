package com.mckenna.thegame;

import java.util.Iterator;

import android.content.Context;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

// This runs in its own thread an does the DB work
public class BackgroundProcessor  implements LocationListener, Listener {
	
	private final String TAG = "BackgroundProcessor";
	private float gameResolution = 50;
	LocationManager locationManager;
	Context context;
    private Game game;
    private String gameId;
	private Database database = new Database();
	DbTargetUpdateListener updateTargetListener;
	private boolean shouldBeActive = false;
	
	public BackgroundProcessor(Context context, String gameId) {
		this.context = context;
		this.gameId = gameId;
		// Get the location manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// Start fetching this game from the database
	    database.getGame(gameId, new DbReadCallbackListener()); 
		// Store this Listener, we will use it in onDestroy
		updateTargetListener = new DbTargetUpdateListener();
		// Set the state
		shouldBeActive = true;
	}
	
	public void stop() {
		// Stop the async callbacks from doing anything.
		shouldBeActive = false;
		stopGpsListener();
		stopDatabaseListener();
	}
	
	private void startGpsListener() {
		MyLog.d(TAG, "startGpsListener");
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 5F, this); //LocationListener
	    locationManager.addGpsStatusListener(this); //Listener
	}

	private void stopGpsListener() {
		MyLog.d(TAG, "stopGpsListener");
	    locationManager.removeUpdates(this);
	    locationManager.removeGpsStatusListener(this);		
	}

	private void startDatabaseListener() {
		MyLog.d(TAG, "startDatabaseListener");
		// Register for updates of targets' hit lists
		database.startTargetUpdates(gameId, updateTargetListener);	
		// Register for game winners or other game level events we will notify about
	}
	
	private void stopDatabaseListener() {
		MyLog.d(TAG, "stopDatabaseListener");
        database.stopTargetUpdates(gameId, updateTargetListener);	
	}
	
	@Override
	public void onGpsStatusChanged(int event) {
        switch (event) {
        case GpsStatus.GPS_EVENT_STARTED:
            //MyLog.d(TAG, "GPS_EVENT_STARTED");
            break;
        case GpsStatus.GPS_EVENT_FIRST_FIX:
            MyLog.d(TAG, "GPS_EVENT_FIRST_FIX");
            break;
        case GpsStatus.GPS_EVENT_STOPPED:
            //MyLog.d(TAG, "GPS_EVENT_STOPPED");
            break;
        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
            //MyLog.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
            break;
        default:
            MyLog.d(TAG, "SOMETHING_ELSE");
            break;
        }
	}

	@Override
	public void onLocationChanged(Location location) {

		if (shouldBeActive) {
			MyLog.d(TAG, "### onLocationChanged ###");  
			MyLog.d(TAG, "lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
			// Save our new position to the database for all to see.
			database.savePosition(gameId, location);
			
			// Check if we have hit any of the markets
			if (checkForTargetsHit(game, location) == true) {
				// We should update the database with this new exciting info
				database.saveTargets(game);
			}
			MyLog.d(TAG, "### onLocationChanged end ###"); 
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	private class DbReadCallbackListener implements ValueEventListener { 
		@Override
		public void onCancelled(FirebaseError arg0) { 
			MyLog.logFirebaseError(arg0);
		}
	
		@Override
		public void onDataChange(DataSnapshot myGameSn) {
			
			if (shouldBeActive) {
				MyLog.d(TAG, "### onDataChange Game ### ");  
				
				// This is where out local game is created
				game = new Game(gameId);
				database.parseGame(myGameSn, game, Singleton.getInstance().getMyPlayerId());
				// Now its ok to register for DB and GPS updates, because we will now have somewhere to save them
				// Register for updates of targets' hit lists
				startDatabaseListener();
				// Start listening to the GPS position
				startGpsListener();
				
				MyLog.d(TAG, "### onDataChange Game end ### "); 
			}
		}
	}

	private class DbTargetUpdateListener implements ValueEventListener { 
		@Override
		public void onCancelled(FirebaseError arg0) { 
			MyLog.logFirebaseError(arg0);
		}
	
		@Override
		public void onDataChange(DataSnapshot targetsSn) {
			if (shouldBeActive) {
				MyLog.d(TAG, "### onDataChange Targets ### ");  
				// Here we just want to keep up to date with the Targets so we 
				// can write to them if we happen to hit one.
				database.parseTargets(targetsSn, game);
				MyLog.d(TAG, "### onDataChange Targets end ### "); 
			}
		}
	}	

    private boolean checkForTargetsHit(Game thisGame, Location myLocation) {
		MyLog.d(TAG, "checkForTargetsHit");		

    	//map.
		boolean ret = false;
		PoiList targetList = thisGame.getTargets();
		Location targetLocation = new Location("dummy");

		if (targetList == null) return ret;
    	// Loop through all the targets in the Game object and see if we are closer
    	// than gameResolution to each one.		
		Iterator<Poi> ti = targetList.iterator(); 
		while (ti.hasNext()) {
			
			Poi target = ti.next();
			targetLocation.setLatitude(target.getLatitude());
			targetLocation.setLongitude(target.getLongitude());
			
			if (myLocation.distanceTo(new Location(targetLocation)) < gameResolution) {
				// We have hit one. 
				// We need to store this in the db so others can see it.
				// We need to redraw the map so it can take this into account.
				target.addHitter(Singleton.getInstance().getMyPlayerId());
				ret = true;
			}
		}
		return ret;
	}
}
