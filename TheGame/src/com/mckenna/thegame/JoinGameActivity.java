package com.mckenna.thegame;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;

public class JoinGameActivity extends Activity {

	private final String TAG = "JoinGameActivity";
//	private Game game;
//	private String gameId;
	private Database database = new Database();
	private ListView listview;
	private Activity a;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		MyLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		a = this;
		
		this.
		// Load our game from DB
		database.getListOfGameIds(new DbReadCallbackListener());
	}
	
	@Override
	public void onResume() {
		MyLog.d(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	public void onPause() {
		MyLog.d(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		MyLog.d(TAG, "onDestroy");
		super.onDestroy();
	}	
	
	// Called when the list of games is available from the db
	private class DbReadCallbackListener implements ValueEventListener {
		private Context getBaseContext;

		@Override
		public void onCancelled(FirebaseError arg0) {
			MyLog.d(TAG, "onCancelled");
			MyLog.logFirebaseError(arg0);
			// We have not been able to contact the database
			// We should show an error and then move back to the main activity
			// If they click no, we will stay here forever!
			GameAlerts.showErrorBeforeExit(a, "Network Error", "Could not contect the server");
		}

		@Override
		public void onDataChange(DataSnapshot games) {
			MyLog.d(TAG, "onDataChange");

			ArrayList<Pair<String, String>> avaivableGames = new ArrayList<Pair<String, String>>();
			// Parse out the game ids and names from the snapshot returned
			database.parseGameIds(games, avaivableGames, Singleton.getInstance().getMyPlayerId());
			Iterator<Pair<String, String>> it = avaivableGames.iterator();
			while (it.hasNext()) {
				MyLog.d(TAG, "onDataChange name=" + it.next().getSecond());
			}
			
			// Find our list view
			listview = (ListView) findViewById(R.id.listViewOfGames);
			
			ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2, android.R.id.text1, avaivableGames) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);
					TextView text1 = (TextView) view.findViewById(android.R.id.text1);
					TextView text2 = (TextView) view.findViewById(android.R.id.text2);

					Pair<String,String> pair = (Pair<String,String>) getItem(position);
					text1.setText(pair.getFirst());
					text2.setText(pair.getSecond());
					return view;
				}
			};
				
			
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,	int position, long id) {
					
					final Pair<String,String> item = (Pair<String,String>) parent.getItemAtPosition(position);
					MyLog.d(TAG, "onItemClick, item=" + item.getFirst());
					
					Intent sendIntent = new Intent(a, GameActivity.class);
					sendIntent.putExtra("GAMEID", item.getSecond());		
					startActivity(sendIntent);
					finish();
	/*				view.animate().setDuration(2000).alpha(0)
							.withEndAction(new Runnable() {
								@Override
								public void run() {
									list.remove(item);
									adapter.notifyDataSetChanged();
									view.setAlpha(1);
								}
							});
							*/
					//list.remove(item);
					//adapter.notifyDataSetChanged();
					//view.setAlpha(1);
				}
			});
		}
	}
}
