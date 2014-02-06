package com.mckenna.thegame;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelablePair implements Parcelable {
	String first;
	String second;
	String third;
	
    public ParcelablePair(String first, String second) {
    	this.first = first;
    	this.second = second;
    }
    
    public ParcelablePair(String first, String second, String third) {
    	this.first = first;
    	this.second = second;
    	this.third = third;
    }
    
    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getSecond() {
        return second;
    }
    public void setSecond(String second) {
        this.second = second;
    }
    public String getThird() {
        return third;
    }
    public void setThird(String third) {
        this.third = third;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    
    public ParcelablePair() { }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first);
        dest.writeString(second);
        dest.writeString(third);
    }
    
    public static final Parcelable.Creator<ParcelablePair> CREATOR = new Parcelable.Creator<ParcelablePair>() {
        public ParcelablePair createFromParcel(Parcel in) {
            return new ParcelablePair(in);
        }
        public ParcelablePair[] newArray(int size) {
            return new ParcelablePair[size];
        }
    };
    
    private ParcelablePair(Parcel in) {
    	first = in.readString();
        second = in.readString();
        third = in.readString();
    }
}