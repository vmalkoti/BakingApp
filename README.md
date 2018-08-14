## Baking app
Code for Udacity Android Developer Nanodegree Project 4 - Baking App

### What Will I Learn
In this project we will:
* Use Exoplayer to display videos.
* Handle error cases in Android.
* Add a widget to your app experience.
* Leverage a third-party library in your app.
* Use Fragments to create a responsive design that works on phones and tablets.


#### Architecture
* Single activity with multiple fragments
* Master-detail flow


#### Libraries used
1. Retrofit (network calls)
2. LiveData (lifecycle aware data)
3. Espresso (test framework)
4. Picasso (for displaying images)


#### Known Issue
Espresso test sometimes fails. 

**Observation:** This happens after emulator is left for some time without any user interaction. 

**Symptom:** Homescreen date/time widget has a translucent background. At that time, on running Espresso test, app doesn't fully launch and tests fail.

**Workaround:** Manually click on emulator homescreen before running Espresso tests.  