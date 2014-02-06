package com.mckenna.thegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPopulator {

	private final String TAG = "MapPopulator";
	private List<Marker> playerMarkerList = new ArrayList<Marker>();
	private List<Marker> targetMarkerList = new ArrayList<Marker>();
	GoogleMap map;
	
	MapPopulator (GoogleMap map) {
		this.map = map;
	}

    private PoiList removePoisThatDontNeedToBeRedrawn(PoiList poiList, List<Marker> markerList) {
		MyLog.d(TAG, "removePoisThatDontNeedToBeRedrawn");
		
		// We need to make a copy to avoid concurrentmodification exceptions
		PoiList poisToDraw = (PoiList) poiList.clone();
		// We have some pois drawn on the screen already. Who should we remove?			
		Poi p; Marker m;
		
		Iterator<Poi> pi = poisToDraw.iterator(); 
		while (pi.hasNext()) {
			p = pi.next(); 
			
			// We are ignoring this no matter what it is
			if (p.getLatitude() == 0 && p.getLongitude() == 0) {
				pi.remove();
				continue;
			}
			Iterator<Marker> mi = markerList.iterator(); 
			while (mi.hasNext()) {
				m = mi.next();
				// Does an incoming poi match with existing marker?
	    		if (p.getId().equals(m.getTitle())) {
	    			//MyLog.d(TAG, "populateMarkers " + p.getId() + " = " + m.getTitle());
	    			// And has it moved?
	    			if ( (p.getLatitude() == m.getPosition().latitude && 
	    			      p.getLongitude() == m.getPosition().longitude) &&
	    			     (p.getHitters() == null) ) {
	    				// This poi hasn't moved, there is no reason to delete and re draw the marker
	    				// Noone has hit it either, so there is no chance we need to change the colour
	    				pi.remove();
	    				MyLog.d(TAG, "populateMarkers removing an unchanged poi");
	    			} else {
	    				// Has moved or maybe it has a different colour now, 
	    				// so remove the old marker from the map so we can draw the new one
	        			m.remove();
	        			MyLog.d(TAG, "populateMarkers removing old marker " + p.getName());
	        			// Remove this marker from the list of markers stored in external lists
	        			mi.remove();
	        			break;
	    			}
	    		}
			}
		}
		return(poisToDraw);
    }
    
    public void populateTargets(Game game) {
    	MyLog.d(TAG, "populateTargets");
    	// The MarkerList is altered by the function
    	PoiList poisToDraw = removePoisThatDontNeedToBeRedrawn(game.getTargets(), targetMarkerList);	
    	
		Iterator<Poi> it = poisToDraw.iterator(); 
		while(it.hasNext()) {
			
			Poi poi = it.next();
			//create marker options
			MarkerOptions newMarkerOptions = calculateMarkerOptions(game.getPlayers(), poi);
			
			// Store a reference to the target markers added
			targetMarkerList.add(map.addMarker(newMarkerOptions));
			MyLog.d(TAG, "populateTargets addMarker name=" + poi.getName() 
					+ ", lat=" + poi.getLatitude() + ", lon=" + poi.getLongitude());
		}
    }
  
    public void populatePlayers(Game game) {
    	MyLog.d(TAG, "populatePlayers");
    	// The MarkerList is altered by the function
    	PoiList poisToDraw = removePoisThatDontNeedToBeRedrawn(game.getPlayers(), playerMarkerList);	
    	
		Iterator<Poi> it = poisToDraw.iterator(); 
		while(it.hasNext()) {
			
			Poi poi = it.next();
			//create marker options
			MarkerOptions newMarkerOptions = new MarkerOptions()
		       .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
		       .title(poi.getId())
		       .snippet(poi.getName() + " Speed=" + poi.getSpeedHeight())
		       .alpha((float) 0.7)
		       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			
			MyLog.d(TAG, "populatePlayers Snippet: " + poi.getName() + " Speed=" + poi.getSpeedHeight());
			
			// Store a reference to the target markers added
			playerMarkerList.add(map.addMarker(newMarkerOptions));
			MyLog.d(TAG, "populatePlayers addMarker name=" + poi.getName() 
					+ ", lat=" + poi.getLatitude() + ", lon=" + poi.getLongitude());
		}
    }	
	
    private MarkerOptions calculateMarkerOptions(PoiList players, Poi poi) {
    	MyLog.d(TAG, "calculateMarkerOptions");
    	
    	float colour = BitmapDescriptorFactory.HUE_AZURE;
    	float alpha = 1;
    	String snippet = "Hit by ";
    	
    	// Has the target been hit?
    	if (poi.getHitters() != null) {
    		// Have I hit it?
    		if (poi.getHitters().contains(Singleton.getInstance().getMyPlayerId())) {
    			colour = BitmapDescriptorFactory.HUE_GREEN;
    			snippet += Singleton.getInstance().getMyPlayerName() + " , ";
    			MyLog.d(TAG, "I've hit this!");
    		} else {
    			// Well someone else has, it wouldn't be not null otherwise
    			colour = BitmapDescriptorFactory.HUE_YELLOW;
    		}
    		
    		// Loop through the players and if they hit, save their corresponding proper name
    		for (Poi player: players) {
    			if (poi.getHitters().contains(player.getId())) {
    				snippet += player.getName() + " , ";
    				MyLog.d(TAG, "Sippet: Player name=" + player.getName() + ", id=" + player.getId() + " has hit this marker");
    			}
    		}
    	}  else {
    		snippet += "nobody yet";
    	}
    	// Transparency depends on how many of the players have hit the target
    	//alpha = 1 - numOfHitters/players.size();
    	
    	MyLog.d(TAG, "Sippet: " + snippet);
    	
		MarkerOptions newMarkerOptions = new MarkerOptions()
	       .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
	       .title(poi.getId())
	       .snippet(snippet)
	       .alpha(alpha)
	       .icon(BitmapDescriptorFactory.defaultMarker(colour));
		
		return newMarkerOptions;
    }
}
