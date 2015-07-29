# Using the BitPay Java Key Utilities Library

This dependency/JAR file provides utilities for use with the BitPay API. It enables creating keys, retrieving public keys, retrieving private keys, creating the SIN that is used in retrieving tokens from BitPay, and signing payloads for the `X-Signature` header in a BitPay API request.

## Quick Start
### Installation
To use the BitPay Java Key Utilities, use the `com.bitpay.keyutils` dependency by [downloading it from the Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.bitpay%22). Be sure to use the latest released version of the key utilities.

If you already have a Maven Java project and you are using Eclipse:

* Open your project in Eclipse.
* Go the pom.xml file and select the "Dependencies" tab at the bottom of the pane.
* In the "Dependencies" column of that pane, select "Add..."
* Type in the Group Id "com.bitpay" and the Artifact Id "keyutils" and Version "2.0.0" (or use the search bar and type in the groupId, then select the latest version of the keyutils)
* Refresh the project (right-click on the project folder and click "Refresh", then right-click again and select "Maven" --> "Update Project").
* Make sure that the keyutils jar file shows up under "Maven Dependencies" in the "Package Explorer" pane on the left.
* Near the top of your Java class file (below the "package ..." line if there is one) add the line `import com.bitpay.keyutils.KeyUtils;`.

If you have a Java project that does not use Maven, go to the Maven Central Repository and download the .jar file directly. Then add it to your project. Finally, near the top of your Java class file (below the "package ..." line if there is one) add the line `import com.bitpay.keyutils.KeyUtils;`.

If you are using a different editor/IDE, please search online for instructions on how to incorporate the Maven dependency (If you need to, you can directly download the .jar file from the Maven Central Repository) Then, near the top of your Java class file (below the "package ..." line if there is one) add the line `import com.bitpay.keyutils.KeyUtils;`.

*You are now ready to use the BitPay Java Key Utilities*

## Using the Functions
To use the Java Key Utilities functions, make sure to complete the steps above. To use a function, type in `KeyUtils.function_name()` (for example: `KeyUtils.generatePem()`, `KeyUtils.getSinFromPem(pem)`).
The following functions are provided (all functions return a string):

* generatePem() - creates a new set of public/private keys and returns the PEM string associated with those keys.
* getCompressPubKeyFromPem(String pem) - uses the PEM to retrieve the compressed public key.
* getPrivateKeyFromPem(String pem) - uses the PEM to retrieve the private key.
* getSinFromPem(String pem) - uses the PEM to generate the associated SIN.
* signMsgWithPem(String message, String pem) - uses the PEM to sign a string message.

## API Documentation

API Documentation is available on the [BitPay site](https://bitpay.com/api).

## Running the Tests
There are two ways to download and run the test file (KeyUtilsTest.java):

In either case, first download the entire GitHub source files by going to the [Releases](https://github.com/bitpay/java-key-utils/releases) and selecting the download format of the source code that you want. Then, extract the contents of the downloaded file. From here you can either,

Import all of the files into your editor/IDE, and run `KeyUtilsTest.java`.
  * If there are errors, make sure that KeyUtilsTest.java and KeyUtils.java are in the same folder. If you are still getting errors or the tests are not passing, please let us know.

OR

Import only the test file (`KeyUtilsTest.java` under the `src` folder) into your editor/IDE
  * Add the Maven Dependency (found in the Quick Start instructions above) OR download and link the .jar file (also found in the Quick Start instructions above) to your Java project. Then run the the test file `KeyUtilsTest.java`.
  * If there are errors, make sure that the dependency or .jar file is properly imported into the project. If you are still getting errors or the tests are not passing, please let us know.

If you find a bug, please let us know so we can improve our code. Thank You.
