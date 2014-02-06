package com.mckenna.thegame;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BackgroundService extends Service  {
	private final String TAG = "BackgroundService";
	BackgroundProcessor background;
	String gameId;
	Notifier notifier;
	
	public BackgroundService() { }
	
	@Override
	public IBinder onBind(Intent intent) {
		MyLog.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();	
		MyLog.d(TAG, "onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//super.onStartCommand(intent, flags, startId);
		MyLog.d(TAG, "--onStartCommand--");
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
		
		if (intent == null) {
			// This is expected if it is started once the app has exited
			MyLog.e(TAG, "Intent was Null");
		} else {
			gameId = intent.getStringExtra("GAMEID");
		}
		if (gameId == null) {
			MyLog.d(TAG, "GAMEID was Null, Exiting Background Service");
			Toast.makeText(this, "GAMEID was Null, Exiting Background Service", Toast.LENGTH_LONG).show();
			if ( notifier != null) notifier.cancelNotification();
			stopSelf();
			return START_NOT_STICKY;
		} else if (gameId == "") {
			MyLog.d(TAG, "GAMEID was blank, Exiting Background Service");
			Toast.makeText(this, "GAMEID was blank, Exiting Background Service", Toast.LENGTH_LONG).show();
			if ( notifier != null) notifier.cancelNotification();
			stopSelf();
			return START_NOT_STICKY;
		}
		// Create the background processor
		background = new BackgroundProcessor(this, gameId);
		// Display the notification so the background app can be exited
		notifier = new Notifier(this);
		Notification notification = notifier.getNotification("The Game", "Running in the background");
		startForeground(4567, notification);
	    // We want this service to continue running until it is explicitly stopped, so return sticky.
		MyLog.d(TAG, "--onStartCommand end--");
	    return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
		MyLog.d(TAG, "--onDestroy--");
		notifier.cancelNotification();
		background.stop();
	}
}