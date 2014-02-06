package com.mckenna.thegame;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notifier {
	Context context;
	int notificationId = 4563456;
	
	public Notifier(Context context) {
		this.context = context;
	}
	
	public Notification getNotification(String title, String message) {
		
		Intent openAppIntent = new Intent(context, GameActivity.class);
		openAppIntent.putExtra("GAMEID", Singleton.getInstance().getCurrentGameId());
		
		Intent exitAppIntent = new Intent(context, BackgroundService.class);
		exitAppIntent.putExtra("GAMEID", "");
		
		PendingIntent openAppPendingIntent =
			PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		PendingIntent exitPendingIntent =
			PendingIntent.getActivity(context, 0, exitAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Build the notification
		NotificationCompat.Builder builder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.rabbit_icon_23x32)
			    .setContentTitle(title)
			    .setContentText(message)
			    .addAction(R.drawable.ball_blue_2, "View", openAppPendingIntent)
			    .addAction(0, "Stop Game", exitPendingIntent)
			    .setContentIntent(openAppPendingIntent)
			    ;
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		// Gets an instance of the NotificationManager service
//		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		//myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Builds the notification and issues it.
//		mNotifyMgr.notify(notificationId, notification);
		
		return notification;
	}
	
    public void cancelNotification(){

    	if (Context.NOTIFICATION_SERVICE!=null) {
    		String ns = Context.NOTIFICATION_SERVICE;
    		NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
    		nMgr.cancel(notificationId);
    	}
    }
}
