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

import java.io.FileOutputStream;
import java.util.List;

import com.nltechno.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class AboutActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidAboutActivity";
	private String menuAre="hardwareonly";

	static final int RESULT_ABOUT =  RESULT_FIRST_USER;

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

		setContentView(R.layout.activity_about);

		// text2 has links specified by putting <a> tags in the string
		// resource.  By default these links will appear but not
		// respond to user input.  To make them active, you need to
		// call setMovementMethod() on the TextView object.
		TextView t1 = findViewById(R.id.TextAbout01);
		t1.setMovementMethod(LinkMovementMethod.getInstance());

		TextView t2 = findViewById(R.id.TextAbout02);
		t2.setMovementMethod(LinkMovementMethod.getInstance());

		TextView t2b = findViewById(R.id.TextAbout02b);
		t2b.setMovementMethod(LinkMovementMethod.getInstance());

		Log.d(LOG_TAG, "Open file " + MainActivity.FILENAME+ " in directory "+getApplicationContext().getFilesDir().toString());


		// Create listener to respond to click on button
		// Not using the android:onClick tag is bugged.
		// Declaring listener is also faster.
		Button btn = findViewById(R.id.buttonDeletePredefinedUrl);
		btn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Log.d(LOG_TAG, "We click on Delete predefined Url");

				FileOutputStream fos;
				try
				{
					Intent intent = getIntent();
					String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");

					// Now loop of each entry and rewrite or exclude it
					fos = openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE);
					int itoremove = -1;
					int sizeofarray = MainActivity.listOfRootUrl.size();
					for (int i = 0; i < sizeofarray; i++)
					{
						String s=MainActivity.listOfRootUrl.get(i);
						if (! s.equals(savedDolRootUrl))	// Add new value into saved list
						{
							Log.d(LOG_TAG, "write " + s);
							fos.write((s+"\n").getBytes());
						} else {
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


		// Show text section 1
		TextView textViewAbout1 = findViewById(R.id.TextAbout01);
		String s1="";

		PackageManager manager = this.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

			s1+=getString(R.string.Version)+": <b>"+info.versionName+" (build "+info.versionCode+")</b><br />\n";
			s1+=getString(R.string.VersionStaticResources)+": <b>"+SecondActivity.VERSION_RESOURCES+"</b><br />\n";

			//s+= "PackageName = " + info.packageName + "\n";
			s1+=getString(R.string.Author)+": <b>Laurent Destailleur</b><br />\n";
			s1+=getString(R.string.Web)+": <span style=\"color:#008888\"><a href=\"https://www.dolicloud.com?origin=dolidroid&amp;utm_source=dolidroid&amp;utm_campaign=none&amp;utm_medium=mobile\">https://www.dolicloud.com</a></span><br />\n";
			s1+=getString(R.string.Compatibility)+": <b>Dolibarr 8+</b><br />\n";
			s1+=getString(R.string.License)+": <b>GPL v3+</b><br />\n";
			//s1+=getString(R.string.Sources)+": https://www.nltechno.com/services/<br />\n";
			s1+=getString(R.string.Sources)+": <span style=\"color:#008888\"><a href=\"https://github.com/DoliCloud/DoliDroid.git\">https://github.com/DoliCloud/DoliDroid.git</a></span><br />\n";
			// This download key allow to download file with name src_dolidroid-info.versionName-downloadkey
			//String downloadkey=Utils.MD5Hex("dolidroid"+info.versionName.replaceAll("[^0-9.]", "")+"saltnltechno").substring(0, 8);
			//s1+=getString(R.string.Sources)+" Download Key: dolidroid-"+info.versionName.replaceAll("[^0-9.]", "")+"-"+downloadkey+"<br />\n";

			s1+="<br />\n";
			
			s1+=getString(R.string.DeviceAPILevel)+": <b>"+Build.VERSION.SDK_INT+"</b><br />\n";
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			s1+=getString(R.string.DeviceSize)+": <b>"+width+"x"+height+"</b><br />";
			//s1+=getString(R.string.DeviceHasMenuHardware)+": <b>"+(Utils.hasMenuHardware(this)?getString(R.string.Yes):getString(R.string.No))+"</b><br />\n";
			s1+=getString(R.string.DeviceHasDownloadManager)+": <b>"+(Utils.isDownloadManagerAvailable(this)?getString(R.string.Yes):getString(R.string.No))+"</b><br />\n";

			// This return /storage/sdcard0/Download for example (we use this for downloading files)
			String downloaddirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			// This return /storage/sdcard0 for example (we do not use this)
			//String downloaddir="";
		    //if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) downloaddir = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.DownloadDirectory)+": <b>"+downloaddirpublic+"</b><br />\n";

			String photosdirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			//if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) photosdirpublic = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.PhotosDirectory)+": <b>"+photosdirpublic+"</b><br />\n";

			Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType("application/pdf"); 
            List<ResolveInfo> list = manager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasPDFViewer)+": <b>"+(list.size() > 0?getString(R.string.Yes)+" ("+list.size()+")":getString(R.string.No))+"</b><br />\n";

			Intent testIntent2 = new Intent(Intent.ACTION_VIEW);
            testIntent2.setType("application/vnd.oasis.opendocument.text"); 
            List<ResolveInfo> list2 = manager.queryIntentActivities(testIntent2, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasODXViewer)+": <b>"+(list2.size() > 0?getString(R.string.Yes)+" ("+list2.size()+")":getString(R.string.No))+"</b>\n";
           
			//s+="Permissions = " + info.permissions;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}

		textViewAbout1.setText(Html.fromHtml(s1));
		// For api level 24: textViewAbout1.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_LEGACY));


		// Show text section 2
		TextView textViewAbout2 = findViewById(R.id.TextAbout02);
		String s2="";

        String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");
        if (savedDolRootUrl != null && ! "".equals(savedDolRootUrl))
        {
			btn.setVisibility(View.VISIBLE);
			btn.setEnabled(true);

			findViewById(R.id.imageView02).setVisibility(View.VISIBLE);
			findViewById(R.id.imageView02).setEnabled(true);

			s2+="<font color='#440066'><b>"+getString(R.string.savedDolUrlRoot)+":</b></font><br /><br />\n";
        	s2+=savedDolRootUrl+"<br />\n";

			textViewAbout2.setVisibility(View.VISIBLE);
			textViewAbout2.setEnabled(true);
			textViewAbout2.setText(Html.fromHtml(s2));
		} else {
        	// No need to show the button, we don't know the predefined url used.
			btn.setVisibility(View.INVISIBLE);
			btn.setEnabled(false);

			findViewById(R.id.imageView02).setVisibility(View.INVISIBLE);
			findViewById(R.id.imageView02).setEnabled(false);

			textViewAbout2.setVisibility(View.INVISIBLE);
			textViewAbout2.setEnabled(false);
			textViewAbout2.setText("");
		}


        // Show btn or not
		// Check if url is inside predefined URL
		boolean savedDolRootUrlFoundIntoPredefinedLoginUrl = false;
		try
		{
			Log.d(LOG_TAG, "Loop on listOfRootUrl "+MainActivity.listOfRootUrl.size());
			// Now loop of each entry and rewrite or exclude it
			for (int i = 0; i < MainActivity.listOfRootUrl.size(); i++)
			{
				String s=MainActivity.listOfRootUrl.get(i);
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


		// Show text section 2b
		TextView textViewAbout2b = findViewById(R.id.TextAbout02b);
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

			s2b+="<br />";
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

		// Show text section 3
		TextView textViewAbout3 = findViewById(R.id.TextAbout03);
		String s3="";

		// Current url
        String currentUrl = intent.getStringExtra("currentUrl");
        String title = intent.getStringExtra("title");
        if (currentUrl != null && ! "".equals(currentUrl)) {
        	s3+="<font color='#440066'><b>"+getString(R.string.currentUrl)+":</b></font><br /><br />\n"+title+"<br />\n"+currentUrl;
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
    	Log.d(LOG_TAG, "AboutActivity::onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        if (resultCode==RESULT_ABOUT)
        {
			Log.d(LOG_TAG, "AboutActivity::onActivityResult We finish activity resultCode = "+RESULT_ABOUT);
    		setResult(RESULT_ABOUT);
            finish();
        } 
    }

}
