package com.mckenna.thegame;

import java.io.FileInputStream;
import java.util.Properties;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
 
public class SplashScreen extends Activity {
 
    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 300;
	private static final String TAG = "SplashScreen";
	//private final String FILENAME = "TheGame.preferences";
 
	public SplashScreen() { }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	MyLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
            	
            	try {
					Properties properties = readProperties();
					Singleton.getInstance().setMyPlayerName(properties.getProperty("name"));
					Singleton.getInstance().setMyPlayerId(properties.getProperty("email"));
					// No login to the database
					Database.startLogin(new LoginAuthenticatedHandler());
					//Database.startLoginCheck(new LoginAuthenticatedHandler());
					//startActivity(new Intent(SplashScreen.this, MainActivity.class));
					//finish();
        		
				} catch (Exception e) {
					MyLog.d(TAG, "readProperties " + e.toString());
					MyLog.logException(e);
					// Read properties failed, we need to enter the user id
					startActivity(new Intent(SplashScreen.this, FirstTimeActivity.class));
					finish();
				}
                // close this activity
                //finish();
            }
        }, SPLASH_TIME_OUT);
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
    public void onStop() {
    	MyLog.d(TAG, "onStop");
    	super.onStop();
    }
    
	@Override
	public void onDestroy() {
		MyLog.d(TAG, "onDestroy");
		super.onDestroy();
	}
	
	private class LoginAuthenticatedHandler implements SimpleLoginAuthenticatedHandler {
		// Callback from database
		@Override
		public void authenticated(Error error, User user) {
			Context context = getBaseContext();
			if (error != null) {
				MyLog.e(TAG, "LoginAuthenticatedHandler Error=" + error.toString());
				Toast.makeText(context, "LoginAuthenticatedHandler Error=" + error.toString(), Toast.LENGTH_SHORT).show();
				
				switch (error) {
					case PermissionDenied: // A problem with the permission settings on the server
					case Unknown:
						// Network problem
						// Not much we can do without a network.
						MyLog.e(TAG, "Exit due to Network Error");
						break;
	
					case InvalidEmail:
					case UserDoesNotExist:
						// There was an error logging into this account,
						// We should ask the user to re enter his details again
						context.deleteFile(Singleton.FILENAME);
						startActivity(new Intent(context, FirstTimeActivity.class));	
						break;
						
					case Preempted:
						MyLog.e(TAG, "Login Preempted, this is ok");
						//startActivity(new Intent(context, MainActivity.class));
						break;
						
					default:
						break;
				}	
				// We dont want to stay on this screen no matter what happens
				finish(); 
				
			} else if (user == null) {
				MyLog.e(TAG, "LoginAuthenticatedHandler User=Null");
			} else {
				MyLog.e(TAG, "LoginAuthenticatedHandler Success for User=" + user.toString());		
				// We are now logged in
				startActivity(new Intent(context, MainActivity.class));
				finish();
			}
		}
	}
    
    public Properties readProperties() throws Exception {
    	MyLog.d(TAG, "readProperties");
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream(this.getFilesDir() + "/" + Singleton.FILENAME);
        properties.loadFromXML(fis);
        return properties;
    }
}
