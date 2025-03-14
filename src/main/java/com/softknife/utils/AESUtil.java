package com.softknife.utils;

/**
 * @author amatsaylo on 3/13/25
 * @project demo-restapi-test
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    // AES algorithm
    private static final String AES_ALGORITHM = "AES";

    // Secret key (must be 16, 24, or 32 bytes long for AES-128, AES-192, or AES-256 respectively)
    private static final String SECRET_KEY = "YourSecretKey"; // 32 bytes for AES-256

    /**
     * Encrypts a password using AES.
     *
     * @param password The password to encrypt.
     * @return The encrypted password as a Base64-encoded string.
     */
    public static String encrypt(String password, String secretKey) {
        try {
            // Create a secret key from the provided key
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

            // Initialize the cipher for encryption
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // Encrypt the password
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes to Base64 for easy storage/transmission
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    /**
     * Decrypts an encrypted password using AES.
     *
     * @param encryptedPassword The encrypted password as a Base64-encoded string.
     * @return The decrypted password.
     */
    public static String decrypt(String encryptedPassword, final String passKey) {
        try {
            // Create a secret key from the provided key
            SecretKeySpec secretKey = new SecretKeySpec(passKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decode the Base64-encoded encrypted password
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);

            // Decrypt the password
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert the decrypted bytes to a string
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting password", e);
        }
    }
}
