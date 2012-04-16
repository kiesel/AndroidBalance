AndroidBalance
===============

# About
AndroidBalance is an Android application by which you can monitor your
bank accounts through the HBCI protocol. This protocol is used in Germany
to request balance and transaction information over the Internet, as well
as create new transactions and other jobs for managing your accounts.

# Building
AndroidBalance uses maven and the maven-android-plugin to build the apk file. To
build the apk, just run ```mvn install```, to deploy the apk to an AVD or connected
debug-enabled Android phone, use ```mvn android:deploy```.

## Dependencies
### hbci4java
HBCI is a network protocol used by (all?) german bank institutes; the hbci4java
library offers a client library to talk this protocol, which is what 
AndroidBalance uses.
Because there's no maven artifact for hbci4java, you need to manually provide
the .jar in your local repository, with the version requested in .pom.
(In Netbeans this works conventiently by right-clicking the dependency in the
project view, then choose ```Manually install artifact```.)

### hbcifacade
This is just a small wrapper around hbci4java developed by me. It is supposed
to simplify using hbci4java and provides some value classes.

# Security
Connecting to your bank institute is a sensitive process; if some malicious
party gets your credentials (account numbers, PIN, ...) you could get into some
trouble.

This software does not take any responsibilities for misuse. If you want to
use it, do so on your own risk.

Please feel free to do security reviews and send in pull requests to improve it.