package com.mckenna.thegame;

import java.util.List;

import android.util.Log;

public class Game {
	private final String TAG = "Game";
	public static enum gameType {JUSTME, BYINVITE, PUBLIC};
	private gameType type;
	private String id;
	private String name;
	private String location;
	private PoiList targets;
	private PoiList players;
	
	public Game() {
		// Assign a UUID for this new game
		//id = UUID.randomUUID().toString();
		// A game is not real until it has had its id assigned
	}
	
	public Game(String id) {
		this.id = id;
	}
	
	/**
	 * @return the gameType
	 */
	public gameType getType() {
		return type;
	}
	/**
	 * @param gameType the gameType to set
	 */
	public void setType(gameType type) {
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the poiList
	 */
	public PoiList getTargets() {
		return targets;
	}
	/**
	 * @param poiList the poiList to set
	 */
	public void setTargets(PoiList targets) {
		this.targets = targets;
	}

	public PoiList getPlayers() {
		return players;
	}
	/**
	 * @param poiList the poiList to set
	 */
	public void setPlayers(PoiList players) {
		this.players = players;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

/*	// Given a PoiList
	public void addTargets(PoiList targetsToAdd) {
		MyLog.d(TAG, "addTargets");
		if (targetsToAdd == null) {
			MyLog.e(TAG, "targetsToAdd is null");
		} else if (targets == null) {
			targets = targetsToAdd;
		} else {
			targets.add(targetsToAdd);
		}
	}*/

	public void addTargets(PoiList targetsToAdd) {
		MyLog.d(TAG, "addTargets");
		if (targetsToAdd == null) {
			MyLog.e(TAG, "targetsToAdd is null");
		} else if (targets == null) {
			targets = targetsToAdd;
		} else {
			targets.removeAll(targetsToAdd);
			targets.addAll(targetsToAdd);
		}
	}

	public void addTarget(Poi target) {
		MyLog.d(TAG, "addTarget");
		if (targets == null) {
			targets = new PoiList();
		}
		targets.remove(target);
		targets.add(target);
	}

	public void deleteTargets(PoiList targetsToDelete) {
		MyLog.d(TAG, "deleteTargets");
		if (targetsToDelete == null) {
			MyLog.e(TAG, "targetsToDelete is null");
		} else if (targets == null) {
			MyLog.d(TAG, "targets is null");
		} else {
			targets.delete(targetsToDelete);
		}
	}
	
	// For the Firebase DB list reader
	public void setTargets(List<Poi> newTargetList) {
		MyLog.d(TAG, "setTargets");
		if (newTargetList == null) {
			MyLog.e(TAG, "newTargetList is null");
		} else {
			targets = new PoiList();
			targets.addAll(newTargetList);
		} 
	}
	
	public void addPlayers(PoiList playersToAdd) {
		MyLog.d(TAG, "addPlayers");
		if (playersToAdd == null) {
			MyLog.e(TAG, "playersToAdd is null");
		} else if (players == null) {
			players = playersToAdd;
		} else {
			players.removeAll(playersToAdd);
			players.addAll(playersToAdd);
		}
	}

	public void addPlayer(Poi player) {
		MyLog.d(TAG, "addPlayer");
		if (players == null) {
			players = new PoiList();
		}
		// Before we add a player, we should delete it from the list if it already exists
/*		if (players.contains(player)) {
			int i = players.indexOf(player);
			players.remove(i);
		} */
		players.remove(player);
		players.add(player);
	}

	public void deletePlayers(PoiList playersToDelete) {
		MyLog.d(TAG, "deletePlayers");
		if (playersToDelete == null) {
			MyLog.e(TAG, "playersToDelete is null");
		} else if (players == null) {
			MyLog.d(TAG, "players is null");
		} else {
			players.delete(playersToDelete);
		}
	}

	// For the Firebase DB list reader
	public void setPlayers(List<Poi> newPlayerList) {
		MyLog.d(TAG, "setPlayers");
		if (newPlayerList == null) {
			MyLog.e(TAG, "players is null");
		} else {
			players = new PoiList();
			players.addAll(newPlayerList);
		}	
	}	
}
