# Using the BitPay Java Key Utilities Library

This JAR file provides utilities for use with the BitPay API. It enables creating keys, retrieving public keys, retrieving private keys, creating the SIN that is used in retrieving tokens from BitPay, and signing payloads for the `X-Signature` header in a BitPay API request.

## Quick Start
### Installation
To be able to use the BitPay Java Key Utilities, download the `java-key-utils-bitpay.jar` file. Save the file in the "lib" sub-folder under your Java Project folder. If your Java Project folder does not already have a "lib" sub-folder, make one and then save the `java-key-utils-bitpay.jar` file there. Add the file to your project's "Build Path". This process is slightly different depending on which editor (a.k.a. IDE) you are using.

If you already have a Java project and you are using Eclipse:
* Open your project in Eclipse
* Make sure the `java-key-utils-bitpay.jar` file is in the lib folder or your Java project
* Refresh the project if needed (right-click on the project folder and click "Refresh")
* Open the "lib" folder and right-click on `java-key-utils-bitpay.jar`. Select "Build Path" --> "Add to Build Path"
* Near the top of the file (below the "package ..." line if there is one) add the line `import com.bitpay.javaKeyUtils.KeyUtils;`

If you are using a different editor (a.k.a. IDE), please search online for instructions on how to incorporate the `java-key-utils-bitpay.jar` file. Then,
* Near the top of the file (below the "package ..." line if there is one) add the line `import com.bitpay.javaKeyUtils.KeyUtils;`

*You are now ready to use the BitPay Java Key Utilities*

## Using the Functions
To use the Java Key Utilities functions, make sure to complete the steps above. The following functions are provided (all functions return a string):
* generatePem() - creates a new set of public/private keys and returns the PEM string associated with those keys
* getCompressPubKeyFromPem(String pem) - uses the PEM to retrieve the compressed public key
* getPrivateKeyFromPem(String pem) - uses the PEM to retrieve the private key
* getSinFromPem(String pem) - uses the PEM to generate the associated SIN
* signMsgWithPem(String message, String pem) - uses the PEM to sign a string message

## API Documentation

API Documentation is available on the [BitPay site](https://bitpay.com/api).

## Running the Tests
Download the project folder from GitHub and open it in an editor. Run the KeyUtilsTest.java file in your editor (a.k.a. IDE). If there are errors, make sure that KeyUtilsTest.java and KeyUtils.java are in the same folder. If you are still getting errors or the tests are not passing, please let us know.
