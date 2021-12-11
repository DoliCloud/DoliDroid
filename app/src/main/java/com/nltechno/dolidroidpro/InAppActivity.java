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

import com.nltechno.inapp.*;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;

/**
 * About activity class
 * 
 * @author eldy@destailleur.fr
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class InAppActivity extends Activity {

	private static final String LOG_TAG = "DoliDroidInAppActivity";
	

	static final String APP_LICENCE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWPkZ1rys0aYT9qQ7gHytljus58x9ZNwFUabsXgRAua2RwVkHnFfc8L2p68ojIb2tNHiRvMV6hYH2qViylftEMSYLFoKnuHzpL4tc+Ic+cTv/KtubP+ehUfISPQfYrZrukp3E8y0zM795Agsy8mefc2mmuOFJny/IZFLNyM5J+vjhoE6mO2l3jBmo08zu/3tz8Mbo/VYqJSs+P9UTppwF8ovB6u3fGPFeqblAdGize9WQ1L4SXNYblIjCklYj0rbXHFN3aJCjV9sSo0U+qdi6i+mT+CZgj09W1+U7RpkNJ6OczspTwhFh7/1nEev3Zci17TIFXNyP2v5aGMoBuCPwIDAQAB";


	IabHelper mHelper;
	
	
	
	/**
	 * Called when activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);


		String base64EncodedPublicKey = APP_LICENCE_KEY;
		   
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		
   		// enable debug logging (for a production application, you should set this to false).
    	mHelper.enableDebugLogging(true);
    		
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(LOG_TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(LOG_TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(LOG_TAG,"Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(LOG_TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        
        
        
		setContentView(R.layout.activity_inapp);
	}

	/**
	 * Called when activity start
	 */
	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();

	}


	/**
	 * Called when activity is destroyed
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
	   super.onDestroy();

	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
	

	
	// To make a purchase

	//mHelper.launchPurchaseFlow(this, IS_REGISTERED, RC_REQUEST, mPurchaseFinishedListener);
	
	
	
	// Listeners

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(LOG_TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(LOG_TAG,"Failed to query inventory: " + result);
                return;
            }

            Log.d(LOG_TAG, "Query inventory was successful.");
        }
    };
    
	
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
	        Log.d(LOG_TAG, "Purchase finished: " + result + ", purchase: " + purchase);
	        if (result.isFailure()) {
	            // Oh noes!
	            Log.d(LOG_TAG, "Error purchasing: " + result);
	            return;
	        }
	
	        Log.d(LOG_TAG, "Purchase successful. Sku="+purchase.getSku());
	    }
	};
	
}
