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

import java.util.List;

import com.nltechno.utils.Utils;

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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class AboutActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidActivity";
	private String menuAre="hardwareonly";
	
	static final int REQUEST_ABOUT = 0;
	static final int REQUEST_WEBVIEW = 1;
	static final int RESULT_LOGOUT =  RESULT_FIRST_USER+0;
	static final int RESULT_WEBVIEW =  RESULT_FIRST_USER+1;
	static final int RESULT_ABOUT =  RESULT_FIRST_USER+2;
	
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

        // menuAre is defined to 'buttonsbar', 'actionbar' or 'hardware'
        if (menuAre.equals("actionbar"))
        {
        	
        }
        else {	// Menu are hardware
        	requestWindowFeature(Window.FEATURE_NO_TITLE);	// Hide title with menus
        }

		setContentView(R.layout.activity_about);
	}

	
	/**
	 * Called when activity start
	 * 
	 * @return	void
	 */
	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
		
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	//boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);

        TextView textViewAbout1 = (TextView) findViewById(R.id.TextAbout01);
		String s1="";

		PackageManager manager = this.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

			s1+=getString(R.string.Version)+": <b>"+info.versionName+" (build "+info.versionCode+")</b><br />\n";
			s1+=getString(R.string.VersionStaticResources)+": <b>"+SecondActivity.VERSION_RESOURCES+"</b><br />\n";

			//s+= "PackageName = " + info.packageName + "\n";
			s1+=getString(R.string.Author)+": <b>Laurent Destailleur</b><br />\n";
			s1+=getString(R.string.Web)+": https://www.nltechno.com</b><br />\n";
			s1+=getString(R.string.Compatibility)+": <b>Dolibarr 3.5+</b><br />\n";
			s1+=getString(R.string.License)+": <b>GPL v3+</b><br />\n";
			//s1+=getString(R.string.Sources)+": https://www.nltechno.com/services/<br />\n";
			s1+=getString(R.string.Sources)+": https://framagit.org/eldy/dolidroid.git<br />\n";
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
			s1+=getString(R.string.DeviceHasMenuHardware)+": <b>"+(Utils.hasMenuHardware(this)?getString(R.string.Yes):getString(R.string.No))+"</b><br />\n";
			s1+=getString(R.string.DeviceHasDownloadManager)+": <b>"+(Utils.isDownloadManagerAvailable(this)?getString(R.string.Yes):getString(R.string.No))+"</b><br />\n";

			// This return /storage/sdcard0/Download for example (we use this for downloading files)
			String downloaddirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			// This return /storage/sdcard0 for example (we do not use this)
			//String downloaddir="";
		    //if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) downloaddir = Environment.getExternalStorageDirectory().getAbsolutePath();
			s1+=getString(R.string.DownloadDirectory)+": <b>"+downloaddirpublic+"</b><br />\n";
			
            Intent testIntent = new Intent(Intent.ACTION_VIEW); 
            testIntent.setType("application/pdf"); 
            List<ResolveInfo> list = manager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasPDFViewer)+": <b>"+(list.size() > 0?getString(R.string.Yes)+" ("+list.size()+")":getString(R.string.No))+"</b><br />\n";
            Intent testIntent2 = new Intent(Intent.ACTION_VIEW); 
            testIntent2.setType("application/vnd.oasis.opendocument.text"); 
            List<ResolveInfo> list2 = manager.queryIntentActivities(testIntent2, PackageManager.MATCH_DEFAULT_ONLY); 
			s1+=getString(R.string.DeviceHasODXViewer)+": <b>"+(list2.size() > 0?getString(R.string.Yes)+" ("+list2.size()+")":getString(R.string.No))+"</b><br />\n";
           
			//s+="Permissions = " + info.permissions;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}

        // Show info
		textViewAbout1.setText(Html.fromHtml(s1));

		TextView textViewAbout2 = (TextView) findViewById(R.id.TextAbout02);
		String s2="";

        // DoliDroid.useJMobileAjax is true to force dol_use_jmobile=2, otherwise dol_use_jmobile is 1.
		//s2+=getString(R.string.JMobileMode)+": <b>"+(DoliDroid.useJMobileAjax?"Ajax":"No ajax")+"</b><br />\n";
		//s2+="<br />";

		Intent intent = getIntent();
        String savedDolRootUrl = intent.getStringExtra("savedDolRootUrl");
        if (savedDolRootUrl != null && ! "".equals(savedDolRootUrl))
        {
        	s2+="<font color='#440066'><b>"+getString(R.string.savedDolUrlRoot)+":</b></font><br />\n"+savedDolRootUrl+"<br />\n";
        
	        // Saved user/pass
	        String username=null;
	        String password=null;
	        Boolean tagToOverwriteLoginPass = prefAlwaysAutoFill;
			if (tagToOverwriteLoginPass)	// If we are allowed to overwrite username/pass into fields
			{
		    	//SharedPreferences sharedPrefs = this.secondActivity.getSharedPreferences(FILENAME_INST_PARAM, Context.MODE_PRIVATE);
				username=sharedPrefs.getString(savedDolRootUrl+"-username", "");
				password=sharedPrefs.getString(savedDolRootUrl+"-password", "");
			}
	
	        s2+=getString(R.string.SavedLogin)+": "+(username != null ? username : "")+"<br />\n";
	        s2+=getString(R.string.SavedPassword)+": "+(password != null ? password.replaceAll(".", "*") : "")+"<br />\n";
	                
	        // Basic auth user/pass used ? 
	        String savedAuthuser = intent.getStringExtra("savedAuthuser");
	        if (savedAuthuser != null) s2+="<br />"+getString(R.string.BasicAuthLogin)+": "+savedAuthuser+"<br />";
	        String savedAuthpass = intent.getStringExtra("savedAuthpass");
	        if (savedAuthpass != null) s2+=getString(R.string.BasicAuthPassword)+": "+savedAuthpass.replaceAll(".", "*")+"<br />\n";
        }

		// Current url
        String currentUrl = intent.getStringExtra("currentUrl");
        String title = intent.getStringExtra("title");
        if (currentUrl != null && ! "".equals(currentUrl)) s2+="<br /><br />\n<font color='#440066'><b>"+getString(R.string.currentUrl)+":</b></font><br />\n"+title+"<br />\n"+currentUrl;
		String lastversionfound = intent.getStringExtra("lastversionfound");
        if (lastversionfound != null && ! "".equals(lastversionfound)) s2+="<br />\nDolibarr "+getString(R.string.Version)+": "+lastversionfound+"<br />\n";

		// User agent
        String userAgent = intent.getStringExtra("userAgent");
        if (userAgent != null && ! "".equals(userAgent)) s2+="<br /><br />\n<font color='#440066'><b>\n"+getString(R.string.UserAgent)+":</b></font><br />\n"+userAgent;

		textViewAbout2.setText(Html.fromHtml(s2));
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
	    		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_LOGOUT);
	    		setResult(RESULT_LOGOUT);
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
		    	Log.d(LOG_TAG, "We clicked onto KEYCODE_MENU or KEYCODE_BACK. We finish activity resultCode = "+RESULT_LOGOUT);
	    		setResult(RESULT_LOGOUT);
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
		Log.d(LOG_TAG, "We finish activity resultCode = "+RESULT_LOGOUT);
		setResult(RESULT_LOGOUT);
    	finish();
    } 
    
	/**
	 * onActivityResult
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.d(LOG_TAG, "AboutActivity::onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        if (resultCode==RESULT_LOGOUT)
        {
			Log.d(LOG_TAG, "AboutActivity::onActivityResult We finish activity resultCode = "+RESULT_LOGOUT);
    		setResult(RESULT_LOGOUT);
            finish();
        } 
    }

}
