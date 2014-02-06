package com.mckenna.thegame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.firebase.client.FirebaseError;

import android.os.SystemClock;
import android.provider.Settings.System;
import android.util.Log;

public class MyLog {
	
	private static final String TAG = "MyLog";
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS ");
	
	private static String getDateString() {
		
		String dateString = sdf.format(new Date());
		
/*        Calendar c = Calendar.getInstance(); 
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int millis = c.get(Calendar.MILLISECOND);
        String dateString =  hours + ":" + minutes + ":" + seconds + ":" + millis;*/
		
        return dateString;
	}
	
    public static void i(String tag, String message){
        // Printing the message to LogCat console
        Log.i(tag, message);
        // Write the log message to the file
        appendLog("I: " + getDateString() + tag + ": " + message);
    }

    public static void d(String tag, String message){
        Log.d(tag, message);
        
        //String milliSecs = SystemClock.currentThreadTimeMillis()/1000d + ": ";
        appendLog("D: " + getDateString() + tag + ": " + message);
    }

    public static void e(String tag, String message){
        Log.e(tag, message);
        String milliSecs = SystemClock.currentThreadTimeMillis() + ": ";
        appendLog("E: " + milliSecs + tag + ": " + message);
    }
    
    public static void wtf(String tag, String message){
        Log.wtf(tag, message);
        String milliSecs = SystemClock.currentThreadTimeMillis() + ": ";
        appendLog("W: " + milliSecs + tag + ": " + message);
    }
    
	public static String logException(Exception e) {
		String error = "Exception: ";
		
		if (e.getMessage() != null) 
			error +=  e.getMessage().toString();
		if (e.getCause() != null) 
			error +=  " \n" + e.getCause().toString();
		if (e.getStackTrace() != null) 
			error +=  " \n" + e.getStackTrace().toString();
		
		MyLog.e(TAG, error);
		return error;
		//Toast.makeText(v, error, Toast.LENGTH_LONG).show();
	}
	
	public static String logFirebaseError(FirebaseError e) {
		String error = "FirebaseError: ";
		
		if (e.getMessage() != null) 
			error +=  e.getMessage().toString();
		
		MyLog.e(TAG, error);
		return error;
	}	
	
    public static void appendLog(String text)
    {       
       File logFile = new File("sdcard/gamelog.txt");
       if (!logFile.exists())
       {
          try
          {
             logFile.createNewFile();
          } 
          catch (IOException e)
          {
             // TODO Auto-generated catch block
        	 Log.e(TAG, "Can't create log file");
             e.printStackTrace();
          }
       }
       try
       {
          //BufferedWriter for performance, true to set append to file flag
          BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
          buf.append(text);
          buf.newLine();
          buf.close();
       }
       catch (IOException e)
       {
          // TODO Auto-generated catch block
    	  Log.e(TAG, "Can't write to log file");
          e.printStackTrace();
       }
    }
}
