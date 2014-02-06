package com.mckenna.thegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.location.Location;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;

public class Database {
	
	private static final String TAG = "Database";
	Game game; // Needed so the async db callbacks can read the game data
	Firebase refPlayerUpdates = null;
	Firebase refTargetUpdates = null;
	Firebase baseRef = Singleton.getInstance().getRef();
	
	public Database() {
		//game = new Game();	
	}
	
	// Start Login process
	public static void startLogin(SimpleLoginAuthenticatedHandler callback) {
		MyLog.d(TAG, "startLogin");
		try {
			SimpleLogin authClient = new SimpleLogin( Singleton.getInstance().getRef() );
			authClient.loginWithEmail(Singleton.getInstance().getMyPlayerId(), "thegame", callback);			
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);			
		}

	}
/*	
	// Are we already logged in?
	public static void startLoginCheck(SimpleLoginAuthenticatedHandler callback) {
		try {
			MyLog.d(TAG, "startLoginCheck");
			SimpleLogin authClient = new SimpleLogin( Singleton.getInstance().getRef() );
			authClient.checkAuthStatus(callback);
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);			
		}
	}	 
*/	
	// Used for writing Game details to the DB
	private static class GameDetails {
	    private String game_name;
	    private String game_location;
	    private int game_number_of_players;

	    private GameDetails() { }
	    public GameDetails(String game_name, String game_location, int game_number_of_players) {
	        this.game_name = game_name;
	        this.game_location = game_location;
	        this.game_number_of_players = game_number_of_players;
	    }
	    public String getGame_name() { return game_name; }
	    public String getGame_location() { return game_location; }
	    public int getGame_number_of_players() { return game_number_of_players; }
	}
	
	// Used for reading and writing Player details to the DB
	private static class PoiDetails {
	    private String id;
	    private String name;
	    private double latitude;
	    private double longitude;
	    private double speedHeight;

	    private PoiDetails() { }
	    public PoiDetails(String id, String name) { 
	    	this.id = id;
	        this.name = name;	    	
	    }
	    public PoiDetails(String id, String name, double latitude, 
	    		double longitude, double speedHeights) {
	    	this.id = id;
	        this.name = name;
	        this.latitude = latitude;
	        this.longitude = longitude;
	        this.speedHeight = speedHeight;
	    }
	    public String getId() { return id; }
	    public String getName() { return name; }
	    public double getLatitude() { return latitude; }
	    public double getLongitude() { return longitude; }
	    public double getSpeedHeight() { return speedHeight; }
	}
	
	public void getGame(String aGameId, ValueEventListener listener) {
		MyLog.d(TAG, "getGame");
		// Create a reference to a Firebase location
		Firebase ref = baseRef.child("/dynamic/games/" + aGameId);
		ref.addListenerForSingleValueEvent(listener);
	}
			
	// Update the game object with the full game information from the DB
    public void parseGame(DataSnapshot myGameSn, Game game, String myPlayerId) {
    	MyLog.d(TAG, "parseGame");    	
    	
		try {
    		// Get the game name from the DB		    		
    		DataSnapshot game_name = myGameSn.child("/game_name");
    		game.setName(game_name.getValue().toString());
    		// Get the game location from the DB
	    	DataSnapshot game_location = myGameSn.child("/game_location");
	    	game.setLocation(game_location.getValue().toString());
	    	MyLog.d(TAG, "name= " + game.getName() + "\nlocation= " + game.getLocation());

	    	// Fetch the list of Targets from the DB and store locally
	    	DataSnapshot game_targets = myGameSn.child("/game_targets");
	    	readTargets(game_targets, game);
	    	
	    	// Fetch the list of Players from the DB and store locally
	    	DataSnapshot game_players = myGameSn.child("/game_players");
	    	readPlayers(game_players, game, myPlayerId);
	    	
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);			
		}
		
        MyLog.d(TAG, "parseGame end"); 
    }

    // The one place we parse out the players from the DB snapshot
    public void readPlayers(DataSnapshot game_players, Game game, String myPlayerId) throws FirebaseException, NullPointerException {
    	MyLog.d(TAG, "readPlayers");  
    	// Fetch the list of Players for this game and their positions
    	for (DataSnapshot aPlayerSn : game_players.getChildren()) { 
    		// The actual database read
    		PoiDetails player = aPlayerSn.getValue(PoiDetails.class);
    		// Remove me from this list. No need to draw in pin on me 
    		if (!player.getId().equalsIgnoreCase(myPlayerId)) {
    			game.addPlayer(new Poi(player.id, player.getName(), player.latitude, 
	                       player.longitude, player.speedHeight));
    		}
    	}	   	
    }
    
    // Write players from a game object to the DB
    public void savePlayers(Firebase ref, Game game) throws FirebaseException, NullPointerException {
    	MyLog.d(TAG, "savePlayers");  
    	if (game.getPlayers() != null) {
			// Iterate through our list of players and create them separately
			Iterator<Poi> it = game.getPlayers().iterator();
			while (it.hasNext()) {
				Poi p = it.next();
				Firebase refPlayer = ref.child("game_players/" + Integer.toString(p.getId().hashCode()));
				// We use POIDETAILS to create the format of the DB
				refPlayer.setValue(new PoiDetails(p.getId(), p.getName(), 1, 1, 1));
			}		
    	}
    }    
    
    // The one place we parse out the targets from the DB snapshot
    public void readTargets(DataSnapshot game_targets, Game game) throws FirebaseException, NullPointerException {
    	MyLog.d(TAG, "readTargets");  
		// Required for reading lists from the DB
		GenericTypeIndicator<List<Poi>> t = new GenericTypeIndicator<List<Poi>>() {};
		// Fetch the list of Targets for this game
		game.setTargets(game_targets.getValue(t));
    }
    
    // Write targets from a game object to the DB
    public void saveTargets(Firebase ref, Game game) throws FirebaseException, NullPointerException {
    	MyLog.d(TAG, "saveTargets");  
		// Add the list of Targets to the DB
		if (game.getTargets() != null) {
			// We save the Targets to DB based upon the data format of the POI
			ref.child("game_targets").setValue(game.getTargets());
		}
    }
     
	// Update the game object with the players positions
    public void parseTargets(DataSnapshot game_targets, Game game) {
    	MyLog.d(TAG, "parseTargets");    	
    	
		try {
	    	readTargets(game_targets, game);	
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);	
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);				
		}
		
        MyLog.d(TAG, "parseTargets end"); 
    }
    
    // Permanently register for target updates
	public void startTargetUpdates(String aGameId, ValueEventListener listener) {
		MyLog.d(TAG, "startTargetUpdates");
		// Create a reference to a Firebase location and store it, we will use this to remove the listener
		refTargetUpdates = baseRef.child("/dynamic/games/" + aGameId + "/game_targets");
		refTargetUpdates.addValueEventListener(listener);
	}

	// Stop listening for player and target position updates
	public void stopTargetUpdates(String aGameId, ValueEventListener listener) {
		MyLog.d(TAG, "stopTargetUpdates");
		if (refTargetUpdates != null && listener != null) {
			refTargetUpdates.removeEventListener(listener);
		}
	}
	
    // Permanently register for position updates
	public void startPositionUpdates(String aGameId, ValueEventListener listener) {
		MyLog.d(TAG, "startPositionUpdates");
		// Create a reference to a Firebase location and store it, we will use this to remove the listener
		refPlayerUpdates = baseRef.child("/dynamic/games/" + aGameId + "/game_players");
		refPlayerUpdates.addValueEventListener(listener);
	}
	
	// Stop listening for player and target position updates
	public void stopPositionUpdates(String aGameId, ValueEventListener listener) {
		MyLog.d(TAG, "stopPositionUpdates");
		if (refPlayerUpdates != null && listener != null) {
			refPlayerUpdates.removeEventListener(listener);
		}
	}
	
	// Update the game object with the players positions
    public void parsePlayersPositions(DataSnapshot game_players, Game game) {
    	MyLog.d(TAG, "parsePlayersPositions");    	
    	
		try {
			readPlayers(game_players, game, Singleton.getInstance().getMyPlayerId());
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);				
		}
        MyLog.d(TAG, "parsePlayersPositions end"); 
    }
    
	public void saveGame(Game newGame) {
		MyLog.d(TAG, "saveGame");
		
		try {
			// Create a reference to a Firebase location
			Firebase ref = baseRef.child("/dynamic/games/");
			// Save the basic game structure to the DB
			ref.child(newGame.getId()).setValue(new GameDetails(newGame.getName(), newGame.getLocation(), newGame.getPlayers().size()));
			// Change reference to the game we just created
			ref = baseRef.child("/dynamic/games/" + newGame.getId());
			// Add the list of Targets to the DB, based upon the format of POI
			saveTargets(ref, newGame);
			// Add the list of Players to the DB
			savePlayers(ref, newGame);

		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "null fire");
			MyLog.logException(e);				
		}
		MyLog.d(TAG, "saveGame end");
	}	
	
	public void getListOfGameIds(ValueEventListener listener) {
		MyLog.d(TAG, "getListOfGameIds");

		// Create a reference to a Firebase location
		Firebase ref = baseRef.child("/dynamic/games/");
		ref.addListenerForSingleValueEvent(listener);
	}
	
	// Update the avaivableGames array with ids and names of each game
    public void parseGameIds(DataSnapshot gamesSn, ArrayList<Pair<String, String>> avaivableGames, String myPlayerId) {
    	MyLog.d(TAG, "parseGameIds");    	
    	
		try {
	    	// Loop through the children on this snapshot
			for (DataSnapshot aGameSn : gamesSn.getChildren()) { 
				
				String gameId = aGameSn.getName();
				MyLog.d(TAG, "parseGameIds id=" + gameId); 
				
	    		DataSnapshot game_name = aGameSn.child("/game_name");
	    		String name = game_name.getValue().toString();	
				MyLog.d(TAG, "parseGameIds name=" + name); 
				
				avaivableGames.add(new Pair<String, String>(name, gameId));			
			}
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "exception null");
			MyLog.logException(e);			
		}
        MyLog.d(TAG, "parseGameIds end"); 
    }

	public void savePosition(String gameId, Location location) {
		MyLog.d(TAG, "saveMyamePosition");
		
		try {
			// Create a reference to a Firebase ref to my player in this game
			Firebase ref = baseRef.child("/dynamic/games/" + gameId + 
					"/game_players/" + Integer.toString(Singleton.getInstance().getMyPlayerId().hashCode()));

			// How to find out child in the list?
			// We dont want to have to serch through the whole list each time. 
			// Or we could use it to get other players updates while we are here?
			// But that would make it async if we do a read.
			// We need to have stored our position in the list. It will not change once a game has started.
			// It should be stored in a local file, related to the current game we are involved in.
			// The same place the current game id is stored. In prefrences file?
			// Solved by putting hash values on emails as they subkeys
			// To optimise updates to other users, we should update them all at ones.
			// We use POIDETAILS to create the format of the DB
			ref.setValue(
					new PoiDetails(Singleton.getInstance().getMyPlayerId(), 
					               Singleton.getInstance().getMyPlayerName(), 
					               location.getLatitude(),
					               location.getLongitude(),
					               location.getSpeed()
					)
			);
			
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "null fire");
			MyLog.logException(e);				
		}
		MyLog.d(TAG, "saveMyamePosition end");
	}

	
	public void saveTargets(Game thisGame) {
		MyLog.d(TAG, "saveTargets");
		
		try {
			// Create a reference to a Firebase location
			Firebase ref = baseRef.child("/dynamic/games/" + thisGame.getId());
			// Add the list of Targets to the DB
			if (thisGame.getTargets() != null) {
				// We save the Targets to DB based upon the data format of the POI
				ref.child("game_targets").setValue(thisGame.getTargets());
			}
		} catch (FirebaseException e) {
			MyLog.e(TAG, "exception fire");
			MyLog.logException(e);
		} catch (NullPointerException e) {
			MyLog.e(TAG, "null fire");
			MyLog.logException(e);			
		}
		MyLog.d(TAG, "saveTargets end");
	}	
}
