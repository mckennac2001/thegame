/**
 * 
 */
package com.mckenna.thegame;

import java.util.Iterator;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author User
 *
 */
public class NewGameActivity extends Activity {

	private static final int PICK_CONTACT = 840296;
	private static final int ADD_TARGETS = 1273946;
	private final String TAG = "NewGameActivity";
	private Database database = new Database();
	private Game game;
	
	/**
	 * 
	 */
	public NewGameActivity() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.d(TAG, "onCreate");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_new_game);
	    
	    ActionBar actionBar = this.getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    if (game == null) {
	    	game = new Game();
	    }
	}
	
	public void onClick_AddPois(View v) {
		MyLog.d(TAG, "onClick_AddPois");
		
		// Create an Intent to hold the data
		Intent sendIntent = new Intent(this, AddPoisActivity.class);
		
		// We need a bundle to add the pois to an Intent
		Bundle poiBundle = new Bundle();
		// List of Targets we may have
		if (game.getTargets() != null) {
			poiBundle.putParcelable("TargetsToDraw", game.getTargets());
		}
		sendIntent.putExtra("TARGETS", poiBundle);

		// Read the game name from the form and use it is the default name for targets
		game.setName( ((EditText) findViewById(R.id.game_name)).getText().toString() );
		if (!game.getName().isEmpty()) {
			sendIntent.putExtra("NAME", game.getName());
		}		
		
		//open view to create the pois
		startActivityForResult(sendIntent, ADD_TARGETS);
	}
	
	public void onClick_Add_Players(View v) {
		MyLog.d(TAG, "onClick_Add_Players");
		
		//startActivity(new Intent(this, PickPlayersActivity.class));
		// Create an Intent to hold the data
//		Intent sendIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		
		Intent sendIntent = new Intent(this, PickPlayersActivity.class);
		startActivityForResult(sendIntent, PICK_CONTACT);
/*		
		Iterator<Pair<String,String>> i = getNameEmailDetails().iterator();
		while (i.hasNext()) {
			MyLog.d(TAG, i.next().toString());
			//MyLog.d(TAG, "email=" + emlAddr + ", name=" + name);
		}
		*/
	}	
		
/*		
		// We need a bundle to add the pois to an Intent
		Bundle poiBundle = new Bundle();
		// List of Targets we may have
		if (game.getTargets() != null) {
			poiBundle.putParcelable("TargetsToDraw", game.getTargets());
		}
		sendIntent.putExtra("TARGETS", poiBundle);

		// Read the game name from the form and use it is the default name for targets
		game.setName( ((EditText) findViewById(R.id.game_name)).getText().toString() );
		if (!game.getName().isEmpty()) {
			sendIntent.putExtra("NAME", game.getName());
		}		
		
		//open view to create the pois
		startActivityForResult(sendIntent, 10);*/

	
	public void onClick_Save(View v) {
		MyLog.d(TAG, "onClick_Save");
		
		// Read the values from the form
		String name = ((EditText) findViewById(R.id.game_name)).getText().toString();
		if (name.isEmpty()) {
			//We really can't have the db polluted by games with no name
			Toast.makeText(this, "This Game deserves a Name", Toast.LENGTH_SHORT).show();
			return;
		}		
		game.setName(name);
		game.setLocation( ((EditText) findViewById(R.id.game_location)).getText().toString() );
		if (game.getName().isEmpty()) {
			game.setName("not specified");
		}		
		// Get radio button value
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.game_privacy);
		int selectedId = radioGroup.getCheckedRadioButtonId();
	
		switch (selectedId) {
		case R.id.radio_just_me:
			game.setType(Game.gameType.JUSTME);
			break;
		case R.id.radio_by_invite:
			game.setType(Game.gameType.BYINVITE);
			break;
		case R.id.radio_public:
			game.setType(Game.gameType.PUBLIC);
			break;
		default:
			game.setType(Game.gameType.PUBLIC);
			break;
		}
		
		Poi me = new Poi(Singleton.getInstance().getMyPlayerId(), Singleton.getInstance().getMyPlayerName());
		game.addPlayer(me);
		
		// Assign a UUID for this new game
		game.setId(UUID.randomUUID().toString());
		
		//store data in db
		database.saveGame(game);	
		game = null;
		finish();
	}	
