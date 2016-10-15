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
package com.nltechno.utils;

import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.view.ViewConfiguration;

/**
 * Utils method
 *  
 * @author ldestailleur
 */
public class Utils {

	public static String bytesToString(byte[] newStuff) 
	{
		String result = "";
		
		for (int j = 0; j < newStuff.length; j++) 
		{
			result = result + (char)newStuff[j];
		}

		return result;
	}



	/**
	 * isDownloadManagerAvailable
	 * 
	 * @param 	context 	Used to check the device version and DownloadManager information
	 * @return boolean		true if the download manager is available
	 */
	public static boolean isDownloadManagerAvailable(Context context) 
	{
	    try {
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } catch (Exception e) {
	        return false;
	    }
	}



	/**
	 * Return if a hardware menu is found
	 * 
	 * @return	boolean		True or not
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean hasMenuHardware(Activity activity) 
	{
        boolean hasMenuHardware=true;
        
        try {
	        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	        {
	        	hasMenuHardware=ViewConfiguration.get(activity).hasPermanentMenuKey();
	        }
        }
        catch(Exception e)
        { }
		
        return hasMenuHardware;
	}
	
	
	/**
	 * MD5Hex
	 * 
	 * @param s
	 * @return
	 */
    public static String MD5Hex(String s) 
    {
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(s.getBytes());
            result = toHex(digest);
        }
        catch (NoSuchAlgorithmException e) {
            // this won't happen, we know Java has MD5!
        }
        return result;
    }

    /**
     * toHex
     * 
     * @param a
     * @return
     */
    public static String toHex(byte[] a) 
    {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < a.length; i++) {
            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(a[i] & 0x0f, 16));
        }
        return sb.toString();
    }

}
