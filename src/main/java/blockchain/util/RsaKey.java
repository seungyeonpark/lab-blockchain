package blockchain.util;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKey {

    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final int RSA_KEY_SIZE = 1024;

    private String publicKey;
    private String privateKey;

    public RsaKey() {
        generateKeyPair();
    }

    public static PublicKey parseBytesToPublicKey(byte[] publicKey) throws Exception {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static PrivateKey parseBytesToPrivateKey(byte[] privateKey) throws Exception {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
            generator.initialize(RSA_KEY_SIZE, new SecureRandom());
            KeyPair keyPair = generator.generateKeyPair();

            privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getPublicKeyString() {
        return publicKey;
    }

    public String getPrivateKeyString() {
        return privateKey;
    }
}
