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

import com.nltechno.utils.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;


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
		TextView t1 = findViewById(R.id.textAboutVersion);
		t1.setMovementMethod(LinkMovementMethod.getInstance());

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

		// Set image
		try {
			ImageView imageView = findViewById(R.id.imageView01);

			// Charger l'image depuis les assets
			InputStream inputStream = getAssets().open("screenshot_dolidroid.png");
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			imageView.setImageBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Show text section 1
		TextView textViewAboutVersion = findViewById(R.id.textAboutVersion);
		TextView textViewAbout2 = findViewById(R.id.textAboutVersion2);
		String sVersion="";
		String s1="";

		PackageManager manager = this.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

			sVersion+="<b>"+info.versionName+" (build "+info.versionCode+")</b>";

			s1+=getString(R.string.VersionStaticResources)+": <b>"+SecondActivity.VERSION_RESOURCES+"</b><br />\n";

			//s+= "PackageName = " + info.packageName + "\n";
			s1+=getString(R.string.Author)+": <span style=\"color:#008888\"><a href=\"https://www.github.com/eldy\">Laurent Destailleur</a></span><br />\n";
			s1+=getString(R.string.Web)+": <span style=\"color:#008888\"><a href=\"https://www.dolicloud.com?origin=dolidroid&amp;utm_source=dolidroid&amp;utm_campaign=none&amp;utm_medium=mobile\">https://www.dolicloud.com</a></span><br />\n";
			s1+=getString(R.string.Compatibility)+": <b>Dolibarr 8+</b><br />\n";
			s1+=getString(R.string.License)+": <b>GPL v3+</b><br />\n";
			//s1+=getString(R.string.Sources)+": https://www.nltechno.com/services/<br />\n";
			s1+=getString(R.string.Sources)+": <span style=\"color:#008888\"><a href=\"https://github.com/DoliCloud/DoliDroid.git\">https://github.com/DoliCloud/DoliDroid.git</a></span><br />\n";
			s1+=getString(R.string.PrivacyPolicy)+": <span style=\"color:#008888\"><a href=\"https://www.dolicloud.com/en-dolidroid-privacy-policy.php\">https://www.dolicloud.com/en-dolidroid-privacy-policy.php</a></span><br />\n";
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

			// For  Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_PODCASTS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_PICTURES, or Environment.DIRECTORY_MOVIES.
			// Files in this directory are deleted when application is deleted.
			//File[] Files = getExternalFilesDirs();

			// File[] Files = getExternalMediaDirs();
			// Files[0].getAbsolutePath will return "/storage/emulated/0/Android/media/com.nltechno.dolidroidpro"
			// From Android 30+, it is better to write into media dire with MediaStore

			// This return /storage/sdcard0/Download for example (we use this for downloading files)
			String downloaddirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			// This return /storage/sdcard0 for example (we do not use this)
			//String downloaddir="";
		    //if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) downloaddir = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.DownloadDirectory)+": <b>"+downloaddirpublic+"</b><br />\n";

			String photosdirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			//if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) photosdirpublic = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.PhotosDirectory)+": <b>"+photosdirpublic+"</b><br />\n";

			String documentdirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
			//if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) documentdirpublic = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.DocumentsDirectory)+": <b>"+documentdirpublic+"</b><br />\n";

			/*
			Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType("application/pdf"); 
            List<ResolveInfo> list = manager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasPDFViewer)+": <b>"+(list.size() > 0?getString(R.string.Yes)+" ("+list.size()+")":getString(R.string.No))+"</b><br />\n";

			Intent testIntent2 = new Intent(Intent.ACTION_VIEW);
            testIntent2.setType("application/vnd.oasis.opendocument.text"); 
            List<ResolveInfo> list2 = manager.queryIntentActivities(testIntent2, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasODXViewer)+": <b>"+(list2.size() > 0?getString(R.string.Yes)+" ("+list2.size()+")":getString(R.string.No))+"</b>\n";
           	*/

			//s+="Permissions = " + info.permissions;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}

		textViewAboutVersion.setText(Html.fromHtml(sVersion, Html.FROM_HTML_MODE_LEGACY));

		textViewAbout2.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_LEGACY));
		// For api level 24: textViewAbout1.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_LEGACY));

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
        Log.d(LOG_TAG, "Click onto menu "+item.toString() + " from AboutActivity");

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
