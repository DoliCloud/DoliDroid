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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Objects;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
public class AboutActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidLogAboutActivity";

	static final int RESULT_ABOUT =  RESULT_FIRST_USER;

	private String nameOfSourceStore = "Unknown";

	/**
	 * Called when activity is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate savedInstanceState="+savedInstanceState);
		super.onCreate(savedInstanceState);

		//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
    	Log.d(LOG_TAG, "prefAlwaysAutoFill="+prefAlwaysAutoFill);

    	// Define kind of menu we want to use
        boolean hasMenuHardware = Utils.hasMenuHardware(this);

        Log.d(LOG_TAG, "hasMenuHardware="+hasMenuHardware);

		setContentView(R.layout.activity_about);

		// text2 has links specified by putting <a> tags in the string
		// resource.  By default, these links will appear but not
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
		
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

		Intent intent = getIntent();

		// Set image
		try {
			ImageView imageView = findViewById(R.id.imageView01);

			// Load image from assets
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

		PackageManager packageManager = this.getPackageManager();
		try
		{
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);

			String installerPackageName = packageManager.getInstallerPackageName(this.getPackageName());
			if ("com.android.vending".equals(installerPackageName)) {
				nameOfSourceStore = "Android PlayStore";
			} else if (installerPackageName != null)   {
				nameOfSourceStore = installerPackageName;
			}

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //    sVersion+="<b>"+info.versionName+" (build "+info.getLongVersionCode()+")</b>";
            //} else {
			sVersion+="<b>DoliDroid "+info.versionName+"</b>";
			//}

            //s+= "PackageName = " + info.packageName + "\n";
			s1+=getString(R.string.Web)+"<br /><span style=\"color:#008888\"><a href=\"https://www.dolicloud.com?origin=dolidroid&amp;utm_source=dolidroid&amp;utm_campaign=none&amp;utm_medium=mobile\">https://www.dolicloud.com</a></span><br />\n";

			s1 += "<br />";

			s1+=getString(R.string.Sources)+"<br /><span style=\"color:#008888\"><a href=\"https://github.com/DoliCloud/DoliDroid.git\">https://github.com/DoliCloud/DoliDroid.git</a></span><br />\n";

			s1 += "<br />";

			s1+=getString(R.string.License)+"<br /><b>GPL v3+</b><br />\n";

			s1 += "<br />";

			s1+=getString(R.string.Author)+"<br /><span style=\"color:#008888\"><a href=\"https://www.github.com/eldy\">Laurent Destailleur</a>, ...</span><br />\n";

			s1 += "<br />";

			s1+=getString(R.string.PrivacyPolicy)+"<br /><span style=\"color:#008888\"><a href=\"https://www.dolicloud.com/en-dolidroid-privacy-policy.php\">https://www.dolicloud.com/en-dolidroid-privacy-policy.php</a></span><br />\n";

			s1 += "<br />\n";
			s1 += "<br />";
			s1 += "<br />";

			// This download key allow to download file with name src_dolidroid-info.versionName-downloadkey
			//String downloadkey=Utils.MD5Hex("dolidroid"+info.versionName.replaceAll("[^0-9.]", "")+"saltnltechno").substring(0, 8);
			//s1+=getString(R.string.Sources)+" Download Key: dolidroid-"+info.versionName.replaceAll("[^0-9.]", "")+"-"+downloadkey+"<br />\n";
			s1+=getString(R.string.Compatibility)+"<br /><b>Dolibarr 8+</b><br />\n";

			s1 += "<br />";

			s1+=getString(R.string.VersionStaticResources)+"<br /><b>"+SecondActivity.VERSION_RESOURCES+"</b><br />\n";

			s1 += "<br />";

			String targetSdkVersion = String.valueOf(getApplicationContext().getApplicationInfo().targetSdkVersion);
			s1+=getString(R.string.TargetSDKVersion)+"<br /><b>"+targetSdkVersion+"</b><br />\n";

			s1+="<br />\n";

			s1+=getString(R.string.NameOfSourceStore)+"<br /><b>"+nameOfSourceStore+"</b><br />\n";

			s1+="<br />\n";
			s1 += "<br />";
			s1 += "<br />";


			s1+=getString(R.string.DeviceAPILevel)+"<br /><b>"+Build.VERSION.SDK_INT+"</b><br />\n";

			s1 += "<br />";

			// Get display size
            Display display;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display = getDisplay();
            } else {
				display = getWindowManager().getDefaultDisplay();
			}

			int width = 0;
			int height = 0;
            if (display != null) {
				Point size = new Point();
                display.getSize(size);
				width = size.x;
				height = size.y;
            }
			s1+=getString(R.string.DeviceSize)+"<br /><b>"+width+"x"+height+"</b><br />";

			s1 += "<br />";

			s1+=getString(R.string.DeviceHasDownloadManager)+"<br /><b>"+(Utils.isDownloadManagerAvailable(this)?getString(R.string.Yes):getString(R.string.No))+"</b><br />\n";

			s1 += "<br />";

			// File[] Files = getExternalMediaDirs();
			// Files[0].getAbsolutePath will return "/storage/emulated/0/Android/media/com.nltechno.dolidroidpro"
			// From Android 30+, it is better to write into media dire with MediaStore

			// This return /storage/sdcard0/Download for example (we use this for downloading files)
			String publicDownloadDirectory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			// This return /storage/sdcard0 for example (we do not use this)
			//String downloaddir="";
			s1+=getString(R.string.DownloadDirectory)+"<br /><b>"+publicDownloadDirectory+"</b><br />\n";

			s1 += "<br />";

			//String publicPhotosDirPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			String publicPhotosDirPath=getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
			s1+=getString(R.string.PhotosDirectory)+"<br /><b>"+publicPhotosDirPath+"</b><br />\n";

			s1 += "<br />";

			//String documentdirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
			//String documentdirpublic=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
			//s1+=getString(R.string.DocumentsDirectory)+": <b>"+documentdirpublic+"</b><br />\n";

			/*
			Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType("application/pdf"); 
            List<ResolveInfo> list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
			s1+=getString(R.string.DeviceHasPDFViewer)+": <b>"+(list.size() > 0?getString(R.string.Yes)+" ("+list.size()+")":getString(R.string.No))+"</b><br />\n";

			Intent testIntent2 = new Intent(Intent.ACTION_VIEW);
            testIntent2.setType("application/vnd.oasis.opendocument.text"); 
            List<ResolveInfo> list2 = packageManager.queryIntentActivities(testIntent2, PackageManager.MATCH_DEFAULT_ONLY);
			s1+=getString(R.string.DeviceHasODXViewer)+": <b>"+(list2.size() > 0?getString(R.string.Yes)+" ("+list2.size()+")":getString(R.string.No))+"</b>\n";
           	*/


			/* Detect the application associated with "mailto:" and "tel" links */

			Intent intentTmp2 = new Intent(Intent.ACTION_DIAL);
			intentTmp2.setData(Uri.parse("tel:"));

			List<ResolveInfo> resolveInfoList2 = packageManager.queryIntentActivities(intentTmp2, PackageManager.MATCH_DEFAULT_ONLY);
			StringBuilder appNames2 = new StringBuilder();
			for (ResolveInfo resolveInfo2 : resolveInfoList2) {
				CharSequence appName2 = resolveInfo2.loadLabel(packageManager);
				if (! "".equals(appName2.toString())) {
					appNames2.append(appName2).append(" ");
				}
			}
			s1+=getString(R.string.ApplicationAssociatedWithTelLink)+"<br /><b>"+(resolveInfoList2.isEmpty() ? getString(R.string.None) : appNames2.toString())+"</b><br />\n";

			s1 += "<br />";

			/*
			ResolveInfo resolveInfo2 = packageManager.resolveActivity(intentTmp2, PackageManager.MATCH_DEFAULT_ONLY);
			CharSequence appName2 = "";
			if (resolveInfo2 != null) {
				appName2 = resolveInfo2.loadLabel(packageManager);
			}
			s1+=getString(R.string.ApplicationAssociatedWithTelLink)+": <b>"+(resolveInfo2 == null ? getString(R.string.None) : appName2)+"</b><br />\n";
			*/


			Intent intentTmp = new Intent(Intent.ACTION_SENDTO);
			intentTmp.setData(Uri.parse("mailto:"));

			List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intentTmp, PackageManager.MATCH_DEFAULT_ONLY);
			StringBuilder appNames = new StringBuilder();
			for (ResolveInfo resolveInfo : resolveInfoList) {
				CharSequence appName = resolveInfo.loadLabel(packageManager);
				if (! "".equals(appName.toString())) {
					appNames.append(appName).append(" ");
				}
			}
			s1+=getString(R.string.ApplicationAssociatedWithMailToLink)+"<br /><b>"+(resolveInfoList.isEmpty() ? getString(R.string.None) : appNames.toString())+"</b><br />\n";

			s1 += "<br />";

			/*ResolveInfo resolveInfo = packageManager.resolveActivity(intentTmp, PackageManager.MATCH_DEFAULT_ONLY);
			CharSequence appName = "";
			if (resolveInfo != null) {
				appName = resolveInfo.loadLabel(packageManager);
			}
			s1+=getString(R.string.ApplicationAssociatedWithMailToLink)+": <b>"+(resolveInfo == null ? getString(R.string.None) : appName)+"</b><br />\n";
			*/


			//s+="Permissions = " + info.permissions;
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, Objects.requireNonNull(e.getMessage()));
		}

		textViewAboutVersion.setText(Html.fromHtml(sVersion, Html.FROM_HTML_MODE_LEGACY));

		textViewAbout2.setText(Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT));
		textViewAbout2.setMovementMethod(LinkMovementMethod.getInstance());

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

        if (item.getItemId() == R.id.menu_back) {
            Log.d(LOG_TAG, "We finish activity resultCode = " + RESULT_ABOUT);
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
