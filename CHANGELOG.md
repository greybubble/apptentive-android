2012-12-18 skykelsey v0.6.4
---------------------------

Bugs fixed:

* ANDROID-114 Rating dialog comes up twice

2012-12-18 skykelsey v0.6.3
---------------------------

Bugs fixed:

* ANDROID-112 Choice dialog comes up twice
* ANDROID-113 Revert behavior of remind setting

2012-12-14 skykelsey v0.6.2
---------------------------

Bugs fixed:

* ANDROID-109 Google Play opens up on top of host application when rating app
* ANDROID-110 Ratings prompt doesn't respect zero value settings.

Added some tests as well.


2012-10-10 skykelsey v0.6.1
---------------------------

Bugs fixed:

* ANDROID-95 Come up with a new way to detect app starts and stops
* ANDROID-96 Crash on pre API 5 phones

App starts are now detected by looking for a 10 second gap where no App Activities are running.

2012-09-13 skykelsey v0.5.2
---------------------------

Bugs fixed:

* ANDROID-84 Remove checked in API keys from the Android repository
* ANDROID-85 Example app has API key in the resources file, but also requires updating the manifest
* ANDROID-89 Add callback after survey completion

2012-09-12 skykelsey v0.5.1
---------------------------

Major changes:

There was a problem with how we initialize our SDK that could lead to NPEs if the main Activity had been garbage collected,
and interfered with unit testing individual child Activities. Fixed that.

Fixed a problem with how we check for app uses. Now we keep track of each Activity instead of the Application as a whole.

Bugs fixed:

* ANDROID-83: App crashes when no permissions are set for the app
* ANDROID-88: Android initialization needs rework

2012-08-15 skykelsey v0.5.0
---------------------------

Major changes:

Updated what counts as a "use". Before, we were incrementing uses on app launch, defined as a call to the main Activity's
onCreate() method. That is not very useful, since hitting home screen backgrounds the app, but won't increment uses upon
return. So instead, we figure out when the app is put into the background, and when it comes back.

Updated app integration process. Instead of making a bunch of Apptentive API calls, you can now inherit from our
ApptentiveActivity or ApptentiveListActivity. These classes allow you to save time integrating. You can also delegate
your Activity's event handlers into Apptentive manually if you can't inherit from us.

Redid the example apps. The previous "Demo" app was not really a demo, but a testing app. Renamed it accordingly.
Also added two new "Example" apps: one using inheritance for integration, and the other using delegation.


Bugs fixed:

* ANDROID-79: Add Changelog
* ANDROID-78: Externalize strings
* ANDROID-77: Send UUID with survey response - Android
* ANDROID-76: Check out using a res bundle for apptentive configuration.
* ANDROID-74: Responses to multichoice questions are being sent as an array
* ANDROID-66: When tapping on a multiple choice question, if the keyboard is up, hide it
* ANDROID-65: App config should be cached unless debugging
* ANDROID-63: Add check for min_selections
* ANDROID-61: If an API key isn't specified, return a better error message
* ANDROID-60: Survey answer options that are more than one line don't display well
* ANDROID-56: Send survey time to completion with responses
* ANDROID-55: Disable Ratings Flow when ratings_enabled preference is false
* ANDROID-35: Don't start rating flow when no data connection is present
