package com.example.smsfilter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.larrytechcorp.smsfilter.R;

public class AppSettingsActivity extends PreferenceActivity {
	final int CONTACT = 10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.appprefrences);
		setupActionBar();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resultCode, Intent it)
	{
		//when user picks a contact, extract the number then add it to the black list
		Toast.makeText(getApplicationContext(), "Contact Received", Toast.LENGTH_LONG).show();
	}
}
