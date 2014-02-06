package com.mckenna.thegame;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Elevation extends AsyncTask<LatLng, Integer, Double> {

	private static final String TAG = "Elevation";
	
    protected Double doInBackground(LatLng... point) {
    	return getElevationFromGoogleMaps(point[0]);
    }
    protected void onProgressUpdate(Integer progress) {
        //setProgressPercent(progress[0]);
    }
    protected void onPostExecute(Double result) {
    	//Double i = result;
    	
        //showDialog("Downloaded " + result + " bytes");
    }
    
	public double getElevationFromGoogleMaps(LatLng point) {
		return getElevationFromGoogleMaps(point.latitude, point.longitude);
	}
	public double getElevationFromGoogleMaps(double latitude, double longitude) {
		
		MyLog.d(TAG, "getElevationFromGoogleMaps");
        double result = Double.NaN;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude)
                + "&sensor=true";
        MyLog.d(TAG, "getElevationFromGoogleMaps USR=" + url);
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    //result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
                    result = Double.parseDouble(value);
                    MyLog.d(TAG, "getElevationFromGoogleMaps result=" + result);
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {
        	MyLog.e(TAG, "getElevationFromGoogleMaps ClientProtocolException");
			if (e.getMessage() != null)
				MyLog.e(TAG, e.getMessage().toString());
			if (e.getCause() != null)
				MyLog.e(TAG, e.getCause().toString());	
        } catch (IOException e) {
        	MyLog.e(TAG, "getElevationFromGoogleMaps IOException");
			if (e.getMessage() != null)
				MyLog.e(TAG, e.getMessage().toString());
			if (e.getCause() != null)
				MyLog.e(TAG, e.getCause().toString());	
        }

        return result;
    }
}
