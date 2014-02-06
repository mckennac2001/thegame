/**
 * 
 */
package com.mckenna.thegame;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * @author User
 * 
 */
public class MainActivity extends Activity {

	private final String TAG = "MainActivity";
	private final String FILENAME = "TheGame.preferences";

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public MainActivity() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// MenuItem searchItem = menu.findItem(R.id.action_stop);
		// SearchView searchView = (SearchView)
		// MenuItemCompat.getActionView(searchItem);

		return super.onCreateOptionsMenu(menu);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		MyLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//boolean con = servicesConnected();
		//MyLog.d(TAG, "onCreate GooglePlayServicesUtil=" + con);
	}

	@Override
	public void onResume() {
		MyLog.d(TAG, "onResume");
		super.onResume();
		
		if (Singleton.getInstance().getCurrentGameId() != null) {
			findViewById(R.id.button_quit_game).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.button_quit_game).setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onPause() {
		MyLog.d(TAG, "onPause");
		super.onPause();
	}
	
    @Override
    public void onStop() {
    	MyLog.d(TAG, "onStop");
    	super.onStop();
    }
    
	@Override
	public void onDestroy() {
		MyLog.d(TAG, "onDestroy");
		super.onDestroy();
	}
	
	public void onClick_New_Game(View v) {
		MyLog.d(TAG, "onClick_New_Game");
		// Intent intent = new Intent();
		// startActivity(new Intent("com.mckenna.thegame.NEWGAME", null, this,
		// NewGame.class));
		startActivity(new Intent(this, NewGameActivity.class));
	}

	public void onClick_New_Game(MenuItem m) {
		MyLog.d(TAG, "onClick_Join_Game");
		startActivity(new Intent(this, NewGameActivity.class));
	}

	public void onClick_Join_Game(View v) {
		MyLog.d(TAG, "onClick_Join_Game");
		startActivity(new Intent(this, JoinGameActivity.class));
	}

	public void onClick_Join_Game(MenuItem m) {
		MyLog.d(TAG, "onClick_Join_Game");
		startActivity(new Intent(this, JoinGameActivity.class));
	}

	public void onClick_Quit_Game(View v) {
		MyLog.d(TAG, "onClick_Quit_Game");
		Singleton.getInstance().setCurrentGameId(null);
		stopService(new Intent(this, BackgroundService.class));
		findViewById(R.id.button_quit_game).setVisibility(View.INVISIBLE);
	}
	
	public void onClick_ReEnter_Properties(MenuItem v) {
		MyLog.d(TAG, "onClick_ReEnter_Properties");
		Toast.makeText(this, "onClick_ReEnter_Properties", Toast.LENGTH_SHORT)
				.show();
		getBaseContext().deleteFile(FILENAME);
		startActivity(new Intent(this, FirstTimeActivity.class));
	}

	public void onClick_Button(View v) {
		MyLog.d(TAG, "onClick_Button");
		Toast.makeText(this, "onClick_Button", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this, MyDialog.class));
	}
}

/*		
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}


	// Handle results returned to the FragmentActivity by Google Play services
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:

			// If the result code is Activity.RESULT_OK, try
			// to connect again

			switch (resultCode) {
			case Activity.RESULT_OK:

				// Try the request again
				break;
			}
		}
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			MyLog.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = resultCode; // connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
			return false;
		}
	}
	*/

