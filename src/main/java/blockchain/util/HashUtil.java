package blockchain.util;

import java.security.MessageDigest;

public class HashUtil {

    public static String applySha256(String input) throws Exception{
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuffer hexString = new StringBuffer();

            for (byte h : hash) {
                String hex = Integer.toHexString(0xff & h);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
