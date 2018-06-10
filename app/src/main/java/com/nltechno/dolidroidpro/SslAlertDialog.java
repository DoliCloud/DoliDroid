/* Copyright (C) 2016 Laurent Destailleur <eldy@users.sourceforge.net>
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

import android.app.Activity;
import android.app.AlertDialog;
import android.webkit.SslErrorHandler;
import android.content.DialogInterface;

/**
 * Class to manage SSl error handling
 */
public class SslAlertDialog {

    private SslErrorHandler handler = null;
    private AlertDialog dialog = null;
    public SecondActivity savedactivity;
    
    /**
     * Constructor
     * 
     * @param errorHandler
     * @param activity
     * @param errorcode
     */
    public SslAlertDialog(SslErrorHandler errorHandler, SecondActivity activity, String errorcode) {

        if (errorHandler == null || activity == null) return;

        this.savedactivity = activity;
        
        handler = errorHandler;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.notification_error_ssl_cert_invalid) + "\n" + errorcode + "\n" + activity.getString(R.string.notification_error_ssl_cert_invalidbis));
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();	// Once we proceed, error will also no more be triggered
                savedactivity.sslErrorWasAccepted = true;
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
                savedactivity.setResult(SecondActivity.RESULT_LOGOUT);
                savedactivity.finish();
            }
        });

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }

}
