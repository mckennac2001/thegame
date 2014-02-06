package com.mckenna.thegame;

import java.util.Iterator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GameActivity extends Activity implements OnMapClickListener, 
                                                      //OnMapLongClickListener, 
                                                      OnMarkerClickListener, 
                                                      //OnMarkerDragListener, 
                                                      LocationListener, 
                                                      //LocationSource, 
                                                      Listener {

	private final String TAG = "GameActivity";
	private final float defaultZoomLevel = 8;
	private GoogleMap map;
    private Intent intentData;
    private Game game;
    private String gameId;
	private Database database = new Database();
	private LocationManager locationManager;
	DbPlayerUpdateListener updatePlayerListener;
	DbTargetUpdateListener updateTargetListener;
	MapPopulator mapPopulator;
	float currentZoomLevel = defaultZoomLevel;
	LatLng currentMapFocus;
	float gameResolution = 50;
	boolean quitingGame = false;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		MyLog.d(TAG, "onCreate");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_game);
    	
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        // For the main activity, make sure the app icon in the action bar
	        // does not behave as a button
	        ActionBar actionBar = getActionBar();
	        //actionBar.setHomeButtonEnabled(false);
	    }
        
    	MyLog.d(TAG, "onCreate 1");
    	// Did we get a GameId ?
		intentData = getIntent();
		gameId = intentData.getStringExtra("GAMEID");
    	if (gameId == null) {
    		MyLog.d(TAG, "GAMEID was null");
    		Toast.makeText(this, "GAMEID was null", Toast.LENGTH_SHORT).show();
    		finish();
    	}
    	Singleton.getInstance().setCurrentGameId(gameId);
    	MyLog.d(TAG, "onCreate 2");

		// Store this Listener, we will use it in onResume
        updatePlayerListener = new DbPlayerUpdateListener();

		// Store this Listener, we will use it in onResume
		updateTargetListener = new DbTargetUpdateListener();
		
	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    
		//create map object and register for position updates
		setUpMapIfNeeded();	

		// Create this to draw the pois on the map
		mapPopulator = new MapPopulator(map);

		MyLog.i(TAG, "onCreate end");
	}
		
	@Override
    public void onPause() {
    	MyLog.d(TAG, "onPause");
        super.onPause();
		
        // Store our current map focus and zoom level 
        Singleton.getInstance().setCameraPosition(map.getCameraPosition());
        
        // Stop listening to the GPS while we are in the background
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
        // Stop DB update us when players move
        database.stopPositionUpdates(gameId, updatePlayerListener);
        // Stop DB update us when players move
        database.stopTargetUpdates(gameId, updateTargetListener);
        /* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
    	map.setMyLocationEnabled(false);
    	
    	if (!quitingGame) {
	    	// This will do the games work while the UI is gone
	    	Intent intent = new Intent(this, BackgroundService.class);
	    	intent.putExtra("GAMEID", gameId);
	    	if (gameId != null) {
				startService(intent);
	    	}
			//PendingIntent startPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	}
    }
	
    @Override
    public void onResume() {
    	MyLog.d(TAG, "onResume");
        super.onResume();

	    // Stop the back ground service if it is running
		stopService(new Intent(this, BackgroundService.class));
		
        /* We query for the best Location Provider everytime this fragment is displayed
         * just in case a better provider might have become available since we last displayed it */
        //followMeLocationSource.getBestAvailableProvider();

        // Get a reference to the map/GoogleMap object
        setUpMapIfNeeded();
        
		// Start loading our game from DB (one-shot, async)
        // Once this completes, the callback registers for continuous updates
	    database.getGame(gameId, new DbReadCallbackListener()); 
		
        // These are for us. The Map has its own location provider below
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 5F, this); //LocationListener
	    locationManager.addGpsStatusListener(this); //Listener
	    
	    // Move map camera back to where it was before we lost focus, assuming we have been here before
    	if (Singleton.getInstance().getCameraPosition() == null) {
    		map.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.centerMapOnLastKnownPosition(locationManager), currentZoomLevel));
    	} else {
    		map.moveCamera(CameraUpdateFactory.newCameraPosition(Singleton.getInstance().getCameraPosition()));
    	}
    	
        /* Enable the my-location layer (this causes our LocationSource to be automatically activated.)
         * While enabled, the my-location layer continuously draws an indication of a user's
         * current location and bearing, and displays UI controls that allow a user to interact
         * with their location (for example, to enable or disable camera tracking of their location and bearing).*/
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onStop() {
    	super.onStop();
    	MyLog.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	MyLog.d(TAG, "onDestroy");
    }    
    
    private CameraUpdate CameraUpdateFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated. This will ensure that we only ever
     * manipulate the map once when it {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and {@link com.google.android.gms.maps.MapView
     * MapView}) will show a prompt for the user to install/update the Google Play services APK on their device.
     */
    private void setUpMapIfNeeded() {
    	MyLog.d(TAG, "setUpMapIfNeeded");
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            //create map object
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
            
            // Check if we were successful in obtaining the map.
            if (map != null) {
            	// Add a listener for clicks on the map
            	map.setOnMapClickListener(this);
            	
            	// Add a long click listener 
//            	map.setOnMapLongClickListener(this);
            	
            	// Add a marker click listener
            	map.setOnMarkerClickListener(this);
            	
            	// Add a marker drag listener
//            	map.setOnMarkerDragListener(this);
            	
                // The Map is verified. It is now safe to manipulate the map:
            	map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            	
                // Replace the (default) location source of the my-location layer with our custom LocationSource
                //map.setLocationSource(followMeLocationSource);
            	//map.setLocationSource(new CurrentLocationProvider(this));
            } else {
            	GameAlerts.showError(this, "map is null");
            }
        }
    }
	
	@Override
	public void onMapClick(LatLng point) {
		MyLog.d(TAG, "onMapClick");
		//Toast.makeText(this, "onMapClick at " + point.latitude + "," + point.longitude, Toast.LENGTH_SHORT).show();
		
		float zoomLevel;
		if ((zoomLevel = map.getCameraPosition().zoom) < 21) {		
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, zoomLevel+1); //0 to 21
			map.animateCamera(update);		
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// Center the map on this marker - nope!
		//CameraUpdate update = CameraUpdateFactory.newLatLng(marker.getPosition());
		//map.animateCamera(update);		
		//markerToDelete = marker;
		// Show the marker info along with a delete button
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //DialogFragment newFragment = GameDialog.newInstance("DelPoi", "Delete", "Delete this POI?", marker, this);
        //newFragment.show(ft, "dialog");
		
		// Show a dialog that offers the user a chance to delete this POI
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		ArrayList<String> infoList = (ArrayList<String>) Arrays.asList(marker.getTitle().split(":[ ]*"));
		builder.setTitle(marker.getTitle());
		builder.setMessage(marker.getSnippet());
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {	    	
		        dialog.dismiss();
		    }
		});

		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	public void onClick_Leave_Game(View v) {
		MyLog.d(TAG, "onClick_ButtonCancel");
		Singleton.getInstance().setCurrentGameId(null);
		stopService(new Intent(this, BackgroundService.class));
		setResult(Activity.RESULT_CANCELED);
		quitingGame = true;
		finish();
	}
	public void onClick_Leave_Game(MenuItem m) {
		MyLog.d(TAG, "onClick_ButtonCancel");
		Singleton.getInstance().setCurrentGameId(null);
		stopService(new Intent(this, BackgroundService.class));
		setResult(Activity.RESULT_CANCELED);
		quitingGame = true;
		finish();
	}
	
	private class DbReadCallbackListener implements ValueEventListener { 
		@Override
		public void onCancelled(FirebaseError arg0) { 
			MyLog.d(TAG, "onCancelled");
			MyLog.logFirebaseError(arg0);
		}
	
		@Override
		public void onDataChange(DataSnapshot myGameSn) {
			MyLog.d(TAG, "*** onDataChange Game ***");  
			
			game = new Game(gameId);
			database.parseGame(myGameSn, game, Singleton.getInstance().getMyPlayerId());
			//mapPopulator.populateTargets(game);
			
			// Now register for updates of players positions
			database.startPositionUpdates(gameId, updatePlayerListener);
			// Now register for updates of targets' hit lists
			database.startTargetUpdates(gameId, updateTargetListener);
			
			MyLog.d(TAG, "*** onDataChange Game end ***"); 
		}
	}
	
	private class DbPlayerUpdateListener implements ValueEventListener { 
		@Override
		public void onCancelled(FirebaseError arg0) { 
			MyLog.d(TAG, "onCancelled");
			MyLog.logFirebaseError(arg0);
		}
	
		@Override
		public void onDataChange(DataSnapshot myPositionSn) {
			MyLog.d(TAG, "*** onDataChange Players ***");  
			
			//game = new Game(gameId);
			database.parsePlayersPositions(myPositionSn, game);
			mapPopulator.populatePlayers(game);
			MyLog.d(TAG, "*** onDataChange Players end ***"); 
		}
	}
	
	private class DbTargetUpdateListener implements ValueEventListener { 
		@Override
		public void onCancelled(FirebaseError arg0) { 
			MyLog.d(TAG, "onCancelled");
			MyLog.logFirebaseError(arg0);
		}
	
		@Override
		public void onDataChange(DataSnapshot targetsSn) {
			MyLog.d(TAG, "*** onDataChange Targets ***");  
			
			//game = new Game(gameId);
			database.parseTargets(targetsSn, game);
			mapPopulator.populateTargets(game);
			MyLog.d(TAG, "*** onDataChange Targets end ***"); 
		}
	}
	
