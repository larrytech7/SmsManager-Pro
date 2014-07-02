package com.example.smsfilter;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.larrytechcorp.smsfilter.R;

public class SmsFilterActivity extends Activity implements TextToSpeech.OnInitListener{

	private TextView msgtext; //= (TextView) findViewById(R.id.autoCompleteTextView1);
	private TextView msgaddr;
	private String SMTP_HOSTNAME;
	FlyOutContainer root;
	final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	final int PICK_DATA = 100;
	private Intent sentIntent;
	private PendingIntent sentPI;
	private Intent deliveryIntent;
	private PendingIntent deliverPI;
	private SharedPreferences appsettings;
	TextToSpeech tts;
	
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
		case R.id.action_sms:
			// Read the language locale from the application's settings and set the locale for the TTS engine
			//appsettings =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			//String localeString = appsettings.getString("langlocale", "No Language found");
			//Toast.makeText(this, localeString, Toast.LENGTH_LONG).show();
			//Locale loc = new Locale(localeString.toUpperCase());
			tts=new TextToSpeech(SmsFilterActivity.this, this);
		
			return true;
			
		default:
			return  super.onOptionsItemSelected(mit);
		}
		
	}
	
	private void ConvertTextToSpeech() {
	        // if the message is empty, there is nothing to read
		// just read out the default message of content not available
	        String text = msgtext.getText().toString();
	        if(text==null||"".equals(text))
	        {
	            text = "Content not available";
	            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	        }else
	            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
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
	// Create the sentIntent parameter
	sentIntent = new Intent(SENT_SMS_ACTION);
	sentPI = PendingIntent.getBroadcast(getApplicationContext(),0,sentIntent,0);
	// Create the deliveryIntent parameter
	 deliveryIntent = new Intent(DELIVERED_SMS_ACTION);
	 deliverPI = PendingIntent.getBroadcast(getApplicationContext(),0,deliveryIntent,0);
	
	// Register the Broadcast for sending
	registerReceiver(new BroadcastReceiver() {
	@Override
	public void onReceive(Context _context, Intent _intent) {
	switch (getResultCode()) {
	case Activity.RESULT_OK:
	//[� send success actions � ];
		Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
		break;
	case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	//[� generic failure actions � ];
		Toast.makeText(getApplicationContext(), "GENERIC FAILURE: \n CODE"+SmsManager.RESULT_ERROR_GENERIC_FAILURE, Toast.LENGTH_LONG).show();
		break;
	case SmsManager.RESULT_ERROR_RADIO_OFF:
	//[� radio off failure actions � ]; 
		Toast.makeText(getApplicationContext(), "Radio OFF, CODE: "+SmsManager.RESULT_ERROR_RADIO_OFF, Toast.LENGTH_LONG).show();
		break;
	case SmsManager.RESULT_ERROR_NULL_PDU:
	//[� null PDU failure actions � ];
		Toast.makeText(getApplicationContext(), "EMPTY DATA: ERROR: "+SmsManager.RESULT_ERROR_NULL_PDU, Toast.LENGTH_LONG).show();
		break;
	}
	}
	},new IntentFilter(SENT_SMS_ACTION));
	//register delivered broadcast
	registerReceiver(new BroadcastReceiver() {
		@Override
		public void onReceive(Context _context, Intent _intent) {
	//	[� SMS delivered actions � ]
			Toast.makeText(getApplicationContext(), "Message Successfully Delivered", Toast.LENGTH_LONG).show();
		}
		}, new IntentFilter(DELIVERED_SMS_ACTION));
	
	}
	
	public void notifyUser()
	{
		final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Outgoing Messsage")
                .setContentText("Message is being sent")
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
		/*String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";*/
		SmsManager smsObject = SmsManager.getDefault();
		try{
			if(msgType.contains("text messages")){
				
			smsObject.sendTextMessage("+237"+msgaddr.getText().toString(), null, msg, sentPI, deliverPI);
			//smsObject.sendMultipartTextMessage(destinationAddress, scAddress, parts, sentIntents, deliveryIntents)
			notifyUser();
			//msg.
			}
			else if(msgType.contains("email message"))
			{
			String[] to = {"weimenglee9learn2develop.net" , "larryakah@gmail.com", msgaddr.getText().toString() }; 
			String[] cc = {"course9learn2develop.net" };
			sendEmail(to, cc,new String[]{"webmail@gmail.com","crysmatech@gmail.com"}, "Hi-Tech software" , msg );
		//	smsit.setData(Uri.parse("smsto: +23797950531"));
			//smsit.setType(");
	//		startActivity(smsit);
			}
			else if(msgType.contains("mms message"))
			{
				Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
				pick.setType("image/*");
				startActivityForResult(pick, PICK_DATA);
			}
		}
		catch(Exception ex)
		{
			Toast.makeText(getApplicationContext(), "Message not sent. Please try again: "+ex.getMessage(), Toast.LENGTH_LONG).show();
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
	Intent emailIntent = new Intent(Intent.ACTION_SEND); 
	emailIntent.setData(Uri.parse("mailto:"));
	String[] to = emailAddresses; String[] cc = carbonCopies;
	emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
	emailIntent.putExtra(Intent.EXTRA_CC, cc);
	emailIntent.putExtra(Intent.EXTRA_BCC, blindcc);
	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); 
	emailIntent.putExtra(Intent.EXTRA_TEXT, message);
	emailIntent.setType("message/rfc822"); 
	startActivity(Intent.createChooser(emailIntent, "Email"));

	}
	
	@Override
	public void onActivityResult(int requestcode, int resultcode, Intent intentData)
	{
		try{
		if(requestcode == PICK_DATA )
		{
			Intent send = new Intent();
			send.setAction(Intent.ACTION_SEND);// Intent.ACTION_GET_CONTENT; Intent.ACTION_SCREEN_OFF;
			send.setData(Uri.parse("smsto:"));
			//send.setType("vnd.android-dir/mms-sms");
		//	send.setType("text/plain");
			send.putExtra("address", msgaddr.getText().toString());
			send.putExtra("sms_body", msgtext.getText().toString());
			send.putExtra(Intent.EXTRA_STREAM,intentData.getData());
			send.setType("image/*");
			//sms_body was in a putExtra method
			/*if(send.getExtras().getInt(Intent.EXTRA_STREAM) == 0)
				Toast.makeText(getApplicationContext(), "No Data Selected. MMS Aborted", Toast.LENGTH_LONG).show();
			else	*/	
			startActivity(send);
		}
		else
			Toast.makeText(getApplicationContext(), "Nothing to do.\n"
					+ "Result code: "+resultcode+"\nRequestCode: "+requestcode
					+RESULT_OK+"\n"+PICK_DATA, Toast.LENGTH_LONG).show();
	}
		catch(Exception ex )
		{
			Toast.makeText(getApplicationContext(), "Exception: "+ex.getMessage(), Toast.LENGTH_LONG).show();
		}
}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onResume()
	{
		super.onResume();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String msgType = sp.getString("msg_type", "message");
			if(msgType.contains("email message"))
			{
				msgaddr.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
			}
	}
	
	@Override 
	public void onPause()
	{
		super.onPause();
		try{
		tts.stop();
		tts.shutdown();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.d("Cannot Pause Activity", ""+ex.getLocalizedMessage());
		}
	}

	
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		 if(status == TextToSpeech.SUCCESS){
     
            appsettings =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
 			String localeString = appsettings.getString("langlocale", "No Language found");
 			Toast.makeText(this, localeString, Toast.LENGTH_LONG).show();
 			//Locale lc = new Locale("");
 			//localeString = localeString.toUpperCase(lc);
 			Locale loc = new Locale(localeString.toUpperCase(Locale.getDefault()));
 			
 			int result;//=tts.setLanguage(Locale.FRENCH);
 			switch(localeString)
 			{
 			case "CANADA":
 				result = tts.setLanguage(Locale.CANADA);
 				break;
 			case "CANADA_FRENCH":
 				result = tts.setLanguage(Locale.CANADA_FRENCH);
 				break;
 			case "CHINA":
 				result = tts.setLanguage(Locale.CHINA);
 				break;
 			case "CHINESE":
 				result = tts.setLanguage(Locale.CHINESE);
 				break;
 			case "ENGLISH":
 				result = tts.setLanguage(Locale.ENGLISH);
 				break; 
 			case "FRANCE":
 				result = tts.setLanguage(Locale.FRANCE);
 				break; 
 			case "FRENCH":
 				result = tts.setLanguage(Locale.FRENCH);
 				break;
 			case "GERMAN":
 				result = tts.setLanguage(Locale.GERMAN);
 				break;
 			case "GERMANY":
 				result = tts.setLanguage(Locale.GERMANY);
 				break;
 			case "ITALIAN":
 				result = tts.setLanguage(Locale.ITALIAN);
 				break;
 			case "ITALY":
 				result = tts.setLanguage(Locale.ITALY);
 				break;
 			case "JAPAN":
 				result = tts.setLanguage(Locale.JAPAN);
 				break;
 			case "JAPANESE":
 				result = tts.setLanguage(Locale.JAPANESE);
 				break;
 			case "KOREA":
 				result = tts.setLanguage(Locale.KOREA);
 				break;
 			case "KOREAN":
 				result = tts.setLanguage(Locale.KOREAN);
 				break;
 			case "PRC":
 				result = tts.setLanguage(Locale.PRC);
 				break;
 			case "SIMPLIFIED_CHINESE":
 				result = tts.setLanguage(Locale.SIMPLIFIED_CHINESE);
 				break;
 			case "TAIWAN":
 				result = tts.setLanguage(Locale.TAIWAN);
 				break;
 			case "UK":
 				result = tts.setLanguage(Locale.UK);
 				break;
 			case "US":
 				result = tts.setLanguage(Locale.US);
 				break;
 			case "TRADITONAL_CHINESE":
 				result = tts.setLanguage(Locale.TRADITIONAL_CHINESE);
 				break;
 			default:
 				result = tts.setLanguage(Locale.US);
 				break;
 			}
 			 if(result==TextToSpeech.LANG_MISSING_DATA ||
                     result==TextToSpeech.LANG_NOT_SUPPORTED){
                 Log.e("error", "This Language is not supported");
                 Toast.makeText(getApplicationContext(), "This Language is not supported: "+loc.toString(), Toast.LENGTH_LONG).show();
             }
             else{
                 ConvertTextToSpeech();
             }
         }
         else{
             Log.e("error", "Initilization Failed!");
             Toast.makeText(getApplicationContext(), "Initialization Failed", Toast.LENGTH_LONG).show();
     }
	}
}
	
