package com.mckenna.thegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class GameDialogFragment extends DialogFragment implements OnClickListener {

	DialogCallback dialogCallback; 
	String whichDialog;
	Object obj;
	String title;
	String text;
	
	public static GameDialogFragment newInstance(String whichDialog, String title, String text, Object obj, DialogCallback dialogCallback) {
		GameDialogFragment f = new GameDialogFragment();
	
	    // Supply num input as an argument.
	    Bundle args = new Bundle();
	    int num = 0;
		args.putInt("num", num);
	    f.setArguments(args);
	
	    return f;
	}
	
	/*public static void newInstance(String whichDialog, String title, String text, Object obj, DialogCallback dialogCallback) {
    	// Store the parameters we will need in the callback
    	this.dialogCallback = dialogCallback;
    	this.whichDialog = whichDialog;
    	this.obj = obj;
    	this.title = title;
    	this.text = text;
	}*/
	
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	// Create the dialog ui
        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(text)
            .setNegativeButton(android.R.string.no, this)
            .setPositiveButton(android.R.string.yes, this)
            .create();
    }

	@Override
	public void onClick(DialogInterface dialog, int whichButton) {
		dialogCallback.dialogAnswered(whichDialog, obj, whichButton);
	}
}
