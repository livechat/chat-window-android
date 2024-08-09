# Android ChangeLog

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
