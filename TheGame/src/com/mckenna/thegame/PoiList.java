package com.mckenna.thegame;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class PoiList extends ArrayList<Poi> implements Parcelable {
	private static final long serialVersionUID = 1556689014826654000L;
	private final String TAG = "PoiList";
	
	public PoiList() {
		MyLog.d(TAG, "constructor");
	}
	
	// Parceling part
	public PoiList(Parcel in) {
		MyLog.d(TAG, "constructor parcel");
		readFromParcel(in); 
	}

	private void readFromParcel(Parcel in) {
		MyLog.d(TAG, "readFromParcel");
		in.readList(this, PoiList.class.getClassLoader());
	}
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		MyLog.d(TAG, "writeToParcel");
		if (isEmpty()) return; 
		dest.writeList(this);  
	}
	
	@Override
	public int describeContents() {
		MyLog.d(TAG, "describeContents");
		// TODO Auto-generated method stub
		return 0;
	}
	
    public static final Parcelable.Creator<PoiList> CREATOR = new Parcelable.Creator<PoiList>() {  
        
        public PoiList createFromParcel(Parcel in) {  
            return new PoiList(in);
        }  
   
        public PoiList[] newArray(int size) {  
            return new PoiList[size];  
        }  
    };  
    
//    @Override
//	public boolean add(Poi poi) {
//		MyLog.d(TAG, "add " + poi);
//		return this.add(poi);
//	}
/*
	public boolean exists(Poi poi) {
		MyLog.d(TAG, "exists");
		if (poiArray == null) return false;
		Iterator<Poi> it = poiArray.iterator(); //iterator
		while(it.hasNext()) {
		    if (poi == it.next()) {
		    	return true;
		    }
		}
		return false;
	}
*/	
	// Return true if the poi is found to delete
//	public boolean delete(Poi poi) {
//		MyLog.d(TAG, "delete " + poi);
//		return super.remove(poi);
//	}

	/**
	 * @return the poiList
	 */
//	public ArrayList<Poi> getPoiArray() {
//		return poiArray;
//	}
	
	public void print() {
		Iterator<Poi> it = iterator(); //iterator
		while(it.hasNext()) {
			MyLog.d(TAG, it.next().toString());
		}		
	}

	//public void add(ArrayList<Poi> poiList) {
/*	public void add(UserList poiAddList) {
		MyLog.d(TAG, "add list");
		if (poiAddList == null) {
			MyLog.e(TAG, "poiAddList is null");
		} else if (this.poiArray == null) {
			this.poiArray = poiAddList.poiArray;
		} else {
			this.poiArray.addAll(poiAddList.poiArray);
		}
	}
	*/

//	public void add(ArrayList<Poi> copyArray) {
//		MyLog.d(TAG, "add list");
//		this.addAll(copyArray);
//	}
//		if (copyArray == null) {
//			MyLog.e(TAG, "poiArray is null");
//		} else {
//			this.addAll(copyArray);
//		}
	
/*	public Iterator<Poi> getIterator() {
		MyLog.d(TAG, "getIterator");
		if (poiArray == null) {
			MyLog.d(TAG, "getIterator is null");
			return null;
		}
		return poiArray.iterator();
	}*/

	public void delete(ArrayList<Poi> listToDelete) {
		MyLog.d(TAG, "delete");
		if (listToDelete == null) {
			MyLog.e(TAG, "listToDelete is null");
			return;
		}
		// Loop through parameter list first
		Iterator<Poi> delIt = listToDelete.iterator(); 
		while(delIt.hasNext()) {
			Poi delMe = delIt.next();
			Iterator<Poi> thisIt = iterator();
			while(thisIt.hasNext()) {
				if ( thisIt.next().equals(delMe) ) {
					thisIt.remove();
					break;
				}
			}
			
		}	
		
	}
}
