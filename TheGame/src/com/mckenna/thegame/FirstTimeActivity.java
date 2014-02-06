package com.mckenna.thegame;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class FirstTimeActivity extends Activity {

	private final String TAG = "FirstTimeActivity";
	private final String FILENAME = "TheGame.preferences";
	
	public FirstTimeActivity() { }
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		MyLog.d(TAG, "onCreate");
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_first_time);	
	}
	
	public void onClick_Save_Properties(View v) {
		MyLog.d(TAG, "onClick_Save_Properties");
		
		// Read the email from the form
		String email = ((EditText) findViewById(R.id.first_time_email)).getText().toString();
		if (email.isEmpty() || !isValidEmail(email)) {
			Toast.makeText(this, "I must insist on an email address", Toast.LENGTH_SHORT).show();
			return;
		}	
		// Read the name from the form
		String name = ((EditText) findViewById(R.id.first_time_name)).getText().toString();
		if (name.isEmpty()) {
			// We can't go any further without this
			Toast.makeText(this, "You really do need a name", Toast.LENGTH_SHORT).show();
			return;
		}		
		
		Singleton.getInstance().setMyPlayerId(email);
		Singleton.getInstance().setMyPlayerName(name);
		
		// Now store them so we never have to do this again
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("email", email);
		
		try {
			writeProperties(properties);
			startActivity(new Intent(FirstTimeActivity.this, MainActivity.class));
			finish();
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.d(TAG, "!!!!!!writeProperties " + e.toString());
			GameAlerts.showError(this, "Can't save our properties, Suicide is the only honourable option");
		}
	}
	
    private Properties writeProperties(Properties properties) throws Exception {
    	MyLog.d(TAG, "writeProperties");
        properties.setProperty("name", Singleton.getInstance().getMyPlayerName());
        properties.setProperty("email", Singleton.getInstance().getMyPlayerId());

        File file = new File(this.getFilesDir() + "/" + FILENAME);
 /*       MyLog.d(TAG, "file.exists=" + file.exists());
        MyLog.d(TAG, "file.getAbsolutePath=" + file.getAbsolutePath());
        MyLog.d(TAG, "file.getCanonicalPath=" + file.getCanonicalPath());
        MyLog.d(TAG, "file.getPath=" + file.getPath());
        MyLog.d(TAG, "file.canWrite=" + file.canWrite());
        MyLog.d(TAG, "file.createNewFile=" + file.createNewFile());
        MyLog.d(TAG, "file.exists=" + file.exists());
        MyLog.d(TAG, "file.delete=" + file.delete());
        MyLog.d(TAG, "file.exists=" + file.exists()); */
        FileOutputStream fos = new FileOutputStream(file);
        properties.storeToXML(fos, "This is the properties of TheGame");
        fos.close();
        return properties;
    }
        //String string = "hello";
        //fos.write(string.getBytes());
     
/*        
        file = new File(FILENAME);
        MyLog.d(TAG, "file.exists=" + file.exists());
        MyLog.d(TAG, "file.getAbsolutePath=" + file.getAbsolutePath());
        MyLog.d(TAG, "file.getCanonicalPath=" + file.getCanonicalPath());
        MyLog.d(TAG, "file.getPath=" + file.getPath());
        MyLog.d(TAG, "file.canWrite=" + file.canWrite());
        */
/*        String appPath = this.getApplicationContext().getFilesDir().getAbsolutePath();
        MyLog.d(TAG, "appPath=" + appPath);
        
        //OutputStream output = new FileOutputStream(this.getFilesDir() + "/" + FILENAME);
        String string = "hello";
        FileOutputStream fos = new FileOutputStream (new File(FILENAME));
        
        //FileOutputStream fos = openFileOutput(this.getFilesDir() + "/" + FILENAME, Context.MODE_PRIVATE);
        fos.write(string.getBytes());
        fos.close();*/
        
        
//        properties.store(output, null);
        
        //output.close();
        
        //FileOutputStream fos = openFileOutput(this.getFilesDir() + FILENAME, Context.MODE_PRIVATE);
//        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//        properties.storeToXML(fos, "This is the properties of TheGame");
//        fos.close();

    
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
