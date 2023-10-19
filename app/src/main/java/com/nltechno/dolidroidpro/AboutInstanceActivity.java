/* Copyright (C) 2013 Laurent Destailleur  <eldy@users.sourceforge.net>
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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.widget.TextView;

import com.nltechno.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class AboutInstanceActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidAboutInstanceActivity";
	private String menuAre="hardwareonly";

	static final int RESULT_ABOUT_INSTANCE =  RESULT_FIRST_USER;

	/**
	 * Called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate savedInstanceState="+savedInstanceState);
		super.onCreate(savedInstanceState);

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

		setContentView(R.layout.activity_about_instance);

		Log.d(LOG_TAG, "Open file " + MainActivity.FILENAME+ " in directory "+getApplicationContext().getFilesDir().toString());
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

        String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");

        // Show btn or not
		// Check if url is inside predefined URL
		boolean savedDolRootUrlFoundIntoPredefinedLoginUrl = false;
		try
		{
			Log.d(LOG_TAG, "Loop on listOfRootUrl "+MainActivity.listOfRootUrl.size());

			// Now loop of each entry and rewrite or exclude it
			for (int i = 0; i < MainActivity.listOfRootUrl.size(); i++)
			{
				String s = MainActivity.listOfRootUrl.get(i).url;
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

		// Show text section 3
		TextView textViewAbout3 = findViewById(R.id.textAboutCurrentUrl);
		String s3="";

		// Current url
        String currentUrl = intent.getStringExtra("currentUrl");
        String title = intent.getStringExtra("title");
        if (currentUrl != null && ! "".equals(currentUrl)) {
			String pattern = "^(https?://[^:]+):[^@]+@";
			Pattern regexPattern = Pattern.compile(pattern);
			Matcher matcher = regexPattern.matcher(currentUrl);
			String currentUrlWithoutPass = matcher.replaceFirst("$1:*****@");
        	s3+="<font color='#440066'><b>"+getString(R.string.currentUrl)+":</b></font><br /><br />\n"+title+"<br />\n"+currentUrlWithoutPass;
		}
		String lastversionfound = intent.getStringExtra("lastversionfound");
        if (lastversionfound != null && ! "".equals(lastversionfound)) {
        	s3+="<br /><br />\nDolibarr "+getString(R.string.Version)+": "+lastversionfound+"<br />\n";
		}

		// User agent
		// The About view is not a webview, so we must use the userAgent propagated by the SecondActivity. It may be null if not already created.
        String userAgent = intent.getStringExtra("userAgent");
        Log.d(LOG_TAG,"userAgent="+userAgent);
        if (userAgent != null && ! "".equals(userAgent)) {
        	s3+="<br /><br />\n<font color='#440066'><b>"+getString(R.string.UserAgent)+":</b></font><br /><br />\n"+userAgent+"<br />\n";
		}

		if (currentUrl != null && ! "".equals(currentUrl)) {
			findViewById(R.id.imageView03).setVisibility(View.VISIBLE);
			findViewById(R.id.imageView03).setEnabled(true);
			textViewAbout3.setText(Html.fromHtml(s3));
			textViewAbout3.setVisibility(View.VISIBLE);
			textViewAbout3.setEnabled(true);
		} else {
			findViewById(R.id.imageView03).setVisibility(View.INVISIBLE);
			findViewById(R.id.imageView03).setEnabled(false);
			textViewAbout3.setText("");
			textViewAbout3.setVisibility(View.INVISIBLE);
			textViewAbout3.setEnabled(false);
		}
	}
	
    /**
     *	Load Smartphone menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		Log.d(LOG_TAG, "onCreateOptionsMenu");
    	getMenuInflater().inflate(R.menu.activity_about, menu);	// Deploy android menu
		//finish();		// If we finish here, activity will end immediatly when using HOLO theme.
        return true;
    }

    
    /**
     *	Once we selected a menu option
     */
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        Log.d(LOG_TAG, "Click onto menu "+item.toString() + " from AboutInstanceActivity");

    	switch (item.getItemId())
    	{
	    	case R.id.menu_back:
	    		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_ABOUT_INSTANCE);
	    		setResult(RESULT_ABOUT_INSTANCE);
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
		    	Log.d(LOG_TAG, "We clicked onto KEYCODE_MENU or KEYCODE_BACK. We finish activity resultCode = "+RESULT_ABOUT_INSTANCE);
	    		setResult(RESULT_ABOUT_INSTANCE);
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
		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_ABOUT_INSTANCE);
		setResult(RESULT_ABOUT_INSTANCE);
    	finish();
    } 
    
	/**
	 * onActivityResult
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.d(LOG_TAG, "AboutInstanceActivity::onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        if (resultCode==RESULT_ABOUT_INSTANCE)
        {
			Log.d(LOG_TAG, "AboutInstanceActivity::onActivityResult We finish activity resultCode = "+RESULT_ABOUT_INSTANCE);
    		setResult(RESULT_ABOUT_INSTANCE);
            finish();
        } 
    }

}
