package com.example.smsfilter;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {
//intercepting system broadcast message(incoming SMS in this case)
	ProgressDialog msgDialog;
	SharedPreferences appSp;
	//constants for settings as defined in the preference file
	final static String contact = "contact";
	final static String duration = "duration";
	final static String kmsg = "keepmsg" ;
	final static String filt_msg = "filtered_msg";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
	//	Toast.makeText(arg0, "Intent Caught 5/5", Toast.LENGTH_LONG).show();
		Bundle bundle =  arg1.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[0]); //read incoming sms message and store as message object
		 
	try{	
		appSp = PreferenceManager.getDefaultSharedPreferences(arg0);
		Editor ed = appSp.edit();
		String name = appSp.getString(MessageReceiver.contact, "");
		boolean val = appSp.getBoolean(MessageReceiver.kmsg, false);
		int duration = Integer.parseInt(appSp.getString(MessageReceiver.duration, ""));
		String bad_msg = appSp.getString(MessageReceiver.filt_msg, "");
		SmsFilterActivity sm_f = new SmsFilterActivity();
		sm_f.notifyUser();//notifyUser();
		//if the sender exists and was blocked or not yet expired then prevent message from being stored
		if(msg.getOriginatingAddress().contains(name) && !val && duration != 0)
		{
			//delete the sms
			abortBroadcast();
			ed.putString(MessageReceiver.duration, new String(""+(duration-1)).toString()); //reduce the duration by 1 each time we get the sms
			ed.commit();
			Toast.makeText(arg0, "Message From: "+msg.getOriginatingAddress()+" was Trashed.", Toast.LENGTH_LONG).show();
		
		}
		else if(msg.getDisplayMessageBody().contains(bad_msg) && duration !=0)
		{
			abortBroadcast();
			ed.putString(MessageReceiver.duration, new StringBuilder(""+(duration-1)).toString()); //reduce the duration by 1 each time we get the sms
			ed.commit();
			Toast.makeText(arg0, "Bad Message. SMS Filtered", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(arg0, "Filter Free Message. No Further Action Required", Toast.LENGTH_LONG).show();
		
	}
	catch(Exception ex)
	{
		Toast.makeText(arg0, "Error Message:  "+ex.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	
//	Log.d("Setting Values", name+" "+val+" "+duration);
	}//end the onReceive method

}
