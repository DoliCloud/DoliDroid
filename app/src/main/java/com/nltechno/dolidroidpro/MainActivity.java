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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.nltechno.utils.Utils;

import android.content.RestrictionsManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


/**
 * Main activity class
 * TargetApi indicates that Lint should treat this type as targeting a given API level, no matter what the project target is.
 */
public class MainActivity extends Activity implements OnItemSelectedListener {

	private static final String LOG_TAG = "DoliDroidLogMainActivity";
	public final static String FILENAME = "dolidroid_prefs";		// File will be into
	private final static String HOME_URL = "";
	public static List<PredefinedUrl> listOfRootUrl = null;
	public static int nbOfEntries = 0;
	private boolean firstCallToOnStart = Boolean.TRUE;

	private boolean allowChangeText=Boolean.FALSE;

	private Menu savMenu;

	static final int REQUEST_ABOUT = RESULT_FIRST_USER;
	static final int RESULT_ABOUT = RESULT_FIRST_USER;

	static final int REQUEST_WEBVIEW = RESULT_FIRST_USER+1;


	/**
	 * Called when activity is created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate Running with SDK="+Build.VERSION.SDK_INT+" hardware menu="+Utils.hasMenuHardware(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

    	//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

    	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
    	Log.d(LOG_TAG, "prefAlwaysAutoFill="+prefAlwaysAutoFill);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//int width = size.x;
		int height = size.y;
		Log.d(LOG_TAG, "Screen height is "+height);
		if (height < 1100) {
			// We hide the image
			//ImageView img1 = findViewById(R.id.imageViewLogoBottom);
			//img1.setVisibility(View.INVISIBLE);
		}

        // text2 has links specified by putting <a> tags in the string
        // resource.  By default these links will appear but not
        // respond to user input.  To make them active, you need to
        // call setMovementMethod() on the TextView object.
        TextView t2 = findViewById(R.id.textViewLink);
        t2.setMovementMethod(LinkMovementMethod.getInstance());


		// Create listener to respond to click on button
		// Not using the android:onClick tag is bugged.
		// Declaring listener is also faster.
		Button btn = findViewById(R.id.buttonStart);
		btn.setOnClickListener(new View.OnClickListener() {
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

		//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

		// The array to contains the list of all predefined URLs
		// This list is saved into a file named FILENAME
    	listOfRootUrl = new ArrayList<>();

		//ArrayAdapter <CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter <CharSequence> adapter = new ArrayAdapter <> (this, R.layout.select_url_item); // Set style for selected visible value
		// Set style for dropdown box (the font size of for the combo box of pre-defined URLs)
		adapter.setDropDownViewResource(R.layout.select_url_item);

		nbOfEntries=0;
		try {
			FileInputStream fis = openFileInput(FILENAME);
			Log.d(LOG_TAG, "Open the data file for Urls ("+FILENAME+") in directory "+getApplicationContext().getFilesDir().toString());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				Log.d(LOG_TAG, "Found entry " + nbOfEntries + " : " + strLine);
				// Check if entry already present
				int count = 0;
				boolean entryfound = false;
				while (count < listOfRootUrl.size()) {
					if (strLine.equals(listOfRootUrl.get(count).url)) {
						entryfound = true;
						break;
					}
					count++;
				}

				if (! entryfound) {
					nbOfEntries++;
					if (nbOfEntries == 1)
					{
						homeUrlFirstFound = strLine;
					}

					// Add new entry into the array this.listOfRootUrl
					PredefinedUrl tmppredefinedurl = new PredefinedUrl();
					tmppredefinedurl.url = strLine;
					listOfRootUrl.add(tmppredefinedurl);
				} else {
					Log.d(LOG_TAG, "Duplicate");
				}
			}

			// Sort the array list of URL
			Collections.sort(listOfRootUrl, Comparator.comparing(PredefinedUrl::getSortOrder));

			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			Log.d(LOG_TAG, "Can't read file " + FILENAME + " " + e.getMessage());
		}

		// Loop on this.listOfRootUrl
		//if (this.listOfRootUrl.size() != 1) {
		//	adapter.add(getString(R.string.SelectUrl) + "...");
		//} else {
		adapter.add(getString(R.string.SelectUrl) + "...");
		//}

		// Set entries to the adapter
		for (int i = 0; i < listOfRootUrl.size(); i++) {
			String line1 = listOfRootUrl.get(i).getDomainUrl().replaceAll("\\\\/+$", "");
			String line2 = listOfRootUrl.get(i).getScheme();
			line2 += "://";
			if (! "".equals(listOfRootUrl.get(i).getBasicAuthLogin())) {
				line2 += " - "+listOfRootUrl.get(i).getBasicAuthLogin();
				//tmps += ":"+this.listOfRootUrl.get(i).getBasicAuthPass();
				line2 += ":*****";
			}

			SpannableString spannableLine2 = new SpannableString(line2);
			spannableLine2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, line2.length(), 0);

			adapter.add(spannableLine2 + "\n" + line1);
		}

		// Show combo list if there is at least 1 choice
		Spinner spinner_for_list_of_predefined_entries = findViewById(R.id.combo_list_of_urls);
		TextView texViewLink = findViewById(R.id.textViewLink);

		if (nbOfEntries > 0) {
			spinner_for_list_of_predefined_entries.setAdapter(adapter);
			spinner_for_list_of_predefined_entries.setVisibility(View.VISIBLE);
			texViewLink.setVisibility(View.INVISIBLE);

			if (nbOfEntries == 1) {
				Log.d(LOG_TAG, "Set selection to = "+homeUrlFirstFound);
				// Only one URL known, we autoselect it
				//spinner_for_list_of_predefined_entries.setSelection(1, false);
				homeUrlToSuggest=homeUrlFirstFound;
			}
		} else {
			spinner_for_list_of_predefined_entries.setVisibility(View.INVISIBLE);
			texViewLink.setVisibility(View.VISIBLE);
		}

		// Check if a default URL exists as a MDM Managed Configuration
		RestrictionsManager myManagedConfigurationMgr = (RestrictionsManager) getSystemService(Context.RESTRICTIONS_SERVICE);
		Bundle myManagedConfiguration = myManagedConfigurationMgr.getApplicationRestrictions();
		if (myManagedConfiguration.containsKey("managedConfigurationDefaultURL") && myManagedConfiguration.getString("managedConfigurationDefaultURL") != null && !"".equals(myManagedConfiguration.getString("managedConfigurationDefaultURL"))) {
			homeUrlToSuggest = myManagedConfiguration.getString("managedConfigurationDefaultURL");
			Log.d(LOG_TAG, "A managed configuration was found with value: " + homeUrlToSuggest);
		}

		// Init url with hard coded value
		EditText editText1 = findViewById(R.id.url_of_instance);
		//editText1.setText(homeUrlToSuggest);

		// If listener was not already added, we add one
		if (firstCallToOnStart) {
			Log.d(LOG_TAG, "First call to onStart");
			spinner_for_list_of_predefined_entries.setOnItemSelectedListener(this);	// Enable handler with onItemSelected and onNothingSelected
			firstCallToOnStart = false;
			editText1.addTextChangedListener(fieldValidatorTextWatcher);
			editText1.setText(homeUrlToSuggest);
		}

		// Init with button disabled
		if (editText1.getText().toString().isEmpty()) {
			Button startButton = findViewById(R.id.buttonStart);
			startButton.setEnabled(false);
			startButton.setClickable(false);
			startButton.setTextColor(Color.LTGRAY);
		}

		if (this.savMenu != null) {	// If menu has been initialized, we can fill/modify it.
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

			MenuItem menuItem4 = this.savMenu.findItem(R.id.always_uselocalresources);
			boolean prefAlwaysUseLocalResources = sharedPrefs.getBoolean("prefAlwaysUseLocalResources", true);
			Log.d(LOG_TAG, "prefAlwaysUseLocalResources value is "+prefAlwaysUseLocalResources);
			if (prefAlwaysUseLocalResources) {
				//menuItem4.setTitle(getString(R.string.menu_autofill_on));
				menuItem4.setChecked(true);
			} else {
				//menuItem4.setTitle(getString(R.string.menu_autofill_off));
				menuItem4.setChecked(false);
			}

    		if (listOfRootUrl != null) {
				MenuItem tmpItem = this.savMenu.findItem(R.id.manage_all_urls);
				tmpItem.setTitle(getString(R.string.menu_manage_all_urls) + " (" + listOfRootUrl.size() + ")");
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
    		Button startButton = findViewById(R.id.buttonStart);
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
    	//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

    	getMenuInflater().inflate(R.menu.activity_main, menu);
    	Log.d(LOG_TAG, "onCreateOptionsMenu");

		MenuItem menuItem2 = menu.findItem(R.id.always_autofill);
   		boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);
   		Log.d(LOG_TAG, "prefAlwaysAutoFill value is "+prefAlwaysAutoFill);
		if (menuItem2 != null) {
			if (prefAlwaysAutoFill) {
				//menuItem2.setTitle(getString(R.string.menu_autofill_on));
				menuItem2.setChecked(true);
			} else {
				//menuItem2.setTitle(getString(R.string.menu_autofill_off));
				menuItem2.setChecked(false);
			}
		}

		if (listOfRootUrl != null) {
			MenuItem tmpItem = menu.findItem(R.id.manage_all_urls);
			if (tmpItem != null) {
				tmpItem.setTitle(getString(R.string.menu_manage_all_urls) + " (" + MainActivity.listOfRootUrl.size() + ")");
			}
		}

		MenuItem menuItem4 = menu.findItem(R.id.always_uselocalresources);
		boolean prefAlwaysUseLocalResources = sharedPrefs.getBoolean("prefAlwaysUseLocalResources", true);
		Log.d(LOG_TAG, "prefAlwaysUseLocalResources value is "+prefAlwaysUseLocalResources);
		if (menuItem4 != null) {
			if (prefAlwaysUseLocalResources) {
				//menuItem4.setTitle(getString(R.string.menu_uselocalresources_on));
				menuItem4.setChecked(true);
			} else {
				//menuItem4.setTitle(getString(R.string.menu_uselocalresources_off));
				menuItem4.setChecked(false);
			}
		}

		MenuItem menuItemQuit = menu.findItem(R.id.quit);
		if (menuItemQuit != null) {
			menuItemQuit.setIcon(getDrawable(R.drawable.ic_baseline_exit_to_app_24));
		}

		MenuItem menuItemAbout = menu.findItem(R.id.about);
		if (menuItemAbout != null) {
			//menuItemAbout.setIcon(getDrawable(R.drawable.ic_baseline_help_outline_24));
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
    	//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

		Editor editor = sharedPrefs.edit();

    	switch (item.getItemId())
    	{
    		case R.id.always_autofill:
				// Same code into MainActivity and SecondActivity
	        	boolean prefAlwaysAutoFill = sharedPrefs.getBoolean("prefAlwaysAutoFill", true);

	    		Log.i(LOG_TAG, "Click onto switch autofill, prefAlwaysAutoFill is "+prefAlwaysAutoFill);
	    		prefAlwaysAutoFill=!prefAlwaysAutoFill;

	        	editor.putBoolean("prefAlwaysAutoFill", prefAlwaysAutoFill);
	        	editor.apply();

	    		Log.d(LOG_TAG, "Switched value is now "+prefAlwaysAutoFill);
	    		// Update men label
	        	if (prefAlwaysAutoFill) {
	        		//this.savMenu.findItem(R.id.always_autofill).setTitle(getString(R.string.menu_autofill_on));
					this.savMenu.findItem(R.id.always_autofill).setChecked(true);
				} else {
	        		//this.savMenu.findItem(R.id.always_autofill).setTitle(getString(R.string.menu_autofill_off));
					this.savMenu.findItem(R.id.always_autofill).setChecked(false);

					// Clear saved login / pass
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
						Editor editorEncrypted = sharedPrefsEncrypted.edit();
						editorEncrypted.clear();
						editorEncrypted.apply();

						Log.d(LOG_TAG, "The encrypted shared preferences file has been cleared");
					}
					catch(Exception e) {
						Log.w(LOG_TAG, "Failed to clear encrypted shared preferences file, we try by deleting the file");
						File prefsFile = new File(getApplicationContext().getFilesDir(), "../shared_prefs/secret_shared_prefs.xml");
						if (prefsFile.exists()) {
							Log.d(LOG_TAG, "File "+prefsFile+" exists, we delete it");
							try {
								boolean deleted = prefsFile.delete();
								Log.d(LOG_TAG, "File " + prefsFile + " deleted = " + deleted);
							} catch(Exception e2) {
								// Keep empty
								Log.d(LOG_TAG, "Failed to delete file " + prefsFile);
							}
						}

					}
				}
				invalidateOptionsMenu();
	    		return true;
			case R.id.always_uselocalresources:
				boolean prefAlwaysUseLocalResources = sharedPrefs.getBoolean("prefAlwaysUseLocalResources", true);
				Log.d(LOG_TAG, "Click onto switch uselocalresources, prefAlwaysUseLocalResources is "+prefAlwaysUseLocalResources);
				prefAlwaysUseLocalResources=!prefAlwaysUseLocalResources;
				editor.putBoolean("prefAlwaysUseLocalResources", prefAlwaysUseLocalResources);
				editor.commit();
				Log.d(LOG_TAG, "Switched value is now "+prefAlwaysUseLocalResources);
				// Update men label
				this.savMenu.findItem(R.id.always_uselocalresources).setChecked(prefAlwaysUseLocalResources);
				invalidateOptionsMenu();
				return true;
			case R.id.manage_all_urls:
				Log.d(LOG_TAG, "Click onto Manage all URLs");
				Intent tmpintent = new Intent(MainActivity.this, ManageURLActivity.class);
				Log.d(LOG_TAG, "onOptionsItemSelected startActivityForResult with requestCode="+REQUEST_ABOUT);
				startActivityForResult(tmpintent, REQUEST_ABOUT);
				return true;
			case R.id.about:
	    		Log.d(LOG_TAG, "Click onto Info");
	    		Intent tmpintent2 = new Intent(MainActivity.this, AboutActivity.class);
	    		Log.d(LOG_TAG, "onOptionsItemSelected startActivityForResult with requestCode="+REQUEST_ABOUT);
	    		startActivityForResult(tmpintent2, REQUEST_ABOUT);
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
		EditText freeUrl = findViewById(R.id.url_of_instance);
		Spinner spinnerUrl = findViewById(R.id.combo_list_of_urls);
		Button startButton = findViewById(R.id.buttonStart);

		if (position > 0) {
			//startButton.setEnabled(true);

			// Get full URL selected
			String dolRootUrl = listOfRootUrl.get(position - 1).url;
			//String dolRootUrl = (spinnerUrl.getSelectedItem() == null ? "": spinnerUrl.getSelectedItem().toString());

			freeUrl.setText(dolRootUrl);	// If not empty choice
			startButton.setTextColor(Color.WHITE);

			this.allowChangeText = Boolean.FALSE;					// We set a flag because after we will make an action that will call same method
			spinnerUrl.setSelection(0, false);	// This re-call the onItemSelected. The this.allowChangeText prevent to change the text a second time
		}
		else
		{
			//startButton.setEnabled(false);
			if (this.allowChangeText) {		// We come here because we have selected an entry
				//freeUrl.setText("");
				//startButton.setTextColor(Color.WHITE);
			}
			this.allowChangeText = Boolean.TRUE;
		}
    }

	/**
	 * Handler to manage event onto select combobox
	 */
    public void onNothingSelected(AdapterView<?> parent)
    {
        Log.d(LOG_TAG, "onNothingSelected");
		EditText freeUrl = findViewById(R.id.url_of_instance);
		freeUrl.setText("");
		Button startButton = findViewById(R.id.buttonStart);
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

		final EditText freeUrl = findViewById(R.id.url_of_instance);
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
		dolRootUrl = dolRootUrl.replaceAll(" ", "");
		dolRootUrl = dolRootUrl.replace(":///", "://");
		if (! dolRootUrl.endsWith("/")) {
			dolRootUrl = dolRootUrl.concat("/");
		}

		Log.d(LOG_TAG, "We clicked 'Start' with dolRootUrl=" + dolRootUrl+" dolRequestUrl=" + dolRequestUrl);

		FileOutputStream fos;
		try
		{
			Log.d(LOG_TAG, "Open file " + MainActivity.FILENAME+ " in directory "+getApplicationContext().getFilesDir().toString());

			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			for (int i = 0; i < listOfRootUrl.size(); i++)
			{
				String s = listOfRootUrl.get(i).url+"\n";
				Log.d(LOG_TAG, "write " + s);
				fos.write(s.getBytes());
			}

			// Check if entry already present
			int count = 0;
			boolean entryfound = false;
			while (count < listOfRootUrl.size()) {
				if (dolRootUrl.equals(listOfRootUrl.get(count).url)) {
					entryfound = true;
					break;
				}
				count++;
			}

			if (! entryfound)	// Add new value into saved list
			{
				// Add entry into file on disk
				Log.d(LOG_TAG, "write new value " + Utils.bytesToString(dolRootUrl.getBytes()));
				fos.write(dolRootUrl.getBytes());

				// Add new entry into the array this.listOfRootUrl
				PredefinedUrl tmppredefinedurl = new PredefinedUrl();
				tmppredefinedurl.url = dolRootUrl;
				tmppredefinedurl.position = 100;
				listOfRootUrl.add(tmppredefinedurl);
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
