package unioeste.sd;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Test {
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String password = "abcd1234";
        byte[] bytes = password.getBytes();
        DESKeySpec keySpec = new DESKeySpec(bytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        System.out.println(new String(key.getEncoded()));
		Cipher cipher = Cipher.getInstance("DES");
    }
}
