/* Copyright (C) 2023 Laurent Destailleur  <eldy@users.sourceforge.net>
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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;

public class ManageUrlAdapter extends ArrayAdapter<String> {
    private static final String LOG_TAG = "DoliDroidManageUrlAdapter";

    private final Activity activity;
    private final Context context;
    private final String[] values;

    public ManageUrlAdapter(Context context, String[] values) {
        super(context, -1, values);
        Log.d(LOG_TAG, "ManageUrlAdapter constructor");
        this.context = context;
        this.values = values;
        this.activity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.manage_url_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.text_view);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_view);


        textView.setText(this.values[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle image click
                Log.d(LOG_TAG, "ManageUrlAdapter we click on image to delete entry "+position);

                // Code is similare in delete onClick of the delete of the ManageUrlActivity
                FileOutputStream fos;
                try
                {
                    String savedDolRootUrl = MainActivity.listOfRootUrl.get(position).url;

                    // Now loop of each entry of file MainActivity.FILENAME and rewrite or exclude the entry
                    fos = context.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE);
                    int itoremove = -1;
                    int sizeofarray = MainActivity.listOfRootUrl.size();
                    for (int i = 0; i < sizeofarray; i++)
                    {
                        String s = MainActivity.listOfRootUrl.get(i).url;
                        if (! s.equals(savedDolRootUrl)) {
                            // Keep this value s
                            Log.d(LOG_TAG, "write " + s);
                            fos.write((s+"\n").getBytes());
                        } else {
                            // We fount the entry to remove
                            Log.d(LOG_TAG, "exclude entry " + s);
                            itoremove = i;
                        }
                    }
                    fos.close();

                    // If success, we can remove entry from memory array listOfRootUrl
                    if (itoremove >= 0) {
                        Log.d(LOG_TAG, "remove entry i=" + itoremove);
                        MainActivity.listOfRootUrl.remove(itoremove);

                        // Define an array with string to show into the ArrayAdapter list
                        String[] listofRootUrlString = new String[MainActivity.listOfRootUrl.size()];
                        int count = 0;
                        while (count < MainActivity.listOfRootUrl.size()) {
                            String tmps = MainActivity.listOfRootUrl.get(count).getDomainUrl().replaceAll("\\/$", "");
                            tmps += " ("+MainActivity.listOfRootUrl.get(count).getScheme();
                            if (! "".equals(MainActivity.listOfRootUrl.get(count).getBasicAuthLogin())) {
                                tmps += " - "+MainActivity.listOfRootUrl.get(count).getBasicAuthLogin();
                                //tmps += ":"+MainActivity.listOfRootUrl.get(count).getBasicAuthPass();
                                tmps += ":*****";
                            }
                            tmps += ")";

                            listofRootUrlString[count] = tmps;
                            count++;
                        }

                        // Force restart of activity to reload the page/list
                        activity.recreate();
                    }
                }
                catch(Exception ioe)
                {
                    Log.e(LOG_TAG, "Error");
                }
            }
        });

        return rowView;
    }
}
