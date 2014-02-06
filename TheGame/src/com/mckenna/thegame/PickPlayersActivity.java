/**
 * Where you pick the players for a game
 */
package com.mckenna.thegame;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author User
 *
 */
public class PickPlayersActivity extends Activity {

	private final String TAG = "PickPlayersActivity";
	private ListView listview;
	private ActionMode mActionMode;
	private Activity a;
	
	public PickPlayersActivity() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_pick_players);
	    
	    a = this;
	}

    @Override
    public void onResume() {
    	MyLog.d(TAG, "onResume");
        super.onResume();
        
        // Get the possible players names and email addresses 
        ArrayList<ParcelablePair> players = getNameEmailDetails();
        
		// Find our list view
		listview = (ListView) findViewById(R.id.listViewOfPlayers);
		
		ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2, android.R.id.text1, players) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);

				ParcelablePair pair = (ParcelablePair) getItem(position);
				text1.setText(pair.getFirst());
				text2.setText(pair.getSecond());
				return view;
			}
		};
			
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
				
				final ParcelablePair item = (ParcelablePair) parent.getItemAtPosition(position);
				MyLog.d(TAG, "onItemClick, item=" + item.getFirst());
	
				// For now we only add 1 player to the list (its all we have in a single choice list)
				PoiList playerList = new PoiList();
				// A POI takes the Id first, the name second.
				playerList.add(new Poi(item.getSecond(), item.getFirst()));
				
				// We need a bundle to add the players to an Intent
				Bundle poiBundle = new Bundle();
				// List of Players for the game
				poiBundle.putParcelable("PLAYERS", playerList);
				
				// Create an Intent to hold the return data
				Intent returnIntent = new Intent(a, NewGameActivity.class);
				returnIntent.putExtra("PLAYERS", poiBundle);
				
				setResult(Activity.RESULT_OK, returnIntent);
				finish();
			}
		});
	
		listview.setOnLongClickListener(new View.OnLongClickListener() {
	        // Called when the user long-clicks on someView
	        public boolean onLongClick(View view) {
	            if (mActionMode != null) {
	                return false;
	            }

	            // Start the CAB using the ActionMode.Callback defined above
	            mActionMode = a.startActionMode(mActionModeCallback);
	            view.setSelected(true);
	            return true;
	        }
	    });        
    }
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.players_context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	MyLog.d(TAG, "onActionItemClicked");
	        switch (item.getItemId()) {
	            case R.id.item1:
	            	MyLog.d(TAG, "picked!!!");
	                //shareCurrentItem();
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	// Get the list of name and associated email addresses in the Contacts db
	public ArrayList<ParcelablePair> getNameEmailDetails() {
		MyLog.d(TAG, "getNameEmailDetails");
		
	    ArrayList<ParcelablePair> emlRecs = new ArrayList<ParcelablePair>();
	    HashSet<String> emlRecsHS = new HashSet<String>();
	    Context context = this.getBaseContext();
	    ContentResolver cr = context.getContentResolver();
	    String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID, 
	            ContactsContract.Contacts.DISPLAY_NAME,
	            ContactsContract.Contacts.PHOTO_ID,
	            ContactsContract.CommonDataKinds.Email.DATA, 
	            ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
	    String order = "CASE WHEN " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " 
	            + ContactsContract.Contacts.DISPLAY_NAME 
	            + ", " 
	            + ContactsContract.CommonDataKinds.Email.DATA
	            + " COLLATE NOCASE";
	    //String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
	    //Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
	    Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, order);
	    if (cur.moveToFirst()) {
	        do {
	            // names comes in hand sometimes
	            String name = cur.getString(1);
	            String emlAddr = cur.getString(3);

	            // keep unique only
	            if (emlRecsHS.add(emlAddr.toLowerCase())) {
	            	//MyLog.d(TAG, "email=" + emlAddr + ", name=" + name);
	            	// Create UUID to assign to this user in this game, based on their email
	            	
	                emlRecs.add(new ParcelablePair(name, emlAddr));
	            }
	        } while (cur.moveToNext());
	    }

	    cur.close();
	    return emlRecs;
	}
}
