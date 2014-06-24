package com.example.smsfilter;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SmsFilterActivity extends Activity {

	private TextView msgtext; //= (TextView) findViewById(R.id.autoCompleteTextView1);
	private TextView msgaddr;
	private String SMTP_HOSTNAME;
	FlyOutContainer root;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.appprefrences, false);
		root = (FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_sms_filter, null);
		//setContentView(R.layout.activity_sms_filter);
		setContentView(root);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sms_filter, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem mit)
	{
		switch(mit.getItemId())
		{
		case	R.id.action_settings:
			startActivity(new Intent(this,AppSettingsActivity.class));
			return true;
		case	R.id.action_about:
			this.root.toggleMenu();
			
			return true;
		case	R.id.action_exit:
			System.exit(0);
			return true;
		/*case	R.id.action_mail:
			//send the mail
			String[] to = {"weimenglee9learn2develop.net" , "larryakah@gmail.com" }; String[] cc = {"course9learn2develop.net" };
			sendEmail(to, cc, new String[]{"webmail@gmail.com","crysmatech@gmail.com"},"Hi-Tech software" , "Welcome to Larrytech Corp" );
			return true;*/
		case R.id.action_sms:
			//send the sms
			return true;
			
		default:
			return  super.onOptionsItemSelected(mit);
		}
		
	}
	/*
	public void SendEmail(View but)
	{
		String to = "larryakah@gmail.com";
		String from	="admin@gmail.com";
		
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", SMTP_HOSTNAME);
		
		Session session = Session.getInstance(props, new Authenticator() {
			private String USERNAME;
			private char[] PASSWORD;

			@Override
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(USERNAME, PASSWORD);
				
			}
		});
		
		try{
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.addRecipient(msg.RecipientType.TO, new InternetAddress(to));
			msg.setSubject("Message Subject");
			msg.setText("Message Body");
			Transport.send(msg);
			Toast.makeText(this, "MEssage Sent Successfully", Toast.LENGTH_LONG).show();
		}
		catch(MessagingException mex)
		{
			mex.printStackTrace();
		}
	}
	*/
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
	msgtext = (TextView) findViewById(R.id.autoCompleteTextView1);
	msgaddr = (TextView) findViewById(R.id.editText1);
	//notifyUser();
}
	
	public void notifyUser()
	{
		final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Outgoing Messsage")
                .setContentText("Mesasge is being sent")
                .setAutoCancel(true);
       
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SmsFilterActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SmsFilterActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

	}
	
	public void sendMessage(View v)
	{
		//send the message typed Depending on the settings
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String msgType = sp.getString("msg_type", "message");
		String msg = msgtext.getText().toString();
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		SmsManager smsObject = SmsManager.getDefault();
		try{
			if(msgType.contains("text messages")){
				
				// Create the sentIntent parameter
				Intent sentIntent = new Intent(SENT_SMS_ACTION);
				PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(),0,sentIntent,0);
				// Create the deliveryIntent parameter
				Intent deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
				PendingIntent deliverPI = PendingIntent.getBroadcast(getApplicationContext(),0,deliveryIntent,0);
				
				// Register the Broadcast for sending
				registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
				//[… send success actions … ];
					Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				//[… generic failure actions … ];
					Toast.makeText(getApplicationContext(), "GENERIC FAILURE: \n CODE"+SmsManager.RESULT_ERROR_GENERIC_FAILURE, Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				//[… radio off failure actions … ]; 
					Toast.makeText(getApplicationContext(), "Radio OFF, CODE: "+SmsManager.RESULT_ERROR_RADIO_OFF, Toast.LENGTH_LONG).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
				//[… null PDU failure actions … ];
					Toast.makeText(getApplicationContext(), "EMPTY DATA: ERROR: "+SmsManager.RESULT_ERROR_NULL_PDU, Toast.LENGTH_LONG).show();
					break;
				}
				}
				},new IntentFilter(SENT_SMS_ACTION));
				//register delivered broadcast
				registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context _context, Intent _intent) {
				//	[… SMS delivered actions … ]
						Toast.makeText(getApplicationContext(), "Message Successfully Delivered", Toast.LENGTH_LONG).show();
					}
					}, new IntentFilter(DELIVERED_SMS_ACTION));
					// Send the message
					//smsManager.sendTextMessage(sendTo, null, myMessage, sentPI, deliverPI);
			smsObject.sendTextMessage("+237"+msgaddr.getText().toString(), null, msg, sentPI, deliverPI);
			notifyUser();
			//Intent smsit = new Intent(Intent.ACTION_SEND);
			}
			else
			{
			String[] to = {"weimenglee9learn2develop.net" , "larryakah@gmail.com", msgaddr.getText().toString() }; 
			String[] cc = {"course9learn2develop.net" };
			sendEmail(to, cc,new String[]{"webmail@gmail.com","crysmatech@gmail.com"}, "Hi-Tech software" , msg );
		//	smsit.setData(Uri.parse("smsto: +23797950531"));
			//smsit.setType(");
	//		startActivity(smsit);
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(getApplicationContext(), "Message not sent. Please try again", Toast.LENGTH_LONG).show();
		}
	}

	//version 1.0 simple email client
	public void sendEmails(String[] tos, String[] cc, String sbj, String msgbody)
	{
		//using a default email sending client
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("text/plain");
		it.putExtra(Intent.EXTRA_EMAIL, tos); //receipients
		it.putExtra(Intent.EXTRA_TEXT, msgbody); //body
		try{
			startActivity(Intent.createChooser(it, "Sending mails ..."));
		}
		catch(android.content.ActivityNotFoundException aex)
		{
			Toast.makeText(getApplicationContext(), "No email clients Found or installed", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void sendEmail(String[] emailAddresses, String[] carbonCopies, String[] blindcc, String subject, String message)
	{
		//using intents  method pro
	Intent emailIntent = new Intent(Intent.ACTION_SEND); emailIntent.setData(Uri.parse("mailto:"));
	String[] to = emailAddresses; String[] cc = carbonCopies;
	emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
	emailIntent.putExtra(Intent.EXTRA_CC, cc);
	emailIntent.putExtra(Intent.EXTRA_BCC, blindcc);
	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); 
	emailIntent.putExtra(Intent.EXTRA_TEXT, message);
	emailIntent.setType("message/rfc822"); 
	startActivity(Intent.createChooser(emailIntent, "Email"));

	}
	/*sms response notifications
	 * Intent sentit = new Intent("sent_sms_action");
	 * PendingIntent sentpi = PendingIntent.getBroadcast(getApplicationContext(), 0, sentit, 0);
	 * Intent responseit = new Intent("deliver_sms_action");
	 * PendingIntent response = PengingIntent.getBroadcast(getApplicationContext(), 0, responseit, 0);
	 * 
	 * startActivity(sentit);
	 * 
	 *  //registering the receivers
	 *  registerReceiver(new BroadcastReceiver(){
	 *  @Override
	 *  public void onReceive(Context ctx, Intent it)
	 *  {
	 *  	switch(getResultCode())
	 *  {
	 *  	case Activity.RESULT_OK:
	 *  		//message sent with success
	 *  		break;
	 *  }
	 *  }
	 *  }, new IntentFilter("sent_sms_action"));
	 *  registerReceiver(new BroadcastReceiver(){
	 *  	@Override
	 *      public void onReceive(Context ctx, Intent it)
	 *      {
	 *      	//message delivered successfully
	 *      }
	 *  }, new IntentFilter("deliver_sms_action"));
	 *  
	 *  // now we use the sms manager API to put everything together
	 *  	SmsManger mysms = SmsManager.getDefault();
	 *	    mysms.sendTextMessage("receipient", null, "Message", sentpi, responseit);
	 */
}
	
