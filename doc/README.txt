-----------------------------------------------------------------
- This file contains document about building android application
-----------------------------------------------------------------


----- GUIDE FOR DEVELOPER
This tutorial explain how to start to develop onto DoliDroid.
1) Install Eclipse
2) Install Android SDK + Other optionnal SDK (AdMobs SDK, Analytics SDK, Play Billing Library)
3) Setup Eclipse to use the Android SDK.
4) Create AVD from Eclipse menu.
5) git clone dolidroid sources (need to be included into private dolidroid project on doliforge.org)
6) Make your change.

***** Creating a new android project
Use Eclipse wizard.
Note that a library file android-support-v4.jar is added into lib. You can remove it if you
don't need backward compatibility layer: http://developer.android.com/tools/support-library/index.html 

***** To add development of Google Play InApp payments:
http://developer.android.com/google/play/billing/billing_integrate.html

***** To add development of Google Analytics features:
1) Create a project into https://cloud.google.com/console/project
2) Into APIs menu, enable Analytics API
3) Into Credentials menu, get OAuth 2 Client ID and Client json secret file.
4) Download the zip google-api-java-client-x.y.zip with jar. Extract it and put jar
into directory "libs" of android project.
5) Download the zip google-api-services-oauth-client-x.y.zip with jar. Extract it and put jar
into directory "libs" of android project.



To generate R.java:
C:\dev_MTD\adt-20130219\sdk\platform-tools\aapt.exe package -f -v -m -S res -J gen -M AndroidManifest.xml -I C:\dev_MTD\adt-20130219\sdk\platforms/android-17/android.jar

To take screenshots of android page.
- Launch application into emulator
- Add perspective DDM into Eclipse.
- Choose emulator and application and click onto screenshot.

To generate a package to publish:
- Create package with Eclipse wizard (use java dolidroid key store in admin/.ssh/dolidroid.keystore)


----- GUIDE For Google Play
Go on https://play.google.com/apps/publish/
To take screenshots, add view Devices into Eclipse and launch session into emulator 720x1280
Click add file and upload apk file


----- GUIDE For F-Droid 
https://fsfe.org/campaigns/android/help.en.html
https://f-droid.org/manual/


----- GUIDE For Amazon
Go on https://developer.amazon.com/
Choose mobile app
Login with contact@destailleur.fr
To take screenshots, add view Devices into Eclipse and launch session into emulator 720x1280
Click add file and upload apk file


----- GUIDE FOR TESTER
You can get beta version by subscribing to Google+ community "Dolidroid Testers".
Then you can subscribe to be a tester:
https://play.google.com/apps/testing/com.nltechno.dolidroid

If you get an error running dolidroid application this is how you can report the bugs. 

First thing is to get a copy of android rolling log file, just after error. For must
unzip the android sdk onto your computer. Then run the adb command to catch log file
> cd /path_to_android_sdk/platform-tools/
> ./adb [-d|-e] logcat -s DoliDroidActivity:V AndroidRuntime:D | tee /tmp/logfile.log
or
> cd /path_to_android_sdk/platform-tools/
> ./adb -s deviceid logcat -s DoliDroidActivity:V AndroidRuntime:D | tee /tmp/logfile.log

Note: Onto a phone, link computer and phone with usb cable.
Note: To get device id, run ./adb devices -l

Then send file /tmp/logfile to eldy@users.sourceforge.net



***** Errors files
When there is an error in an application files may be generated:
/data/anr/traces.txt
/data/tombstones/tombstones
/data/data/dolidroid/traces.txt


***** adb usage *****

List android devices connected
adb devices -l

If you get error « error: insufficient permissions for device », try to rerun adb from root.
cd /media/HDDATA1_LD/tmp/android-sdk-linux/platform-tools; sudo ./adb kill-server; sudo ./adb start-server; ./adb devices

Pour accéder en ligne de commande à l'appareil :
adb shell

To swith as root :
su - root

Pour monter un filesystem
mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system

Pour modifier les permissions d'un répertoire
chmod 777 /system/app

Pour copier un fichier apk depuis le PC vers le rep des applis /system/app :
./adb push ../../Téléchargements/GooglePlayServices.apk /system/app
WARNING : * is not allowed. You must use exact file name !!!



Pour copier un fichier apk dans le rep des applis /system/app :
/home/ldestailleur/android-sdk/platform-tools/adb shell 
