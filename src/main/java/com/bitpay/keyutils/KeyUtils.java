package com.bitpay.keyutils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;

public class KeyUtils {

	// used in private methods at bottom of this file
	final private static char[] hexArray = "0123456789abcdef".toCharArray();
	final private static String pemPattern = "-----BEGIN EC PRIVATE KEY-----\nMHQCA.*SuBBAAK\noUQDQ.*\n.*\n.*END EC PRIVATE KEY-----\n"; 
	
	public static String generatePem() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

		KeyPair pair = createNewKeyPair();

		StringWriter stringWriter = new StringWriter();
		JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
		pemWriter.writeObject(pair);
		pemWriter.close();

		String pem = stringWriter.toString();
		return pem;
	}

	public static String getCompressPubKeyFromPem(String pem) throws IllegalArgumentException, IOException {

		KeyPair keys = keyPairFromPEM(pem);

		String[] valuesXY = getPublicKeyValuesXY(keys);
		String xVal = valuesXY[0];
		String yVal = valuesXY[1];
		
		// only need the end of the y-coordinate
		String yCoordEnd = yVal.substring(60).toUpperCase();

		// only need final digit of Y-coordinate to check if even or odd
		// for prefix of compressed public key
		Long yFinalNumber = Long.parseLong(yCoordEnd.substring(yCoordEnd.length()-1), 16);

		String prefix;
		if (yFinalNumber % 2 == 1) {
			prefix = "03"; // put 03 prefix if y is odd
		}
		else {
			prefix = "02"; // put 02 prefix if y is even
		}

		return prefix + xVal.toUpperCase();

	}

	public static String getPrivateKeyFromPem(String pem) throws IllegalArgumentException, IOException {

		KeyPair keys = keyPairFromPEM(pem);

		String fullPrivateKey = keys.getPrivate().toString();
		int startKeyIndex = fullPrivateKey.indexOf("S: ") + 3;
		String privateKey = fullPrivateKey.substring(startKeyIndex, startKeyIndex + 64);
		privateKey = privateKey.replaceAll("\n", "").replaceAll(" ", ""); // remove unwanted whitespace
		privateKey = checkHas64(privateKey);
		return privateKey.toUpperCase();
	}

	public static String getSinFromPem(String pem) throws IllegalArgumentException, NoSuchAlgorithmException, IOException {

		String hexPubKey = getCompressPubKeyFromPem(pem);	

		// Step 1 and 2
		// Hex to Bytes, SHA-256 and RIPEMD-160		
		byte[] bytesPubKey1 = hexToBytes(hexPubKey);
		byte[] bytesPubKey2 = Utils.sha256hash160(bytesPubKey1);
		String shaAndRipe = bytesToHex(bytesPubKey2);

		// Step 3
		// Add 0F02 to Hex of Step 2
		String step3 = "0F02" + shaAndRipe;

		// Step 4
		// Hex to Bytes, Double SHA-256
		byte[] bytesPubKey3 = hexToBytes(step3);
		byte[] bytesPubKey4 = Sha256Hash.hashTwice(bytesPubKey3); //Utils.doubleDigest(bytesPubKey3);

		// Step 5
		// Substring of first 4 bytes (first 8 characters)
		String step4Hex = bytesToHex(bytesPubKey4);
		String step5 = step4Hex.substring(0, 8);

		// Step 6
		// Combine step 3 and step 5
		String step6 = step3 + step5;

		// Convert to base 58
		byte[] step6bytes = hexToBytes(step6);
		String sin = Base58.encode(step6bytes);

		return sin;
	}


	public static String signMsgWithPem(String msg, String pem) throws IllegalArgumentException, IOException {
		
		if (msg == null || msg.isEmpty())
			throw new IllegalArgumentException("Message cannot be empty.");
		
		String privKey = getPrivateKeyFromPem(pem);
		String pubKey = getCompressPubKeyFromPem(pem);
	
		byte[] privKeyBytes = hexToBytes(privKey);
		byte[] pubKeyBytes = hexToBytes(pubKey);
		
		@SuppressWarnings("deprecation")
		ECKey key = new ECKey(privKeyBytes, pubKeyBytes);
		return sign(key, msg);
	}

	// *******************************************
	// *         Private Methods Section         *
	// *******************************************

	private static int getHexVal(char hex)
	{
		int val = (int)hex;
		return val - (val < 58 ? 48 : (val < 97 ? 55 : 87));
	}


	private static byte[] hexToBytes(String hex) throws IllegalArgumentException
	{
		char[] hexArray = hex.toCharArray();

		if (hex.length() % 2 == 1) {
			throw new IllegalArgumentException("Error: The binary key cannot have an odd number of digits");
		}

		byte[] arr = new byte[hex.length() >> 1];

		for (int i = 0; i < hex.length() >> 1; ++i) {
			arr[i] = (byte)((getHexVal(hexArray[i << 1]) << 4) + (getHexVal(hexArray[(i << 1) + 1])));
		}

		return arr;
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;

			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String(hexChars);
	}

	private static String checkHas64(String str) {
		String str2 = str;
		if (str2.length() % 2 == 1 && str2.length() < 64)
			str2 = "0" + str2;
		while (str2.length() < 64)
			str2 = "00" + str2;

		return str2;
	}
	
	private static KeyPair createNewKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
		kpGenerator.initialize(ecSpec, new SecureRandom());
		KeyPair pair = kpGenerator.generateKeyPair();  // format of keys = PKCS#8, keys are in hex
		
		return pair;
		
	}
	
	private static void checkValidPEM(String pem) throws IllegalArgumentException {
		boolean validPem = pem.matches(pemPattern);
		if (!validPem) {
			throw new IllegalArgumentException("PEM is not in a valid format.");
		}
			
	}
	
	private static KeyPair keyPairFromPEM(String pem) throws IllegalArgumentException, IOException {
		
		checkValidPEM(pem);
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

		StringReader strReader = new StringReader(pem);
		PEMParser pemParser = new PEMParser(strReader);
		Object keyObject = pemParser.readObject();
		pemParser.close();

		KeyPair keys = converter.getKeyPair((PEMKeyPair) keyObject);
		return keys;
		
	}
	
	private static String[] getPublicKeyValuesXY(KeyPair keys) {
		
		String fullPublicKey = keys.getPublic().toString();
		
		int indexX = fullPublicKey.indexOf("X: ") + 3; 
		String xCoord = fullPublicKey.substring(indexX, indexX + 64);
		xCoord = xCoord.replaceAll("\n", "").replaceAll(" ", ""); // remove unwanted whitespace 
		String xCoord64 = checkHas64(xCoord); // make sure x-coordinate is 64 chars long
		
		int indexY = fullPublicKey.indexOf("Y: ") + 3; 
		String yCoord = fullPublicKey.substring(indexY, indexY + 64);
		yCoord = yCoord.replaceAll("\n", "").replaceAll(" ", ""); // remove unwanted whitespace 
		String yCoord64 = checkHas64(yCoord); // make sure y-coordinate is 64 chars long
		
		String[] valuesXY = {xCoord64, yCoord64};
		
		return valuesXY;
	}
	
	private static String sign(ECKey key, String input) {
		byte[] data = input.getBytes();

		Sha256Hash hash = Sha256Hash.of(data);
		ECDSASignature sig = key.sign(hash, null);

		byte[] bytes = sig.encodeToDER();

		return bytesToHex(bytes);
	}

}
