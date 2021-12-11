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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.nltechno.utils.Utils;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Main activity class
 *
 * TargetApi indicates that Lint should treat this type as targeting a given API level, no matter what the project target is.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends Activity implements OnItemSelectedListener {

	private static final String LOG_TAG = "DoliDroidMainActivity";
	public final static String FILENAME = "dolidroid_prefs";		// File will be into
	private final static String HOME_URL = "";
	public static List<String> listOfRootUrl = null;
	public static int nbOfEntries = 0;
	private boolean firstCallToOnStart = Boolean.TRUE;

	private boolean allowChangeText=Boolean.FALSE;

	private Menu savMenu;
	private String menuAre="hardwareonly";

	static final int REQUEST_ABOUT = RESULT_FIRST_USER+0;
	static final int RESULT_ABOUT = RESULT_FIRST_USER+0;

	static final int REQUEST_WEBVIEW = RESULT_FIRST_USER+1;

    final Activity activity = this;


	/**
	 * Called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate Running with SDK="+Build.VERSION.SDK_INT+" hardware menu="+Utils.hasMenuHardware(this));
		super.onCreate(savedInstanceState);

    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
    	Log.d(LOG_TAG, "prefAlwaysShowBar="+prefAlwaysShowBar+" prefAlwaysAutoFill="+prefAlwaysAutoFill);

    	// For main page, we always show bar
    	prefAlwaysShowBar=true;
    	Log.d(LOG_TAG, "prefAlwaysShowBar for main page="+prefAlwaysShowBar);

    	// Define kind of menu we want to use
        boolean hasMenuHardware = Utils.hasMenuHardware(this);
        if (! hasMenuHardware || prefAlwaysShowBar)
        {
        	this.menuAre="actionbar";
        }
        Log.d(LOG_TAG, "hasMenuHardware="+hasMenuHardware+" menuAre="+this.menuAre);

        // menuAre is defined to buttonsbar, actionbar or hardwareonly
        if (this.menuAre.equals("actionbar"))
        {
        	//getActionBar().setHomeButtonEnabled(true);
        	//getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {	// We choose menu using hardware
       		// Hide actionbar without hiding title (no requestFeature(Window.FEATURE_NO_TITLE) because there is no way to restore actionbar after
       		try {
       			ActionBar actionBar = getActionBar();
       			if (actionBar != null) actionBar.hide();
       		}
       		catch(Exception e)
       		{}
        }
        //this.savWindow.requestFeature(Window.FEATURE_PROGRESS);
        //this.savWindow.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);


		setContentView(R.layout.activity_main);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		Log.d(LOG_TAG, "Scree height is "+height);
		if (height < 1100) {
			// We hide the image
			ImageView img1 = (ImageView) findViewById(R.id.imageViewLogoBottom);
			img1.setVisibility(View.INVISIBLE);
		}

        // text2 has links specified by putting <a> tags in the string
        // resource.  By default these links will appear but not
        // respond to user input.  To make them active, you need to
        // call setMovementMethod() on the TextView object.
        TextView t2 = (TextView) findViewById(R.id.textViewLink);
        t2.setMovementMethod(LinkMovementMethod.getInstance());


		// Create listener to respond to click on button
		// Not using the android:onClick tag is bugged.
		// Declaring listener is also faster.
		Button btn = (Button) findViewById(R.id.buttonStart);
		btn.setOnClickListener(new View.OnClickListener()
		{
		    @Override
		    public void onClick(View v) {
		    	try {
		    		openDolUrl(v);
		    	}
		    	catch(IOException ioe)
		    	{
		    		Log.e(LOG_TAG, "Error in openDolUrl");
		    	}
		    }
		});
	}

	/**
	 * Called when activity start
	 */
	@Override
	public void onStart() {
		Log.i(LOG_TAG, "onStart MainActivity");
		super.onStart();

		String homeUrlToSuggest = HOME_URL;
		String homeUrlFirstFound = "";

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    	this.listOfRootUrl = new ArrayList<String>();

		//ArrayAdapter <CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter <CharSequence> adapter = new ArrayAdapter <CharSequence> (this, R.layout.main_spinner_item); // Set style for selected visible value
		adapter.setDropDownViewResource(R.layout.main_spinner_item);	// Set style for dropdown box
		this.nbOfEntries=0;
		try {
			FileInputStream fis = openFileInput(FILENAME);
			Log.d(LOG_TAG, "Open data file "+FILENAME+" in directory "+getApplicationContext().getFilesDir().toString());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				Log.d(LOG_TAG, "Found entry " + this.nbOfEntries + " : " + strLine);
				if (! this.listOfRootUrl.contains(strLine))
				{
					this.nbOfEntries++;
					if (this.nbOfEntries == 1)
					{
						homeUrlFirstFound = strLine;
					}
					this.listOfRootUrl.add(strLine);
				} else {
					Log.d(LOG_TAG, "Duplicate");
				}
			}
			Collections.sort(this.listOfRootUrl);
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			Log.d(LOG_TAG, "Can't read file " + FILENAME + " " + e.getMessage());
		}

		// Loop on this.listOfRootUrl
		if (this.listOfRootUrl.size() == 0 || this.listOfRootUrl.size() > 1) {
			adapter.add(getString(R.string.SelectUrl) + "...");
		}
		else {
			//adapter.add(getString(R.string.enterNewUrl) + "...");
			adapter.add(getString(R.string.SelectUrl) + "...");
		}
		// Set entries to adapter
		for (int i = 0; i < this.listOfRootUrl.size(); i++)
		{
			adapter.add(this.listOfRootUrl.get(i));
		}

		// Show combo list if there is at least 1 choice
		Spinner spinner1 = (Spinner) findViewById(R.id.combo_list_of_urls);
		TextView texViewLink = (TextView) findViewById(R.id.textViewLink);

		if (this.nbOfEntries > 0)
		{
			spinner1.setAdapter(adapter);
			spinner1.setVisibility(View.VISIBLE);
			texViewLink.setVisibility(View.INVISIBLE);

			if (this.nbOfEntries == 1)
			{
				Log.d(LOG_TAG, "Set selection to = "+homeUrlFirstFound);
				// Only one URL known, we autoselect it
				//spinner1.setSelection(1, false);
				homeUrlToSuggest=homeUrlFirstFound;
			}
		}
		else
		{
			spinner1.setVisibility(View.INVISIBLE);
			texViewLink.setVisibility(View.VISIBLE);
		}

		// Init url with hard coded value
		EditText editText1 = (EditText) findViewById(R.id.url_of_instance);
		//editText1.setText(homeUrlToSuggest);

		// If listener was not already added, we add one
		if (firstCallToOnStart) {
			Log.d(LOG_TAG, "First call to onStart");
			spinner1.setOnItemSelectedListener(this);	// Enable handler with onItemSelected and onNothingSelected
			firstCallToOnStart = false;
			editText1.addTextChangedListener(fieldValidatorTextWatcher);
			editText1.setText(homeUrlToSuggest);
		}

		// Init with button disabled
		if (editText1.getText().toString().equals("")) {
			Button startButton = (Button) findViewById(R.id.buttonStart);
			startButton.setEnabled(false);
			startButton.setClickable(false);
			startButton.setTextColor(Color.LTGRAY);
		}

		if (this.savMenu != null)	// Menu may not be initialized yet
		{
	        // Hide menu show bar if there is no hardware, change label otherwise
	    	MenuItem menuItem = this.savMenu.findItem(R.id.always_show_bar);
	    	if (Utils.hasMenuHardware(activity))
	    	{
				menuItem.setVisible(true);
		    	boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
		    	Log.d(LOG_TAG, "prefAlwaysShowBar value is "+prefAlwaysShowBar);
		    	if (prefAlwaysShowBar) {
		    		menuItem.setTitle(getString(R.string.menu_show_bar_on));
				} else {
		    		menuItem.setTitle(getString(R.string.menu_show_bar_off));
				}
	    	}
	    	else
	    	{
	    		menuItem.setVisible(false);
	    	}

	        // Hide menu show bar if phone too old, change label otherwise
			MenuItem menuItem2 = this.savMenu.findItem(R.id.always_autofill);
    		boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
    		Log.d(LOG_TAG, "prefAlwaysAutoFill value is "+prefAlwaysAutoFill);
    		if (prefAlwaysAutoFill) {
    			//menuItem2.setTitle(getString(R.string.menu_autofill_on));
				menuItem2.setChecked(true);
			} else {
    			//menuItem2.setTitle(getString(R.string.menu_autofill_off));
				menuItem2.setChecked(false);
			}

    		if (this.listOfRootUrl != null) {
				MenuItem menuItem3 = this.savMenu.findItem(R.id.clear_all_urls);
				menuItem3.setTitle(getString(R.string.menu_clear_all_urls) + " (" + this.listOfRootUrl.size() + ")");
			}
		}
	}

	/**
	 * Handler to manage change of Url
	 */
	TextWatcher fieldValidatorTextWatcher = new TextWatcher()
	{
		public void afterTextChanged(Editable s) {
        //    Log.d(LOG_TAG, "afterTextChanged s="+s);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //    Log.d(LOG_TAG, "beforeTextChanged s="+s);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
    		Button startButton = (Button) findViewById(R.id.buttonStart);
            Log.d(LOG_TAG, "onTextChanged s="+s);
            if (s.equals("") || "http://".contains(s.toString().toLowerCase(Locale.ENGLISH)) || "https://".contains(s.toString().toLowerCase(Locale.ENGLISH))) {
				startButton.setEnabled(false);
				startButton.setClickable(false);
				startButton.setTextColor(Color.LTGRAY);
			} else {
            	startButton.setEnabled(true);
				startButton.setClickable(true);
				startButton.setTextColor(Color.WHITE);
			}
        }
    };



    /**
     *	Load Smartphone menu
     *
     *	@param	Menu		menu	Object menu to initialize
     *	@return	boolean				true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    	getMenuInflater().inflate(R.menu.activity_main, menu);
    	Log.d(LOG_TAG, "onCreateOptionsMenu");

    	MenuItem menuItem  = menu.findItem(R.id.always_show_bar);

        // Hide menu show bar if there is no hardware
        if (Utils.hasMenuHardware(activity))
        {
			menuItem.setVisible(true);
        	boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
        	Log.d(LOG_TAG, "prefAlwaysShowBar value is "+prefAlwaysShowBar);
        	if (prefAlwaysShowBar) menuItem.setTitle(getString(R.string.menu_show_bar_on));
        	else menuItem.setTitle(getString(R.string.menu_show_bar_off));
        }
        else
        {
        	// When there is no hardware button and not using "actionbar", we remove the 'always show bar' menu
        	menuItem.setVisible(false);
        }

		MenuItem menuItem2 = menu.findItem(R.id.always_autofill);
   		boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
   		Log.d(LOG_TAG, "prefAlwaysAutoFill value is "+prefAlwaysAutoFill);
   		if (prefAlwaysAutoFill) {
   			//menuItem2.setTitle(getString(R.string.menu_autofill_on));
			menuItem2.setChecked(true);
		} else {
   			//menuItem2.setTitle(getString(R.string.menu_autofill_off));
			menuItem2.setChecked(false);
		}


		if (this.listOfRootUrl != null) {
			MenuItem menuItem3 = menu.findItem(R.id.clear_all_urls);
			menuItem3.setTitle(getString(R.string.menu_clear_all_urls) + " (" + MainActivity.listOfRootUrl.size() + ")");
		}


		MenuItem menuItem4 = menu.findItem(R.id.always_uselocalresources);
		if (prefAlwaysAutoFill) {
			//menuItem4.setTitle(getString(R.string.menu_uselocalresources_on));
			menuItem4.setChecked(true);
		} else {
			//menuItem4.setTitle(getString(R.string.menu_uselocalresources_off));
			menuItem4.setChecked(false);
		}

        this.savMenu=menu;

        return true;
    }

    /**
     *	Once we click onto a menu option
     *
     *  @param	MenuItem	item	Menu item selected
     *	@return	boolean				True if we selected a menu managed, False otherwise
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = sharedPrefs.edit();

    	switch (item.getItemId())
    	{
    		case R.id.always_show_bar:
	        	boolean prefAlwaysShowBar = sharedPrefs.getBoolean("prefAlwaysShowBar", true);
	    		Log.d(LOG_TAG, "Click onto switch show bar, prefAlwaysShowBar is "+prefAlwaysShowBar);
	    		prefAlwaysShowBar=!prefAlwaysShowBar;
	        	editor.putBoolean("prefAlwaysShowBar", prefAlwaysShowBar);
	        	editor.apply();
	    		Log.d(LOG_TAG, "Switched value is now "+prefAlwaysShowBar);
	    		// Update men label
	        	if (prefAlwaysShowBar) {
	        		//this.savMenu.findItem(R.id.always_show_bar).setTitle(getString(R.string.menu_show_bar_on));
					this.savMenu.findItem(R.id.always_show_bar).setChecked(true);
				} else {
	        		//this.savMenu.findItem(R.id.always_show_bar).setTitle(getString(R.string.menu_show_bar_off));
					this.savMenu.findItem(R.id.always_show_bar).setChecked(false);
				}
	    		return true;
    		case R.id.always_autofill:
	        	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
	    		Log.d(LOG_TAG, "Click onto switch autofill, prefAlwaysAutoFill is "+prefAlwaysAutoFill);
	    		prefAlwaysAutoFill=!prefAlwaysAutoFill;
	        	editor.putBoolean("prefAlwaysAutoFill", prefAlwaysAutoFill);
	        	editor.commit();
	    		Log.d(LOG_TAG, "Switched value is now "+prefAlwaysAutoFill);
	    		// Update men label
	        	if (prefAlwaysAutoFill) {
	        		//this.savMenu.findItem(R.id.always_autofill).setTitle(getString(R.string.menu_autofill_on));
					this.savMenu.findItem(R.id.always_autofill).setChecked(true);
				} else {
	        		//this.savMenu.findItem(R.id.always_autofill).setTitle(getString(R.string.menu_autofill_off));
					this.savMenu.findItem(R.id.always_autofill).setChecked(false);
				}
	    		return true;
			case R.id.always_uselocalresources:
				boolean prefAlwaysUseLocalResources = sharedPrefs.getBoolean("prefAlwaysUseLocalResources", true);
				Log.d(LOG_TAG, "Click onto switch uselocalresources, prefAlwaysUseLocalResources is "+prefAlwaysUseLocalResources);
				prefAlwaysUseLocalResources=!prefAlwaysUseLocalResources;
				editor.putBoolean("prefAlwaysUseLocalResources", prefAlwaysUseLocalResources);
				editor.commit();
				Log.d(LOG_TAG, "Switched value is now "+prefAlwaysUseLocalResources);
				// Update men label
				if (prefAlwaysUseLocalResources) {
					//this.savMenu.findItem(R.id.always_uselocalresources).setTitle(getString(R.string.menu_uselocalresources_on));
					this.savMenu.findItem(R.id.always_uselocalresources).setChecked(true);
				} else {
					//this.savMenu.findItem(R.id.always_uselocalresources).setTitle(getString(R.string.menu_uselocalresources_off));
					this.savMenu.findItem(R.id.always_uselocalresources).setChecked(false);
				}
				return true;
	    	/*case R.id.add_url:
	    		Log.d(LOG_TAG, "Add predefined URL");
	    		return true;*/
	    	/*case R.id.remove_url:
	    		Log.d(LOG_TAG, "Remove predefined URL");
	    		return true;*/
	    	case R.id.clear_all_urls:
				File file = new File(getApplicationContext().getFilesDir().toString() + "/" + FILENAME);
				Log.d(LOG_TAG, "Clear predefined URL list "+FILENAME+" by deleting file with full path="+file.getAbsolutePath());
	    		Boolean result = file.delete();
				Log.d(LOG_TAG, result.toString());
	    		// Hide combo
	    		Spinner spinner1 = (Spinner) findViewById(R.id.combo_list_of_urls);
	    		spinner1.setVisibility(View.INVISIBLE);
				TextView texViewLink = (TextView) findViewById(R.id.textViewLink);
				texViewLink.setVisibility(View.VISIBLE);
				// Now update menu entry
				this.listOfRootUrl = new ArrayList<String>();	// Clear array of menu entry
				MenuItem menuItem3 = this.savMenu.findItem(R.id.clear_all_urls);
				menuItem3.setTitle(getString(R.string.menu_clear_all_urls) + " (" + this.listOfRootUrl.size() + ")");
	    	    return true;
		    case R.id.about:
	    		Log.d(LOG_TAG, "Click onto Info");
	    		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
	    		Log.d(LOG_TAG, "onOptionsItemSelected startActivityForResult with requestCode="+REQUEST_ABOUT);
	    		startActivityForResult(intent, REQUEST_ABOUT);
	    		return true;
    		case R.id.quit:
    	        Log.d(LOG_TAG, "Click finish");
    	        finish();
    			return true;
    	}

    	Log.w(LOG_TAG, "Click onto unknown button "+item.getItemId());
    	return false;
    }

	/**
	 * Handler to manage event onto the select of combobox
	 */
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
	{
        Log.d(LOG_TAG, "onItemSelected position="+position+" id="+id+" this.allowChangeText="+this.allowChangeText);
		EditText freeUrl = (EditText) findViewById(R.id.url_of_instance);
		Spinner spinnerUrl = (Spinner) findViewById(R.id.combo_list_of_urls);
		String dolRootUrl = (spinnerUrl.getSelectedItem() == null ? "": spinnerUrl.getSelectedItem().toString());
		Button startButton = (Button) findViewById(R.id.buttonStart);

		if (position > 0)
		{
			//startButton.setEnabled(true);
			freeUrl.setText(dolRootUrl);	// If not empty choice
			startButton.setTextColor(Color.WHITE);

			this.allowChangeText=Boolean.FALSE;					// We set a flag because after we will make an action that will call same method
			spinnerUrl.setSelection(0, false);	// This call the onItemSelected. The this.allowChangeText prevent to change the text a second time
		}
		else
		{
			//startButton.setEnabled(false);
			if (this.allowChangeText) {		// We come here because we have selected an entry
				//freeUrl.setText("");
				//startButton.setTextColor(Color.WHITE);
			}
			this.allowChangeText=Boolean.TRUE;
		}
    }

	/**
	 * Handler to manage event onto select combobox
	 */
    public void onNothingSelected(AdapterView<?> parent)
    {
        Log.d(LOG_TAG, "onNothingSelected");
		EditText freeUrl = (EditText) findViewById(R.id.url_of_instance);
		freeUrl.setText("");
		Button startButton = (Button) findViewById(R.id.buttonStart);
		startButton.setEnabled(false);
    }

	/**
	 * openDolUrl
	 *
	 * @param View	button
	 * @throws IOException IOException
	 */
	public void openDolUrl(View button) throws IOException
	{
		// Do click handling here

		final EditText freeUrl = (EditText) findViewById(R.id.url_of_instance);
		String dolRequestUrl = freeUrl.getText().toString();
		String dolRootUrl = freeUrl.getText().toString();
		dolRequestUrl = dolRequestUrl.replace("\\", "/").trim();
		dolRootUrl = dolRootUrl.replace("\\", "/").trim();

		// Add https:// if no http provided
		if (! dolRequestUrl.toLowerCase(Locale.ENGLISH).contains("http://") && ! dolRequestUrl.toLowerCase(Locale.ENGLISH).contains("https://")) {
			dolRequestUrl = "https://".concat(dolRootUrl.replaceAll("^/", ""));
		}
		dolRequestUrl = dolRequestUrl.replaceAll("(?i)/index.php$", "");
		if (! dolRequestUrl.endsWith("/") && ! dolRequestUrl.contains("?") && ! dolRequestUrl.endsWith(".php")) {
			dolRequestUrl = dolRequestUrl.concat("/");
		}

		// Add https:// if no http provided
		if (! dolRootUrl.toLowerCase(Locale.ENGLISH).contains("http://") && ! dolRootUrl.toLowerCase(Locale.ENGLISH).contains("https://")) {
			dolRootUrl = "https://".concat(dolRootUrl.replaceAll("^/", ""));
		}
		dolRootUrl = dolRootUrl.replaceAll("(?i)/index.php$", "");
		if (dolRootUrl.contains("?") || dolRootUrl.endsWith(".php")) {
			String parttoremove = dolRootUrl.replaceAll("http(s|)://([^/]+)/", "");
			dolRootUrl = dolRootUrl.replace(parttoremove, "");
		}
		dolRootUrl = dolRootUrl.replace(":///", "://");
		if (! dolRootUrl.endsWith("/")) {
			dolRootUrl = dolRootUrl.concat("/");
		}

		Log.d(LOG_TAG, "We clicked 'Start' with dolRootUrl=" + dolRootUrl+" dolRequestUrl=" + dolRequestUrl);

		FileOutputStream fos;
		try
		{
			Log.d(LOG_TAG, "Open file " + FILENAME+ " in directory "+getApplicationContext().getFilesDir().toString());

			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			for (int i = 0; i < this.listOfRootUrl.size(); i++)
			{
				String s=this.listOfRootUrl.get(i)+"\n";
				Log.d(LOG_TAG, "write " + s);
				fos.write(s.getBytes());
			}
			if (! this.listOfRootUrl.contains(dolRootUrl))	// Add new value into saved list
			{
				// Add entry into file on disk
				Log.d(LOG_TAG, "write new value " + Utils.bytesToString(dolRootUrl.getBytes()));
				fos.write(dolRootUrl.getBytes());
				// Add entry also into this.listOfRootUrl
				this.listOfRootUrl.add(dolRootUrl);
			}
			fos.close();
		}
		catch(Exception e)
		{
			Log.d(LOG_TAG, "Can't write file " + FILENAME + " " + e.getMessage());
		}

		Intent intent = new Intent(MainActivity.this, SecondActivity.class);
		intent.putExtra("dolRootUrl", dolRootUrl);
		intent.putExtra("dolRequestUrl", dolRequestUrl);
		Log.d(LOG_TAG, "startActivityForResult with requestCode="+REQUEST_WEBVIEW);
		startActivityForResult(intent,REQUEST_WEBVIEW);
	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Log.d(LOG_TAG, "MainActivity::onActivityResult requestCode = "+requestCode + " resultCode = "+resultCode);
        if (requestCode == REQUEST_WEBVIEW && resultCode != RESULT_ABOUT)
        {
			Log.d(LOG_TAG, "MainActivity::onActivityResult We finish activity.");
            finish();
        }
    }


}
