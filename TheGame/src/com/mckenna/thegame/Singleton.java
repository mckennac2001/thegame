package com.mckenna.thegame;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.CameraPosition;


public class Singleton {
	private static Singleton objSingleton;
	private String myPlayerId;
	private String myPlayerName;
	private CameraPosition cameraPosition;
	private String currentGameId;
	private Firebase ref;

	private Singleton() {
		ref = new Firebase("https://the-game.firebaseio.com");
	}

	public static final String FILENAME = "TheGame.preferences";
	
	public static Singleton getInstance() {

		if (objSingleton == null) {
			objSingleton = new Singleton();
		}
		return objSingleton;
	}
	
	public String getMyPlayerId() {
		return myPlayerId;
	}

	public void setMyPlayerId(String myPlayerId) {
		this.myPlayerId = myPlayerId;
	}

	public String getMyPlayerName() {
		return myPlayerName;
	}

	public void setMyPlayerName(String myPlayerName) {
		this.myPlayerName = myPlayerName;
	}

	public CameraPosition getCameraPosition() {
		return cameraPosition;
	}

	public void setCameraPosition(CameraPosition cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public String getCurrentGameId() {
		return currentGameId;
	}

	public void setCurrentGameId(String currentGameId) {
		this.currentGameId = currentGameId;
	}

	public Firebase getRef() {
		return ref;
	}

	public void setRef(Firebase ref) {
		this.ref = ref;
	}
}
