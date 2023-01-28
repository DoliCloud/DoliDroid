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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.nltechno.utils.Utils;


/**
 * Main activity class
 *
 * TargetApi indicates that Lint should treat this type as targeting a given API level, no matter what the project target is.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest extends Activity implements OnItemSelectedListener {

	private static final String LOG_TAG = "DoliDroidMainActivityTest";

	final Activity activity = this;


	/**
	 * Called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate Running with SDK=" + Build.VERSION.SDK_INT + " hardware menu=" + Utils.hasMenuHardware(this));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maintest);

	}

	/**
	 * Called when activity start
	 */
	@Override
	public void onStart() {
		Log.i(LOG_TAG, "onStart MainActivity");
		super.onStart();

	}


	/**
	 * Load Smartphone menu
	 *
	 * @param    Menu        menu	Object menu to initialize
	 * @return boolean                true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Once we click onto a menu option
	 *
	 * @param    MenuItem    item	Menu item selected
	 * @return boolean                True if we selected a menu managed, False otherwise
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	/**
	 * Handler to manage event onto the select of combobox
	 */
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

	}

	/**
	 * Handler to manage event onto select combobox
	 */
	public void onNothingSelected(AdapterView<?> parent) {
	}
}
