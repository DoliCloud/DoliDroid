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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;



/**
 * Class to manage end of download
 */
public class DownloadBroadcastReceiver extends BroadcastReceiver {
	private static final String LOG_TAG = "DoliDroidDownloadBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) 
        {
        	Log.d(LOG_TAG, "onReceive ACTION_DOWNLOAD_COMPLETE");
            //Show a notification
			Toast.makeText(context, context.getString(R.string.downloadComplete), Toast.LENGTH_LONG).show();
        }
        
        if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) 
        {
        	Log.d(LOG_TAG, "onReceive ACTION_NOTIFICATION_CLICKED");
            //Show a notification
        	//Toast.makeText(context, "Download from Dolidroid still running", Toast.LENGTH_LONG).show();
        }
        
    }
}
