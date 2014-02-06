package com.mckenna.thegame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MyDialog extends Activity {

	private final String TAG = "MyDialog";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
	}
	
    @Override
    public void onResume() {
    	MyLog.d(TAG, "onResume");
        super.onResume();
    }
}
