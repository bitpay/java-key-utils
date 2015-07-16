import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
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

import com.google.bitcoin.core.Base58;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ECKey.ECDSASignature;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Utils;

public class KeyUtils {
	
	// used in private methods at bottom of this file
	final private static char[] hexArray = "0123456789abcdef".toCharArray();
	
	public static String generatePem() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
	
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
		kpGenerator.initialize(ecSpec, new SecureRandom());
		KeyPair pair = kpGenerator.generateKeyPair();  // format of keys = PKCS#8, keys are in hex
		
		StringWriter strWriter = new StringWriter();
		JcaPEMWriter pemWriter = new JcaPEMWriter(strWriter);
		pemWriter.writeObject(pair);
		pemWriter.close();
		
		String pem = strWriter.toString();
		return pem;
	}
	
	public static String getCompressPubKeyFromPem(String pem) throws IOException {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
		
		StringReader strReader = new StringReader(pem);
		PEMParser pemParser = new PEMParser(strReader);
		Object keyObject = pemParser.readObject();
		pemParser.close();
		
		KeyPair keys = converter.getKeyPair((PEMKeyPair) keyObject);
		
		String fullPublicKey = keys.getPublic().toString();
		int indexX = fullPublicKey.indexOf("X: ") + 3; 
		int indexY = fullPublicKey.indexOf("Y: ") + 3; 
		
		String xCoord = fullPublicKey.substring(indexX, indexX + 64);
		xCoord = xCoord.replaceAll("\n", "").replaceAll(" ", ""); // remove unnecessary whitespace 
		String xCoord64 = checkHas64(xCoord); // make sure x-coordinate is 64 chars long
		
		// only need the end of the y-coordinate
		String yCoordEnd = fullPublicKey.substring(indexY+60).toUpperCase();
		yCoordEnd = yCoordEnd.replaceAll("\n", "").replaceAll(" ", ""); // remove unnecessary whitespace
		
		// Only need final digit of Y-coordinate to check if even or odd
		// for prefix to compressed public key
		Long yFinalNumber = Long.parseLong(yCoordEnd.substring(yCoordEnd.length()-1), 16);
		
		String prefix;
		if (yFinalNumber % 2 == 1) {
			prefix = "03"; // put 03 prefix if y is odd
		}
		else {
			prefix = "02"; // put 02 prefix if y is even
		}
		
		return prefix + xCoord64.toUpperCase();
		
	}
	
	public static String getPrivateKeyFromPem(String pem) throws IOException {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
		
		StringReader strReader = new StringReader(pem);
		PEMParser pemParse = new PEMParser(strReader);
		Object obj = pemParse.readObject();
		pemParse.close();
		
		KeyPair kp = converter.getKeyPair((PEMKeyPair) obj);
		
		String fullPriKey = kp.getPrivate().toString();
		int startKey = fullPriKey.indexOf("S: ") + 3;
		String privateKey = fullPriKey.substring(startKey, startKey + 64);
		
		return privateKey.toUpperCase();
	}
	
	public static String getSinFromPem(String pem) throws NoSuchAlgorithmException, IOException {
		
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
		byte[] bytesPubKey4 = Utils.doubleDigest(bytesPubKey3);
		
		// Step 5
		// Substring of first 4 bytes (first 8 chars)
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
	
	
	public static String signMsgWithPem(String msg, String pem) throws IOException {
		String pubKey = getCompressPubKeyFromPem(pem);
		byte[] pubKeyBytes = hexToBytes(pubKey);
		String privKey = getPrivateKeyFromPem(pem);
		
		ECKey key = new ECKey(new BigInteger(privKey, 16), pubKeyBytes, true);
		
		return sign(key, msg);
	}
	
	private static String sign(ECKey key, String input) {
        byte[] data = input.getBytes();

        Sha256Hash hash = Sha256Hash.create(data);
        ECDSASignature sig = key.sign(hash, null);

        byte[] bytes = sig.encodeToDER();

        return bytesToHex(bytes);
    }
	
	// *******************************************
	// *         Private Methods Section         *
	// *******************************************
	
	private static int getHexVal(char hex)
    {
        int val = (int)hex;
        return val - (val < 58 ? 48 : (val < 97 ? 55 : 87));
    }
	
	
	public static byte[] hexToBytes(String hex) throws IllegalArgumentException
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
	

}





