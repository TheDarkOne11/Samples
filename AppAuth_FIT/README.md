# AppAuth login to FIT sample project
This project is an example of how to use AppAuth library for Android to connect to FIT internal OAuth2 provider called ZUUL.
It is rewritten official demo app from the AppAuth github.

### Source of this sample: 
https://github.com/openid/AppAuth-Android/tree/master/app

### Purpose:
The app makes it possible to log into FIT ZUUL (OAuth2 protected) and try out some of the endpoints.

## Important info
#### OAuth2 config
All configuration is done inside "raw/auth_config.json".

Part of redirectURI has to match the value set inside "app/build.gradle" in a placeholder, otherwise the PendingIntents won't work. FIT OAuth2 token endpoint requires clientSecret.
