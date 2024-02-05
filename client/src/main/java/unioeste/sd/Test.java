package unioeste.sd;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Test {
    public static void main(String[] args) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher des = Cipher.getInstance("DES");

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        String a = "12345678";
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(a.getBytes()));

        des.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] bytes = des.doFinal("OLAMUNDO AAAA".getBytes());

        String b = "5642345678";
        SecretKey wrongkey = keyFactory.generateSecret(new DESKeySpec(b.getBytes()));

        Cipher des2 = Cipher.getInstance("DES");
        des2.init(Cipher.DECRYPT_MODE, wrongkey);
        byte[] bytes1 = des2.doFinal(bytes);
        System.out.println(new String(bytes1));
    }
}
