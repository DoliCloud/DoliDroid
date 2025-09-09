# DEV INFOR

To launch the app from Android Studio directly on smartphoen, you must enable the Developer mode, then enable the bug mode on USB.
Plug the phone to computer and smartphone should appears in the list of devices.


# ERROR WHEN RUNNING THE EMULATOR

Try to run the emulator profile from command line with
export ANDROID_SDK_ROOT=~/Android/Sdk
cd ~/.android/avd/XXX
emulator -avd Pixel_8a

If error vulkan, try
emulator -avd Pixel_8a -gpu swiftshader_indirect

If it's ok, then edit the Device profile to et the Graphics acceleration to "Software"
