# Android ChangeLog

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