/*
	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
*/
	@Override
	public void onLocationChanged(Location location) {
		MyLog.d(TAG, "*** onLocationChanged ***");  
		MyLog.d(TAG, "lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
		
		// Save our position to the database
		database.savePosition(gameId, location);
		
		// Check if we have hit any of the markets
		if (checkForTargetsHit(game, location) == true) {
			// We should update the database with this new exciting info
			database.saveTargets(game);
//			mapPopulator.populateTargets(game);
		} else {
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		}
		
		MyLog.d(TAG, "*** onLocationChanged end ***");
		// Update the database to show everyone I have hit a target

		// the uid of each user will be stored in a list inside the target?
		// To find out which targets i have hit, will mean checking the list of every target.
		// This most important thing is that the marker color on MY map is different if 
		// I have hit it. I need that feedback
		
		// maybe a server component should check who has won a game and provide notifications
		// using another part of the database (like the static)
		// It could also decide if a target has been hit, so the phone wouldnt have to.
		// Well the phone is writting up to the server on each location update anyways.
		// So not a big deal to 
		
		// They will see this by the targets getting more transparent
		// When all players have hit a marker, it goes black
		//  BitmapDescriptorFactory.defaultMarker(float hue)
		// Everytime a target is hit, I think all the targets will be read. So be it
		
/*		(not a priority) * Change targets to be saved like the players, so a separate listiner can be added to them all.
		This will hopefully mean that only that one target is updated when it is hit.
		We could add separate listiners for each palyer too.
		But will it really limit the data that is transfered?
		Maybe all the separate requests would be a bigger headache. like when the 
		screen is rotated we might have a separate update from each target.
		So. its not a priority , its an optimisation
	*/	
	}

	@Override
    public void onProviderDisabled(String provider)
    {
    	MyLog.d(TAG, "onProviderDisabled Provider=" + provider);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    	MyLog.d(TAG, "onProviderEnabled Provider=" + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    	MyLog.d(TAG, "onStatusChanged " + provider + ", Status=" + status);
    }

	@Override
	public void onGpsStatusChanged(int event) {
		/*
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite>sat = satellites.iterator();
            int i=0;
            String strGpsStats = "";
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
            }
            MyLog.d(TAG, strGpsStats);
        }	*/	
		
		//MyLog.d(TAG, "onGpsStatusChanged event=" + event);  
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
