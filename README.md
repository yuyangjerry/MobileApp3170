
# Heartsight Mobile App
## How to install and deploy

The mobile app is not publicly deployed. If you want to see the app running you will need to build the app on your machine and then run it on your mobile device or emulator. You can do so by following these steps



1.  Fork and Clone the Mobile App repository on your local machine

2.  Open Android Studio, click File -> Open, then locate and select the repository you just downloaded and click “Ok”.

3.  Create a new Firebase project and link the mobile app to it. You can follow the official tutorial on how to [Add Firebase to your android project](https://firebase.google.com/docs/android/setup).

4.  You can now launch the app on your mobile device or emulator, you can follow the official tutorial on how to [Run your app](https://developer.android.com/training/basics/firstapp/running-app). Note, the app requires a minimum SDK version 21, which corresponds to Android 5.0 Lollipop. You will need a mobile device or an emulator that supports these versions.

Android Studio will take care of downloading the required dependencies for you.

## Required Hardware

-   ECG heart rate monitoring device


-   For development an arduino emulator was used with fictional data


-   Android compatible phone



## Required Software

-   We recommend using Android Studio Arctic Fox, 2020.3.1 or later to run or edit the app. Other SDKs are possible(minimum version is 21).


-   Any Android version that is 5.1 or above should be runnable, however, it is recommended to use version 11 or latest as we used version 11 for testing.





## Version Strategy

Though not yet implemented, we suggest using [Semantic Versioning 2.0.0](https://semver.org/) with the version number being represented

by MAJOR.MINOR.PATCH. Where an increment is made to the respective version depending on the type of change being made.



## Pull Request Strategy



1. Break pull requests down into feature sized chunks to retain functionality but limit scope. Don’t mix features into the same pull request.

2. Make sure the more complicated blocks of code are well commented.

3. Make sure to add the necessary details into the commit messages to give more context with what the changes are about.

4. Indicate in the pull request the level of testing which has occurred.
