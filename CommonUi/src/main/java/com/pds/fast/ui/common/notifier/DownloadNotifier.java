//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pds.fast.ui.common.notifier;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat.Builder;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadNotifier {
    private static final AtomicInteger mIDGenerator = new AtomicInteger();
    private final int mNotificationId = this.generateGlobalId();
    private final NotificationManager mNotificationManager;
    private Notification mNotification;
    private Builder mBuilder;
    private String mTitle;
    public static final String PRIMARY_CHANNEL = "default";
    private Application application;

    public DownloadNotifier(String url) {
        this.mTitle = this.splitName(url);
        this.mNotificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            if (VERSION.SDK_INT >= 26) {
                this.mBuilder = new Builder(application, "default");
                NotificationChannel mNotificationChannel = new NotificationChannel("default", this.getApplicationName(), NotificationManager.IMPORTANCE_LOW);
                NotificationManager mNotificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
                mNotificationChannel.enableLights(false);
                mNotificationChannel.enableVibration(false);
                mNotificationChannel.setSound((Uri) null, (AudioAttributes) null);
            } else {
                this.mBuilder = new Builder(application);
            }

            this.initBuilder();
            this.mNotification = this.mBuilder.build();
            this.mNotificationManager.notify(this.mNotificationId, this.mNotification);
        } catch (Throwable var4) {
        }

        this.progress(0);
    }

    void initBuilder() {
        this.mBuilder.setContentIntent(PendingIntent.getActivity(application, 200, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        this.mBuilder.setSmallIcon(17301633);
        this.mBuilder.setTicker("You have a new notice");
        this.mBuilder.setContentTitle(this.mTitle);
        this.mBuilder.setContentText("Coming soon to download the file");
        this.mBuilder.setWhen(System.currentTimeMillis());
        this.mBuilder.setAutoCancel(true);
        this.mBuilder.setPriority(-1);
        this.mBuilder.setDefaults(0);
    }

    public void progress(int progress) {
        String contentText = this.mTitle + "  " + progress + "%";
        this.mBuilder.setContentText(contentText);
        this.mBuilder.setProgress(100, progress, false);
        this.mNotification = this.mBuilder.build();
        this.mNotificationManager.notify(this.mNotificationId, this.mNotification);
    }

    public void cancel() {
        this.mNotificationManager.cancel(this.mNotificationId);
    }

    public void installApp(Context context, File file) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (VERSION.SDK_INT >= 24) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, "com.stones.download.fileprovider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;

        try {
            packageManager = application.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(application.getPackageName(), 0);
        } catch (NameNotFoundException var4) {
            applicationInfo = null;
        }

        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    String splitName(String url) {
        if (!TextUtils.isEmpty(url)) {
            String[] split = url.split("/");
            return split[split.length - 1];
        } else {
            return "download";
        }
    }

    int generateGlobalId() {
        return mIDGenerator.getAndIncrement();
    }
}
