package com.mckenna.thegame;

import java.util.Iterator;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class AddPoisActivity extends Activity implements OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener, OnMarkerDragListener {

	private final String TAG = "AddPois";
	private GoogleMap map;
    private FollowMeLocationSource followMeLocationSource;
    private Marker markerToDelete;
    private String defaultName;
    private Intent intentData;
	private PoiList targetAddList = new PoiList();
	private PoiList targetDeleteList = new PoiList();
	private PoiList targetDrawList = new PoiList();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pois);
    	
    	MyLog.d(TAG, "onCreate 1");
		intentData = getIntent();
		defaultName = intentData.getStringExtra("NAME");
    	if (defaultName == null) {
    		defaultName = "Empty Game Name";
    		GameAlerts.showError(this, "AddPoisActivity, intentData.getStringExtra was null");
    	}
    	MyLog.d(TAG, "onCreate 2");
    	Bundle poiBundle = intentData.getBundleExtra("TARGETS");
    	if (poiBundle != null) {
    		targetDrawList = poiBundle.getParcelable("TargetsToDraw");
    	} else {
    		GameAlerts.showError(this, "AddPoisActivity, intentData.getBundleExtra was null");
    	}
    	MyLog.d(TAG, "onCreate 3");
    	
		//create map object
		setUpMapIfNeeded();	
		
        // Add pois to the map
        //populateMap();
        
        // creates our custom LocationSource and initializes some of its members
        followMeLocationSource = new FollowMeLocationSource(map, this, null);
        
        //GameAlerts.showError(this, "AddPois onCreate");
		MyLog.i(TAG, "onCreate");
	}
	
    @Override
    public void onResume() {
    	MyLog.d(TAG, "onResume");
        super.onResume();

        /* We query for the best Location Provider everytime this fragment is displayed
         * just in case a better provider might have become available since we last displayed it */
        followMeLocationSource.getBestAvailableProvider();

        // Get a reference to the map/GoogleMap object
        setUpMapIfNeeded();

        // Add pois to the map
        populateMap();
        
        /* Enable the my-location layer (this causes our LocationSource to be automatically activated.)
         * While enabled, the my-location layer continuously draws an indication of a user's
         * current location and bearing, and displays UI controls that allow a user to interact
         * with their location (for example, to enable or disable camera tracking of their location and bearing).*/
        map.setMyLocationEnabled(true);
    }

    private void populateMap() {
    	MyLog.d(TAG, "populateMap");
		if (targetDrawList == null) return;
		
		Iterator<Poi> it = targetDrawList.iterator(); 
		while(it.hasNext()) {
			
			Poi target = it.next();
			//create marker options
			MarkerOptions newMarkerOptions = new MarkerOptions()
		       .position(new LatLng(target.getLatitude(), target.getLongitude()))
		       .title(target.getId())
		       //.snippet(target.getName())
		       .snippet("populateMap")
			   //.icon(yellowBitmap);
		       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			
			map.addMarker(newMarkerOptions);
			MyLog.d(TAG, "populateMap addMarker latitude=" + target.getLatitude() + ", longtude=" + target.getLongitude());
		}
		
	}

	@Override
    public void onPause() {
    	MyLog.d(TAG, "onPause");
        super.onPause();
        
        /* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
    	map.setMyLocationEnabled(false);
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
            	map.setOnMapLongClickListener(this);
            	
            	// Add a marker click listiner
            	map.setOnMarkerClickListener(this);
            	
            	// Add a marker drag listiner
            	map.setOnMarkerDragListener(this);
            	
                // The Map is verified. It is now safe to manipulate the map:
            	map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            	
                // Replace the (default) location source of the my-location layer with our custom LocationSource
                map.setLocationSource(followMeLocationSource);
/*
                CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
                // Set default zoom
                map.moveCamera(CameraUpdateFactory.zoomTo(15f));
                */
        		
        		//CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_HOME, 8); //0 to 21
        		//map.animateCamera(update);	
            } else {
            	GameAlerts.showError(this, "map is null");
            }
        }
    }
	
	@Override
	public void onMapClick(LatLng point) {
		MyLog.d(TAG, "onMapClick");
		//Toast.makeText(this, "onMapClick at " + point.latitude + "," + point.longitude, Toast.LENGTH_SHORT).show();
		
		/*float zoomLevel;
		if ((zoomLevel = map.getCameraPosition().zoom) < 21) {		
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, zoomLevel+1); //0 to 21
			map.animateCamera(update);		
		}*/
	}

	@Override
	public void onMapLongClick(LatLng point) {
		MyLog.d(TAG, "onMapLongClick");
		MyLog.d(TAG, "onMapLongClick at " + point.latitude + "," + point.longitude);
		//Toast.makeText(this, "onMapLongClick at " + point.latitude + "," + point.longitude, Toast.LENGTH_SHORT).show();
			
		// Zoom in if we are too far out to create a poi
		float zoomLevel;
		if ((zoomLevel = map.getCameraPosition().zoom) < 5) {	
			// We are too far out to create an accurate poi, zoom in
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, zoomLevel+1); //0 to 21
			map.animateCamera(update);		
		} else {		
			double evelvation = 0;
			// Get this points elevation from Google API
//			LatLng []points
			//new Elevation().execute(point);
			// Create a marker at this location, we are zoomed enough to be accurate
			MarkerOptions newMarkerOptions = new MarkerOptions()
		       .position(point)
		       .title(UUID.randomUUID().toString()) //used to store the id
		       //.snippet(defaultName + " Elevation=" + evelvation)
		       .snippet("onMapLongClick")
			   //.icon(yellowBitmap);
		       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			
			Marker markerAdded = map.addMarker(newMarkerOptions);
					
			Poi target = new Poi(markerAdded, evelvation); // Create target at this location and add it to the list
			targetAddList.add(target); // Add this poi to the list of pois to save
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// Center the map on this marker
		CameraUpdate update = CameraUpdateFactory.newLatLng(marker.getPosition());
		map.animateCamera(update);		
		markerToDelete = marker;
		// Show the marker info along with a delete button
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //DialogFragment newFragment = GameDialog.newInstance("DelPoi", "Delete", "Delete this POI?", marker, this);
        //newFragment.show(ft, "dialog");
		
		// Show a dialog that offers the user a chance to delete this POI
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(markerToDelete.getTitle() + " " + markerToDelete.getSnippet());
		builder.setMessage("Delete this Target?");

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int which) {
				// Create target at this location 
				Poi target = new Poi(markerToDelete);
				
				// Delete this locally if it is one of the ones we have just added
				if (!targetAddList.remove(target)) {
					// Add this target to the list of targets to delete from the database
					targetDeleteList.add(target);
				}
				
		    	// Delete this marker
		    	if (markerToDelete != null) markerToDelete.remove();
		    	markerToDelete = null;
		    	
		        dialog.dismiss();
		    }
		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // Don't delete it
		        dialog.dismiss();
		    }
		});

		AlertDialog alert = builder.create();
		alert.show();
		
		return false;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// Set the market to be in this new location
		
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// no idea
		
	}
	
	public void onClick_ButtonSave(View v) {
		MyLog.d(TAG, "onClick_ButtonSave");

		// We need a bundle to add the pois to an Intent
		Bundle poiBundle = new Bundle();
		// List of Targets we just added
		poiBundle.putParcelable("TargetsToAdd", targetAddList);
		// List of Targets we just deleted
		poiBundle.putParcelable("TargetsToDelete", targetDeleteList);
		
		// Create an Intent to hold the return data
		Intent returnIntent = new Intent(this, NewGameActivity.class);
		returnIntent.putExtra("TARGETS", poiBundle);
		
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	public void onClick_ButtonCancel(View v) {
		MyLog.d(TAG, "onClick_ButtonCancel");
		setResult(Activity.RESULT_CANCELED);
		finish();
	}
	
}
