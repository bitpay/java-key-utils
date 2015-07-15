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
	
		
		final String pem = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEICg7E4NN53YkaWuAwpoqjfAofjzKI7Jq1f532dX+0O6QoAcGBSuBBAAK\noUQDQgAEjZcNa6Kdz6GQwXcUD9iJ+t1tJZCx7hpqBuJV2/IrQBfue8jh8H7Q/4vX\nfAArmNMaGotTpjdnymWlMfszzXJhlw==\n-----END EC PRIVATE KEY-----\n";
		final String pubKeyCompressed = "038D970D6BA29DCFA190C177140FD889FADD6D2590B1EE1A6A06E255DBF22B4017";
		final String privateKey = "283B13834DE77624696B80C29A2A8DF0287E3CCA23B26AD5FE77D9D5FED0EE90";
		final String sin = "TeyN4LPrXiG5t2yuSamKqP3ynVk3F52iHrX";
		final String msg = "This is a test message";
		@Test
		public void testGeneratePem() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
			String pem = KeyUtils.generatePem();
			String pemPattern = "-----BEGIN EC PRIVATE KEY-----\nMHQCA.*SuBBAAK\noUQDQ.*\n.*\n.*END EC PRIVATE KEY-----\n"; // need to give regex pattern
			System.out.println(pem);
			assertTrue(pem.matches(pemPattern));
		}
		
		@Test
		public void testCompressPubKeyFromPem() throws IOException {
			String pubKey = KeyUtils.getCompressPubKeyFromPem(pem);
			System.out.println("Compressed Public Key: " + pubKey);
			assertEquals(pubKeyCompressed, pubKey);
		}
		
		
		@Test
		public void testPrivKeyFromPem() throws IOException {
			String priKey = KeyUtils.getPrivateKeyFromPem(pem);
			System.out.println("Private Key: " + priKey);
			assertEquals(privateKey, priKey);			
		}
		
		
		@Test
		public void testSinFromPem() throws NoSuchAlgorithmException, IOException {
			String testSin = KeyUtils.getSinFromPem(pem);
			System.out.println("Sin: " + testSin);
			assertEquals(sin, testSin);   
		}
		
		
		@Test
		public void testSigning() throws IOException, NoSuchAlgorithmException {
			String hexDer = KeyUtils.signMsgWithPem(msg, pem);
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(msg.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] msgBytes = md.digest();
						    
			byte[] hexDerBytes = KeyUtils.hexToBytes(hexDer);
			byte[] pubKeyBytes = KeyUtils.hexToBytes(KeyUtils.getCompressPubKeyFromPem(pem));
			
			assertTrue(ECKey.verify(msgBytes, hexDerBytes, pubKeyBytes));
			
		}
		
		// test for priv key from pub key?
		// test for pub key from priv key?
		// test for sin from pub key?
		
}
	


