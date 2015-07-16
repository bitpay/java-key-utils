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

	final String pem2 = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIGxJZ/H8cJ6rTejjSL/jg+bmvV6P1bh+oQyElxQfztbqoAcGBSuBBAAK\noUQDQgAEAJiwjbqNtzFoL1HvdRRWQgzEDMwN08PJC2bdqTmGHmZ0FfX5t+pOy4Ai\nOuluV4VbIFWZ64onGHXu0A7ejWY/jg==\n-----END EC PRIVATE KEY-----\n";
	final String pubKeyCompressed2 = "020098B08DBA8DB731682F51EF751456420CC40CCC0DD3C3C90B66DDA939861E66";
	final String privateKey2 = "6C4967F1FC709EAB4DE8E348BFE383E6E6BD5E8FD5B87EA10C8497141FCED6EA";
	final String sin2 = "Tf3goZsaKmYcD5YWGv2bz2yifbrM3ogkbMd";
	final String msg2 = "A 2nd test message.";

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

		byte[] hexDerBytes = KeyUtils.hexToBytes(hexDer);
		byte[] pubKeyBytes = KeyUtils.hexToBytes(KeyUtils.getCompressPubKeyFromPem(pem));

		assertTrue(ECKey.verify(msgBytes, hexDerBytes, pubKeyBytes));
	}

	// *******************************************
	// 	2nd set of tests
	// *******************************************

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

		byte[] hexDerBytes = KeyUtils.hexToBytes(hexDer);
		byte[] pubKeyBytes = KeyUtils.hexToBytes(KeyUtils.getCompressPubKeyFromPem(pem2));

		assertTrue(ECKey.verify(msgBytes, hexDerBytes, pubKeyBytes));
	}

}



