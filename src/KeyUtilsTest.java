import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.junit.Test;

import com.google.bitcoin.core.ECKey;

public class KeyUtilsTest {

	final String pemPattern = "-----BEGIN EC PRIVATE KEY-----\nMHQCA.*SuBBAAK\noUQDQ.*\n.*\n.*END EC PRIVATE KEY-----\n"; 

	final String pem = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEICg7E4NN53YkaWuAwpoqjfAofjzKI7Jq1f532dX+0O6QoAcGBSuBBAAK\noUQDQgAEjZcNa6Kdz6GQwXcUD9iJ+t1tJZCx7hpqBuJV2/IrQBfue8jh8H7Q/4vX\nfAArmNMaGotTpjdnymWlMfszzXJhlw==\n-----END EC PRIVATE KEY-----\n";
	final String pubKeyCompressed = "038D970D6BA29DCFA190C177140FD889FADD6D2590B1EE1A6A06E255DBF22B4017";
	final String privateKey = "283B13834DE77624696B80C29A2A8DF0287E3CCA23B26AD5FE77D9D5FED0EE90";
	final String sin = "TeyN4LPrXiG5t2yuSamKqP3ynVk3F52iHrX";
	final String msg = "This is a test message.";

	final String pem2 = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEINMwkuB8YAtSTJzUNals8F2lXxsLIncL3rOc8dqRdps8oAcGBSuBBAAK\noUQDQgAEn0OPHdZ0hx+tLRobqDMbC5U12k+BAzynN/wMjzG3axbkgNIFGLim30pf\nh1Lvp4eFVHUydkbP250fTOrJ4zo7RQ==\n-----END EC PRIVATE KEY-----\n";
	final String pubKeyCompressed2 = "039F438F1DD674871FAD2D1A1BA8331B0B9535DA4F81033CA737FC0C8F31B76B16";
	final String privateKey2 = "D33092E07C600B524C9CD435A96CF05DA55F1B0B22770BDEB39CF1DA91769B3C";
	final String sin2 = "TfFW1ePJ5q6EZKBsYUrMGQEbb6z4fBmx6BW";
	final String msg2 = "Testing by using this message.";

	@Test
	public void testGeneratePem() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		String testPem = KeyUtils.generatePem();
		assertTrue(testPem.matches(pemPattern));
	}

	@Test
	public void testCompressPubKeyFromPem() throws IOException {
		String pubKey = KeyUtils.getCompressPubKeyFromPem(pem);
		assertEquals(pubKeyCompressed, pubKey);
	}


	@Test
	public void testPrivKeyFromPem() throws IOException {
		String priKey = KeyUtils.getPrivateKeyFromPem(pem);
		assertEquals(privateKey, priKey);			
	}


	@Test
	public void testSinFromPem() throws NoSuchAlgorithmException, IOException {
		String testSin = KeyUtils.getSinFromPem(pem);
		assertEquals(sin, testSin);   
	}


	@Test
	public void testSigning() throws IOException, NoSuchAlgorithmException {
		String hexDer = KeyUtils.signMsgWithPem(msg, pem);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(msg.getBytes("UTF-8")); 
		byte[] msgBytes = md.digest();

		byte[] hexDerBytes = KeyUtilsTest.hexToBytes(hexDer);
		byte[] pubKeyBytes = KeyUtilsTest.hexToBytes(KeyUtils.getCompressPubKeyFromPem(pem));

		assertTrue(ECKey.verify(msgBytes, hexDerBytes, pubKeyBytes));
	}

	// ************************************
	// *         2nd set of tests         *
	// ************************************

	@Test
	public void testGeneratePem2() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		String testPem = KeyUtils.generatePem();
		assertTrue(testPem.matches(pemPattern));
	}

	@Test
	public void testCompressPubKeyFromPem2() throws IOException {
		String pubKey = KeyUtils.getCompressPubKeyFromPem(pem2);
		assertEquals(pubKeyCompressed2, pubKey);
	}


	@Test
	public void testPrivKeyFromPem2() throws IOException {
		String priKey = KeyUtils.getPrivateKeyFromPem(pem2);
		assertEquals(privateKey2, priKey);			
	}


	@Test
	public void testSinFromPem2() throws NoSuchAlgorithmException, IOException {
		String testSin = KeyUtils.getSinFromPem(pem2);
		assertEquals(sin2, testSin);   
	}


	@Test
	public void testSigning2() throws IOException, NoSuchAlgorithmException {
		String hexDer = KeyUtils.signMsgWithPem(msg2, pem2);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(msg2.getBytes("UTF-8")); 
		byte[] msgBytes = md.digest();

		byte[] hexDerBytes = KeyUtilsTest.hexToBytes(hexDer);
		byte[] pubKeyBytes = KeyUtilsTest.hexToBytes(KeyUtils.getCompressPubKeyFromPem(pem2));

		assertTrue(ECKey.verify(msgBytes, hexDerBytes, pubKeyBytes));
	}
	
	
	// Private methods copied from KeyUtils for testing the signature here
	
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
	
	private static int getHexVal(char hex)
	{
		int val = (int)hex;
		return val - (val < 58 ? 48 : (val < 97 ? 55 : 87));
	}

}



