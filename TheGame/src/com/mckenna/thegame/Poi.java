package com.mckenna.thegame;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class Poi implements Parcelable{
	private final String TAG = "Poi";
    private String id = ""; // For Targets this is a UUID, for Players this is ther email address
    private String name = ""; // For Targets, this is the game name, for Players it is a proper name
//    private String email = ""; // Not used
	private double latitude = 0; 
    private double longitude = 0;
    private double speedHeight = 0; // For Targets it is the height, for Players it is their speed
    private ArrayList<String> hitters;
    

    // Constructor for Players
    public Poi(String id, String name){
    	this(id, 
			name,
//			"",
			0, 
			0, 
			0,
			null);
   }
    
    // Constructor for Players
    public Poi(String id, String name, double speedHeight){
    	this(id, 
			name,
//			"",
			0, 
			0, 
			speedHeight,
			null);   
   }
    
    // Constructor for Players
    public Poi(String id, String name, LatLng point, double speedHeight){
    	this(id, 
			name,
//			"",
			point.latitude, 
			point.longitude, 
			speedHeight,
			null);   	
   }
    
 // Constructor for Players
    public Poi(String id, String name, double latitude, double longitude, double speedHeight){
    	this(id, 
			name,
//			"",
			latitude, 
			longitude, 
			speedHeight,
			null);   	
   }
    
    
    public Poi(Marker marker){
    	this(marker.getTitle(), 
			marker.getSnippet(), 
//			"", 
			marker.getPosition().latitude, 
			marker.getPosition().longitude, 
			0,
			null);
   }
    
    public Poi(Marker marker, double speedHeight){
    	this(marker.getTitle(), 
			marker.getSnippet(), 
//			"", 
			marker.getPosition().latitude, 
			marker.getPosition().longitude, 
			speedHeight,
			null);
   }
    
    public Poi(String id, String name, double latitude, 
    		double longitude, double speedHeight, ArrayList<String> hitters){
        this.id = id;
        this.name = name;
//        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedHeight = speedHeight;
        this.hitters = hitters;
        
        if ((name == null) || (name.isEmpty())) {
        	name = "blank";
        	MyLog.d(TAG, "name was blank #1");
        }    	
    }
    
   // Parcelling part
   public Poi(Parcel in) {
	   readFromParcel(in); 
   }
   
   //
   public Poi() {
       this.id = "blank-blank";
       this.name = "blank name";
//       this.email = "blank email";
       this.latitude = 0;
       this.longitude = 0;
       this.speedHeight = 0;
       this.hitters = null;
   }
   
   private void readFromParcel(Parcel in)
   {
       String[] data = new String[5];
       in.readStringArray(data);
       this.id = data[0];
       this.name = data[1];
//       this.email = data[2];
       this.latitude = Double.valueOf(data[2]);
       this.longitude = Double.valueOf(data[3]);
       this.speedHeight = Double.valueOf(data[4]);
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
       dest.writeStringArray(new String[] {this.id,
                                           this.name,
//                                           this.email,
                                           Double.toString(this.latitude),
                                           Double.toString(this.longitude),
                                           Double.toString(this.speedHeight)});
   }
   
   @Override
   public int describeContents(){
       return 0;
   }
   
   public static final Parcelable.Creator<Poi> CREATOR = new Parcelable.Creator<Poi>() {
	   
       public Poi createFromParcel(Parcel in) {
           return new Poi(in); 
       }

       public Poi[] newArray(int size) {
           return new Poi[size];
       }
   };

   // Either same ids or same coordinates correspond to equality
   @Override
   public boolean equals(Object poi) {
     if (poi == null) {
    	 return false;
     }
     Poi p = (Poi) poi;
     if ( id.equals(p.id)) {
    	 MyLog.d(TAG, "equals is true");
    	 return true;
     }
     return false;
   }
   
   /****** Setters and Getters ******/
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
//	public String getEmail() {
//		return email;
//	}

	/**
	 * @param email the email to set
	 */
//	public void setEmail(String email) {
//		this.email = email;
//	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * @return the longtitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * @param longtitude the longtitude to set
	 */
	public void setLongtitude(double longitude) {
		this.longitude = longitude;
	}

	
	@Override
	public String toString() {
        return "id=" + id + 
        		", name=" + name + 
//        		", email=" + email +         		
        		", latitude=" + latitude + 
        		", longitude=" + longitude +
        		", speedHeight=" + speedHeight;
	}

	public double getSpeedHeight() {
		return this.speedHeight;
	}
	
	public void setSpeedHeight(double speedHeight) {
		this.speedHeight = speedHeight;
	}

	public ArrayList<String> getHitters() {
		return hitters;
	}

	public void setHitters(ArrayList<String> hitters) {
		this.hitters = hitters;
	}

	public void addHitter(String hitter) {
		if (this.hitters == null) {
			this.hitters = new ArrayList<String>();
			this.hitters.add(hitter);
		} else if (!this.hitters.contains(hitter)) {
			this.hitters.add(hitter);
		}
	}	
}