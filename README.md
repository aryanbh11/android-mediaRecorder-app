# Android Media Recorder App

## Features
### Main View:
- A GridView to display (thumbnail display) all videos and photos captured from your app and stored 
  on the user mobile.
- A button to activate the camera to take a photo or video.
- A button to immediately upload the media content to a cloud server for backup. When the user 
  clicks on this button, if the device is not connected to WiFi, a pop-up message is displayed to 
  the user “You are not connected to WiFi. Would you like to proceed?” to request user 
  preference in uploading the media. If the user clicks YES then update happens immediately but if 
  they click NO then the update is scheduled for when the device reconnects to WiFi.

### Custom Camera View:
Custom camera interface built by leveraging the built-in camera support. This view contains the 
following buttons:
- A button to capture a photo
- A button that starts the video recording
- A stop recording button which replaces the video recorder button once the camera starts recording 
Once a photo or video is captured, the media is saved locally, and the saved media appears on the
main view (GridView).
  
### Cloud Backup:
This app backs up the media (photos and videos) to Google’s Firebase platform cloud server. 
Synchronisation of media happens automatically without user interaction. This regular automated 
backup only happens when the user is connected to WiFi. If the device is not connected to WiFi at 
the regular upload time, then the update is scheduled for when the device reconnects to WiFi.

## Demonstration Video
[UNLISTED YouTube Video](https://youtu.be/kiitPwC5BHM)

## References
- [Custom Camera API using Android Studio Part 1](https://youtu.be/_wZvds9CfuE)
- [Custom Camera API using Android Studio Part 3](https://www.youtube.com/watch?v=FFTd2INvwBc) 
- [Custom Camera API using Android Studio Part 4](https://www.youtube.com/watch?v=hCqC-XLKeu4)
- [Image Adapter for Grid](https://stackoverflow.com/a/6548453)
- [Getting File Paths from android directory](https://stackoverflow.com/a/8647397)
- [Android Docs: Recycler View](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [Android Docs: Custom Camera](https://developer.android.com/guide/topics/media/camera#custom-camera)
- [Firebase Storage: Upload and Retrieve Images](https://www.youtube.com/watch?v=lPfQN-Sfnjw)
- [Check if Android Device is connected to Wifi](https://stackoverflow.com/a/3841407)
- [Timer](https://stackoverflow.com/a/4612602)
- [Check if App is running in background](https://stackoverflow.com/a/48767617)

## Note
- Inline comments have been made to give information where necessary
- Javadoc comments have been made for all new **public** classes and methods 