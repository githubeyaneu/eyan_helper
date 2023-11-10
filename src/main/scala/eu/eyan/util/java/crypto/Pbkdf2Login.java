package eu.eyan.util.java.crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Pbkdf2Login {
    /**
     * Calculate the secret key on Android.
     */
    public static String calculatePbkdf2Response(String challenge, String password) {
        String[] challenge_parts = challenge.split("\\$");
        int iter1 = Integer.parseInt(challenge_parts[1]);
        byte[] salt1 = fromHex(challenge_parts[2]);
        int iter2 = Integer.parseInt(challenge_parts[3]);
        byte[] salt2 = fromHex(challenge_parts[4]);
        byte[] hash1 = pbkdf2HmacSha256(password.getBytes(StandardCharsets.UTF_8), salt1, iter1);
        byte[] hash2 = pbkdf2HmacSha256(hash1, salt2, iter2);
        return challenge_parts[4] + "$" + toHex(hash2);
    }

    /**
     * Hex string to bytes
     */
    static byte[] fromHex(String hexString) {
        int len = hexString.length() / 2;
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) Short.parseShort(hexString.substring(i * 2, i *
                    2 + 2), 16);
        }
        return ret;
    }

    /**
     * byte array to hex string
     */
    static String toHex(byte[] bytes) {
        StringBuilder s = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            s.append(String.format("%02x", b));
        }
        return s.toString();
    }

    /**
     * Create a pbkdf2 HMAC by appling the Hmac iter times as specified.
     * We can't use the Android-internal PBKDF2 here, as it only accepts
     * char[] arrays, not bytes (for multi-stage hashing)
     */
    static byte[] pbkdf2HmacSha256(final byte[] password, final byte[]
            salt, int iters) {
        try {
            String alg = "HmacSHA256";
            Mac sha256mac = Mac.getInstance(alg);
            sha256mac.init(new SecretKeySpec(password, alg));
            byte[] ret = new byte[sha256mac.getMacLength()];
            byte[] tmp = new byte[salt.length + 4];
            System.arraycopy(salt, 0, tmp, 0, salt.length);
            tmp[salt.length + 3] = 1;
            for (int i = 0; i < iters; i++) {
                tmp = sha256mac.doFinal(tmp);
                for (int k = 0; k < ret.length; k++) {
                    ret[k] ^= tmp[k];
                }
            }
            return ret;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null; // TODO: Handle this properly
        }
    }
}