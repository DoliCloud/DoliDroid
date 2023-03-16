/* Copyright (C) 2023 Laurent Destailleur  <eldy@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * or see http://www.gnu.org/
 */

package com.nltechno.dolidroidpro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.nltechno.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
public class ManageURLActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidManageURLActivity";
	private String menuAre="hardwareonly";

	static final int RESULT_ABOUT =  RESULT_FIRST_USER;

	ListView listView;

	/**
	 * Called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate savedInstanceState="+savedInstanceState);
		super.onCreate(savedInstanceState);
		// Set the XML view to use
		setContentView(R.layout.activity_manageurl);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
    	Log.d(LOG_TAG, "prefAlwaysShowBar="+prefAlwaysShowBar+" prefAlwaysAutoFill="+prefAlwaysAutoFill); 

    	// Define kind of menu we want to use
        boolean hasMenuHardware = Utils.hasMenuHardware(this);
        if (! hasMenuHardware || prefAlwaysShowBar)
        {
        	this.menuAre="actionbar";
        }
        Log.d(LOG_TAG, "hasMenuHardware="+hasMenuHardware+" menuAre="+this.menuAre);

        // menuAre is defined to 'actionbar' or 'hardware'
        if (menuAre.equals("actionbar")) {
        	// Nothing
        } else {	// Menu are hardware
        	requestWindowFeature(Window.FEATURE_NO_TITLE);	// Hide title with menus
        }

		TextView t2 = findViewById(R.id.TextInstanceURLTitle);
		t2.setMovementMethod(LinkMovementMethod.getInstance());

		TextView t2b = findViewById(R.id.TextSavedLogins);
		t2b.setMovementMethod(LinkMovementMethod.getInstance());

		Log.d(LOG_TAG, "Open file " + MainActivity.FILENAME+ " in directory "+getApplicationContext().getFilesDir().toString());

		// Define an array with string to show into the ArrayAdapter list
		String[] listofRootUrlString = new String[MainActivity.listOfRootUrl.size()];
		String[] listofRootUrlStringEmpty = new String[0];
		int count = 0;
		while (count < MainActivity.listOfRootUrl.size()) {
			String tmps = MainActivity.listOfRootUrl.get(count).getDomainUrl().replaceAll("\\/$", "");
			tmps += " ("+MainActivity.listOfRootUrl.get(count).getScheme();
			if (! "".equals(MainActivity.listOfRootUrl.get(count).getBasicAuthLogin())) {
				tmps += " - "+MainActivity.listOfRootUrl.get(count).getBasicAuthLogin();
				//tmps += ":"+this.listOfRootUrl.get(i).getBasicAuthPass();
			}
			tmps += ")";

			listofRootUrlString[count] = tmps;
			count++;
		}

		ManageUrlAdapter adapter = new ManageUrlAdapter(this, listofRootUrlString);
		ManageUrlAdapter adapterempty = new ManageUrlAdapter(this, listofRootUrlStringEmpty);
		// Fill the list of Urls into the ArrayAdapter
		ListView listViewOfUrls = (ListView) findViewById(R.id.listViewConnections);
		listViewOfUrls.setAdapter(adapter);

		// Create listener to respond to click on button Delete current URL
		// Not using the android:onClick tag is bugged. Declaring listener is also faster.
		Button btn = findViewById(R.id.buttonDeletePredefinedUrl);
		btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "We click on Delete predefined Url");

				// Code is similar in delete onClick of the delete of the ManageUrlAdapter
				FileOutputStream fos;
				try
				{
					Intent intent = getIntent();
					String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");

					// Now loop of each entry of file MainActivity.FILENAME and rewrite or exclude the entry
					fos = openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE);
					int itoremove = -1;
					int sizeofarray = MainActivity.listOfRootUrl.size();
					for (int i = 0; i < sizeofarray; i++)
					{
						String s = MainActivity.listOfRootUrl.get(i).url;
						if (! s.equals(savedDolRootUrl)) {
							// Keep this value s
							Log.d(LOG_TAG, "write " + s);
							fos.write((s+"\n").getBytes());
						} else {
							// We fount the entry to remove
							Log.d(LOG_TAG, "exclude entry " + s);
							btn.setEnabled(false);
							btn.setTextColor(Color.LTGRAY);
							itoremove = i;
						}
					}
					fos.close();

					// If success, we can remove entry from memory array listOfRootUrl
					if (itoremove >= 0) {
						MainActivity.listOfRootUrl.remove(itoremove);
					}
				}
				catch(Exception ioe)
				{
					Log.e(LOG_TAG, "Error");
				}
			}
		});


		// Update menu label to add the number of predefined URL into label
		Button buttonClearAllUrl = findViewById(R.id.buttonClearAllUrl);
		if (MainActivity.listOfRootUrl != null) {
			buttonClearAllUrl.setText(getString(R.string.DeleteAllPredefinedUrl) + " (" + MainActivity.listOfRootUrl.size() + ")");
		} else {
			buttonClearAllUrl.setText(getString(R.string.DeleteAllPredefinedUrl) + " (0)");
		}

		// Create listener to respond to click on button Remove all predefined URL
		// Not using the android:onClick tag is bugged. Declaring listener is also faster.
		buttonClearAllUrl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "We click on Remove all predefined URLs");

				try {
					// Delete the file of predefined URLs MainActivity.FILENAME
					File file = new File(getApplicationContext().getFilesDir().toString() + "/" + MainActivity.FILENAME);
					Log.d(LOG_TAG, "Clear predefined URL list " + MainActivity.FILENAME + " (from ManageURLActivity) by deleting file with full path=" + file.getAbsolutePath());
					boolean result = file.delete();
					Log.d(LOG_TAG, result ? "true" : "false");

					MainActivity.listOfRootUrl = new ArrayList<PredefinedUrl>();    // Clear array of menu entry

					// Now update button label entry
					buttonClearAllUrl.setText(getString(R.string.DeleteAllPredefinedUrl));
					buttonClearAllUrl.setEnabled(true);

					listViewOfUrls.setAdapter(adapterempty);
					TextView textViewListOfUrl = findViewById(R.id.textListOfUrlsTitle);
					TextView textViewListOfUrl2 = findViewById(R.id.textListOfUrlsTitle2);
					textViewListOfUrl.setText(getString(R.string.menu_manage_all_urls));
					textViewListOfUrl.setVisibility(View.VISIBLE);
					textViewListOfUrl2.setVisibility(View.VISIBLE);

					// Clear also the list of saved login / pass too
					try {
						//SharedPreferences sharedPrefsEncrypted = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
						SharedPreferences sharedPrefsEncrypted = EncryptedSharedPreferences.create(
								"secret_shared_prefs",
								masterKeyAlias,
								getApplicationContext(),
								EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
								EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
						);
						SharedPreferences.Editor editorEncrypted = sharedPrefsEncrypted.edit();
						editorEncrypted.clear();	// delete the file
						editorEncrypted.commit();

						Log.d(LOG_TAG, "The encrypted shared preferences file has been cleared");
					} catch (Exception e) {
						Log.w(LOG_TAG, "Failed to clear encrypted shared preferences file");
					}
				} catch (Exception ioe) {
					Log.e(LOG_TAG, "Error");
				}
			}
		});
	}

	
	/**
	 * Called when activity start
	 */
	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
		
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	//boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
		Intent intent = getIntent();


		Button btn = findViewById(R.id.buttonDeletePredefinedUrl);


		// For api level 24: textViewAbout1.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_LEGACY));

		// Show text title
		TextView textListOfCurrentUrl = findViewById(R.id.textListOfCurrentUrl);
		TextView textViewAbout2 = findViewById(R.id.TextInstanceURLTitle);
		String s2="";

        String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");
        if (savedDolRootUrl != null && ! "".equals(savedDolRootUrl))
        {
			btn.setVisibility(View.VISIBLE);
			btn.setEnabled(true);

			//findViewById(R.id.imageView02).setVisibility(View.VISIBLE);
			//findViewById(R.id.imageView02).setEnabled(true);

        	s2=savedDolRootUrl;

			textListOfCurrentUrl.setVisibility(View.VISIBLE);
			textListOfCurrentUrl.setEnabled(true);
			textViewAbout2.setVisibility(View.VISIBLE);
			textViewAbout2.setEnabled(true);
			textViewAbout2.setText(Html.fromHtml(s2));
		} else {
        	// No need to show the button, we don't know the predefined url used.
			btn.setVisibility(View.INVISIBLE);
			btn.setEnabled(false);

			//findViewById(R.id.imageView02).setVisibility(View.INVISIBLE);
			//findViewById(R.id.imageView02).setEnabled(false);

			textListOfCurrentUrl.setVisibility(View.INVISIBLE);
			textListOfCurrentUrl.setEnabled(false);

			textViewAbout2.setVisibility(View.INVISIBLE);
			textViewAbout2.setEnabled(false);
			textViewAbout2.setText("");
		}


        // Show btn or not
		// Check if url is inside favorite URL
		boolean savedDolRootUrlFoundIntoPredefinedLoginUrl = false;
		try
		{
			Log.d(LOG_TAG, "Loop on listOfRootUrl "+MainActivity.listOfRootUrl.size());
			// Now loop of each entry and rewrite or exclude it
			for (int i = 0; i < MainActivity.listOfRootUrl.size(); i++)
			{
				PredefinedUrl tmppredefinedurl = MainActivity.listOfRootUrl.get(i);
				String s = tmppredefinedurl.url;
				Log.d(LOG_TAG, "Check for s="+s+" equal to savedDolRootUrl="+savedDolRootUrl);
				if (s.equals(savedDolRootUrl))	// Add new value into saved list
				{
					Log.d(LOG_TAG, "We found the savedDolRootUrl into the list of predefined URL, so we will show the button to remove it");
					savedDolRootUrlFoundIntoPredefinedLoginUrl = true;
				}
			}
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error");
		}
		if (savedDolRootUrlFoundIntoPredefinedLoginUrl) {
			btn.setTextColor(Color.WHITE);
		} else {
			btn.setTextColor(Color.LTGRAY);
		}


		// Show text section of login and pass
		TextView textViewAbout2b = findViewById(R.id.TextSavedLogins);
		String s2b="";

		if (savedDolRootUrl != null && ! "".equals(savedDolRootUrl))
		{
			// Saved user/pass
			String username=null;
			String password=null;
			Boolean tagToOverwriteLoginPass = prefAlwaysAutoFill;
			if (tagToOverwriteLoginPass)	// If we are allowed to overwrite username/pass into fields
			{
				try {
					//SharedPreferences sharedPrefsEncrypted = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
					SharedPreferences sharedPrefsEncrypted = EncryptedSharedPreferences.create(
							"secret_shared_prefs",
							masterKeyAlias,
							getApplicationContext(),
							EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
							EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
					);

					username = sharedPrefsEncrypted.getString(savedDolRootUrl + "-username", "");
					password = sharedPrefsEncrypted.getString(savedDolRootUrl + "-password", "");
				}
				catch(Exception e) {
					Log.w(LOG_TAG, "Failed to read the encrypted shared preference file");
				}
			}

			s2b+=getString(R.string.SavedLogin)+": "+(username != null ? username : "")+"<br />\n";
			s2b+=getString(R.string.SavedPassword)+": "+(password != null ? password.replaceAll(".", "*") : "")+"\n";

			// Basic auth user/pass used ?
			String savedAuthuser = intent.getStringExtra("savedAuthuser");
			if (savedAuthuser != null) s2b+="<br /><br />"+getString(R.string.BasicAuthLogin)+": "+savedAuthuser+"<br />";
			String savedAuthpass = intent.getStringExtra("savedAuthpass");
			if (savedAuthpass != null) s2b+=getString(R.string.BasicAuthPassword)+": "+savedAuthpass.replaceAll(".", "*")+"\n";
		}

		if (s2b != null && ! "".equals(s2b)) {
			textViewAbout2b.setVisibility(View.VISIBLE);
			textViewAbout2b.setEnabled(true);
			textViewAbout2b.setText(Html.fromHtml(s2b));
		} else {
			textViewAbout2b.setVisibility(View.INVISIBLE);
			textViewAbout2b.setEnabled(false);
			textViewAbout2b.setText("");
		}


		// Update the menu label to add the number of predefined URL into label
		TextView textViewListOfUrl = findViewById(R.id.textListOfUrlsTitle);
		TextView textViewListOfUrl2 = findViewById(R.id.textListOfUrlsTitle2);
		if (MainActivity.listOfRootUrl.size() >= 1) {
			// Set position of textViewListOfUrl
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textViewListOfUrl.getLayoutParams();
			if (s2b != null && !"".equals(s2b)) {
				layoutParams.addRule(RelativeLayout.BELOW, R.id.buttonDeletePredefinedUrl);
			} else {
				layoutParams.addRule(RelativeLayout.BELOW, R.id.imageTop);
			}
			textViewListOfUrl.setLayoutParams(layoutParams);

			if (MainActivity.listOfRootUrl != null) {
				textViewListOfUrl.setText(getString(R.string.menu_manage_all_urls) + " (" + MainActivity.listOfRootUrl.size() + ")");
				textViewListOfUrl.setVisibility(View.VISIBLE);
				textViewListOfUrl2.setVisibility(View.INVISIBLE);
			} else {
				textViewListOfUrl.setText(getString(R.string.menu_manage_all_urls));
				textViewListOfUrl.setVisibility(View.VISIBLE);
				textViewListOfUrl2.setVisibility(View.VISIBLE);
			}
		} else {
			// Set position of textViewListOfUrl
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textViewListOfUrl.getLayoutParams();
			if (s2b != null && !"".equals(s2b)) {
				layoutParams.addRule(RelativeLayout.BELOW, R.id.buttonDeletePredefinedUrl);
			} else {
				layoutParams.addRule(RelativeLayout.BELOW, R.id.imageTop);
			}
			textViewListOfUrl.setLayoutParams(layoutParams);

			textViewListOfUrl.setVisibility(View.VISIBLE);
			textViewListOfUrl2.setVisibility(View.VISIBLE);

			Button btnClearAll = findViewById(R.id.buttonClearAllUrl);
			btnClearAll.setVisibility(View.INVISIBLE);
			ListView listView = findViewById(R.id.listViewConnections);
			listView.setVisibility(View.INVISIBLE);
		}

	}
	
    /**
     *	Load Smartphone menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		Log.d(LOG_TAG, "onCreateOptionsMenu");
    	getMenuInflater().inflate(R.menu.activity_manageurl, menu);	// Deploy android menu
		//finish();		// If we finish here, activity will end immediatly when using HOLO theme.
        return true;
    }

    
    /**
     *	Once we selected a menu option
     */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        Log.d(LOG_TAG, "Click onto menu "+item.toString());

    	switch (item.getItemId())
    	{
	    	case R.id.menu_back:
	    		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_ABOUT);
	    		setResult(RESULT_ABOUT);
	    		finish();
	    		return true;
    	}
    	
    	return false;
    }

	/**
     * Once we click onto Smartphone hardware key
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN) 
		{
	    	Log.d(LOG_TAG, "We clicked onto key "+keyCode);

	    	// Check if the key event was the Back button and if there's history
		    if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) 
		    {
		    	Log.d(LOG_TAG, "We clicked onto KEYCODE_MENU or KEYCODE_BACK. We finish activity resultCode = "+RESULT_ABOUT);
	    		setResult(RESULT_ABOUT);
				finish();
				return true;
		    }
	    }
	    // If it wasn't the Back key or there's no web page history, bubble up to the default, system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	} 
	
    /**
     * Click onto text Back
     */
    @SuppressLint("SetJavaScriptEnabled")
	public void onClickToBack(View v) 
    {
		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_ABOUT);
		setResult(RESULT_ABOUT);
    	finish();
    } 
    
	/**
	 * onActivityResult
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.d(LOG_TAG, "ManageURLActivity::onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        if (resultCode==RESULT_ABOUT)
        {
			Log.d(LOG_TAG, "ManageURLActivity::onActivityResult We finish activity resultCode = "+RESULT_ABOUT);
    		setResult(RESULT_ABOUT);
            finish();
        } 
    }

}
