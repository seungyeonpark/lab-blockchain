package blockchain.util;

import java.security.Signature;
import java.util.Base64;

public class SignatureUtil {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int SIGNATURE_BLOCK_LENGTH = 172;

    public static String generateSignature(String privateKey, String plainText) throws Exception {
        try {
            Signature s = Signature.getInstance(SIGNATURE_ALGORITHM);
            s.initSign(RsaKey.parseBytesToPrivateKey(
                    Base64.getDecoder().decode(privateKey.getBytes())
            ));
            s.update(plainText.getBytes());
            byte[] signature = s.sign();

            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean verifySignature(String publicKey, String plainText, String signature) throws Exception {
        if (signature.length() != SIGNATURE_BLOCK_LENGTH) {
            return false;
        }

        try {
            Signature s = Signature.getInstance(SIGNATURE_ALGORITHM);
            s.initVerify(RsaKey.parseBytesToPublicKey(
                    Base64.getDecoder().decode(publicKey)
            ));
            s.update(plainText.getBytes());

            if (!s.verify(Base64.getDecoder().decode(signature))) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