/*	
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
            super.onActivityResult(reqCode, resultCode, data);

            switch (reqCode) {
                    case (PICK_CONTACT):
                            if (resultCode == Activity.RESULT_OK) {
                                    Uri contactData = data.getData();
                                    Cursor c = managedQuery(contactData, null, null, null, null);
                                    if (c.moveToFirst()) {
                                            String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
                                            txtContacts.setText(name);
                                    }
                            }
                            break;
            }
    }
    */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MyLog.d(TAG, "onActivityResult, requestCode=" + requestCode);

		// Check which request we're responding to
        switch (requestCode) {
        	case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
		        	// Will check for the list of new players to add
		        	Bundle bundle = data.getBundleExtra("PLAYERS");
		        	MyLog.d(TAG, "onActivityResult #21");
		        	PoiList playerList = bundle.getParcelable("PLAYERS");
		        	MyLog.d(TAG, "onActivityResult #22");   

		        	if (playerList != null) {
		        		MyLog.d(TAG, "onActivityResult #24");
		        		playerList.print();
		        		game.addPlayers(playerList);
		        		MyLog.d(TAG, "onActivityResult #26");
		        	}
		        	
		    	    if (game.getPlayers() != null && game.getPlayers().size() > 0) {
		    	    	Button createButton = (Button) findViewById(R.id.button_create_game);
		    	    	createButton.setEnabled(true);
		    	    	MyLog.d(TAG, "onActivityResult #30");		    	    	
		    	    }

		        	
/*                	
                    Uri contactData = data.getData();
                    Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                      String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                      //String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.));
                      // TODO Whatever you want to do with the selected contact name.
                      MyLog.d(TAG, "onActivityResult PickContact " + name);
                    }*/
                }
        		break;

        	case (ADD_TARGETS): 
		        // Make sure the request was successful
		        if (resultCode == RESULT_OK) {
		        	// Will check for the list of new pois to store or delete
		        	Bundle bundle = data.getBundleExtra("TARGETS");
		        	MyLog.d(TAG, "onActivityResult #1");
		        	PoiList targetsToAdd = bundle.getParcelable("TargetsToAdd");
		        	MyLog.d(TAG, "onActivityResult #2");
		        	PoiList targetsToDelete = bundle.getParcelable("TargetsToDelete");
		        	MyLog.d(TAG, "onActivityResult #3");
		        	// Print out lists
		        	if (targetsToAdd != null) {
		        		MyLog.d(TAG, "onActivityResult #4");
		        		targetsToAdd.print();
		        		MyLog.d(TAG, "onActivityResult #5");
		        		game.addTargets(targetsToAdd);
		        		MyLog.d(TAG, "onActivityResult #6");
		        	}
		        	if (targetsToDelete != null) {
		        		MyLog.d(TAG, "onActivityResult #7");
		        		targetsToDelete.print();	
		        		MyLog.d(TAG, "onActivityResult #8");
		        		game.deleteTargets(targetsToDelete);
		        		MyLog.d(TAG, "onActivityResult #9");
		        	}
		        	
		    	    if (game.getTargets() != null && game.getTargets().size() > 0) {
		    	    	Button createButton = (Button) findViewById(R.id.button_add_players);
		    	    	createButton.setEnabled(true);
		    	    	MyLog.d(TAG, "onActivityResult #10");
		    	    	
		    			//debug
		    			TextView textView = (TextView) findViewById(R.id.display_targets);
		    			textView.setMovementMethod(new ScrollingMovementMethod());
		    			Iterator<Poi> it = game.getTargets().iterator();
		    			textView.append("Targets \n");
		    			while (it.hasNext()) {
		    				MyLog.d(TAG, "onActivityResult #loop");
		    				Poi target = it.next();
		    				textView.append( Double.toString(target.getLatitude()) + " ");
		    				textView.append( Double.toString(target.getLatitude()) + "\n");
		    			}
		    	    }
		        } else {
		        	GameAlerts.showError(this, "Bad Return from AddPois");
		        }
        }
	}

}
