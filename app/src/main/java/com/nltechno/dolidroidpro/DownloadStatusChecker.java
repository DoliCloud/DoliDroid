package com.nltechno.dolidroidpro;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

// 1 instance of this class is created at each download
public class DownloadStatusChecker {
    private static final String LOG_TAG = "DoliDroidDownloadStatusChecker";
    private Context context;
    private Handler handler;
    private DownloadManager downloadManager;
    private long downloadId;

    public DownloadStatusChecker(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void startMonitoringDownloads(final long downloadId, final DownloadStatusListener listener, final long intervalMillis) {
        this.downloadId = downloadId;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int downloadStatus = getDownloadStatus(downloadId);
                if (downloadStatus != -1) {
                    if (listener != null) {
                        listener.onDownloadStatusUpdated(downloadStatus);
                    }
                }
                // Répétez la surveillance après l'intervalle spécifié
                handler.postDelayed(this, intervalMillis);
            }
        };

        handler.post(runnable);
    }

    public void stopMonitoringDownloads() {
        Log.d(LOG_TAG, "Try to remove handler for downloadId="+this.downloadId);
        handler.removeCallbacksAndMessages(null);
    }

    private int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (columnIndex > 0) {
                int status = cursor.getInt(columnIndex);
                cursor.close();
                return status;
            }
        }
        return -1;
    }

    public interface DownloadStatusListener {
        void onDownloadStatusUpdated(int status);
    }
}
