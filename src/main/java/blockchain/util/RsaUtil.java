package blockchain.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Base64;

public class RsaUtil {

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String rsaEncrypt(String publicKey, String plainText) throws Exception {
        String result;

        try {
            Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    RsaKey.parseBytesToPublicKey(
                            Base64.getDecoder().decode(publicKey.getBytes())
                    )
            );

            byte[] inputArray = plainText.getBytes();
            int inputLength = inputArray.length;
            int offset = 0;
            byte[] resultBytes = {};
            byte[] cache = {};

            while (inputLength - offset > 0) {
                if (inputLength - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(inputArray, offset, MAX_ENCRYPT_BLOCK);
                    offset += MAX_ENCRYPT_BLOCK;
                } else {
                    cache = cipher.doFinal(inputArray, offset, inputLength - offset);
                    offset = inputLength;
                }

                resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
                System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            }

            result = Base64.getEncoder().encodeToString(resultBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return result;
    }

    public static String rsaDecrypt(String privateKey, String cipherText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(KEY_FACTORY_ALGORITHM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    RsaKey.parseBytesToPrivateKey(
                            Base64.getDecoder().decode(privateKey.getBytes())
                    )
            );

            byte[] inputArray = cipherText.getBytes();
            int inputLength = inputArray.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache = {};
            int i = 0;

            while (inputLength - offset > 0) {
                if (inputLength - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(inputArray, offset, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(inputArray, offset, inputLength - offset);
                }

                out.write(cache, 0, cache.length);
                offset = ++i + MAX_DECRYPT_BLOCK;
            }

            byte[] decryptedData = out.toByteArray();
            out.close();

            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
