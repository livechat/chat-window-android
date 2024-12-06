# Android ChangeLog

## Version 2.4.5

* Revised error reporting in the `ChatWindowEventsListener.onError` callback and conditions for displaying the error view.
* JavaScript errors are now ignored.

**Breaking Changes**
* `ChatWindowErrorType.Console` removed.

## Version 2.4.4

* Report http errors in the ChatWindowEventsListener.onError method

## Version 2.4.3

* Updated gson to `2.11.0` fixing issues when minification enabled

## Version 2.4.2
**Breaking Changes**
* `ChatWindowConfiguration` and it's `Builder` custom parameters name change - `customVariables` to `customParameters`

## Version 2.4.1 - Oct 4th, 2024
**Breaking Changes**
* Minimum Android SDK version is now 21
* `ChatWindowUtils.clearSession()` - Context param removed
* `ChatWindnowView.reload()` - Bool param removed

## Version 2.4.0 - Oct 4th, 2024
Migrate to AndroidX for managing Fragments
Allow for sending multiple files at once on Android 21+

**Breaking Changes**

Initialization
* removed `setConfiguration` method from `ChatWindowView`
* changed `initialize` to `init` and added required `ChatWindowConfiguration` parameter

Move away from deprecated `startActivityForResult` to `registerForActivityResult` to start file picker Activity and handle its result.

Removed:
* `onStartFilePickerActivity` callback from `ChatWindowEventsListener`
* `onActivityResult` method from `ChatWindowView`

Added:
* `supportFileSharing` in `ChatWindowView`
* `onFilePickerActivityNotFound` in`ChatWindowEventsListener`

See "Sharing files" section in the docs for more details.

## Version 2.3.4 - Sep 1st, 2024
Remove obsolete WebView popup from layout and prevent unnecessary view inflation

## Version 2.3.3 - Aug 30th, 2024
Code refactor - remove unused code, introduce MVP pattern, cleanups

## Version 2.3.2 - Aug 9th, 2024
Simplify reloading - always reload url.
This fixes error view not disappearing when chat is reloaded - #34

## Version 2.3.1 - Aug 9th, 2024
Upgrade Android Gradle Plugin to 7.4.2

## Version 2.3.0 - July 12th, 2024
Targeting Android 13 (API 33)

## Version 2.2.0 - December 3rd, 2021
Add possibility to use SnapCall

## Version 2.2.0 - December 3rd, 2021
Small redesign for better readability
Allows for easier chat configuration change #64

## Version 2.1.6 - June 17, 2021
Handling keyboard overlapping chat input field in full screen activities
Update android compile and target sdk version to 30

## Version 2.1.5 - May 21, 2021
Fix NoClassFound error

## Version 2.1.4 - May 20, 2021
Provide convenience method for clearing chat session

## Version 2.1.3 - May 20, 2021
Migrate to androidx support libraries, update gradle, build tools and dependencies

## Version 2.1.2 - April 15, 2020
Handle case where chat window configuration failed

### Changes
- Expose reload and isChatLoaded methods
- Handle case where chat window configuration failed

## Version 2.1.1 - April 2, 2020
Get rid of problematic AsyncTask for obtaining configuration and use volley for that.
This should result in quicker and more reliable initial widget configuration.

### Changes
- Get rid of problematic AsyncTask
- Use volley and leverage caching
- Unify handling errors from initial configuration
- Introduce new error type - `InitialConfiguration`

## Version 2.1.0 - March 30, 2020
**Breaking Change**
Patch release improving error handling and fixing bad url check that caused NPE.
Breaking change comes from adding possibility to handle error. If you don't want to use that,
just override onError method and return false

### Changes
- Fixed NullPointerException crash when tapping on email link
- Added onError method to give more control to users of this library
- Decreased minimum android sdk to 15
- Expose configuration builder setters
