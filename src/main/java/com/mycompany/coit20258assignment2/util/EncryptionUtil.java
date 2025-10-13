package com.mycompany.coit20258assignment2.util;

import java.util.Base64;

/**
 * Simple encryption/decryption utility using Base64 encoding.
 * Provides basic data security for passwords and sensitive information.
 * 
 * Note: This is a basic implementation for demonstration purposes.
 * For production systems, use stronger encryption algorithms like AES or BCrypt.
 */
public class EncryptionUtil {
    
    /**
     * Encrypts a plain text string using Base64 encoding
     * 
     * @param plainText The text to encrypt
     * @return Encrypted (encoded) text
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            byte[] encodedBytes = Base64.getEncoder().encode(plainText.getBytes());
            return new String(encodedBytes);
        } catch (Exception e) {
            System.err.println("Error encrypting data: " + e.getMessage());
            return plainText; // Return original if encryption fails
        }
    }
    
    /**
     * Decrypts an encrypted string using Base64 decoding
     * 
     * @param encryptedText The encrypted text to decrypt
     * @return Decrypted (decoded) plain text
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText.getBytes());
            return new String(decodedBytes);
        } catch (Exception e) {
            System.err.println("Error decrypting data: " + e.getMessage());
            return encryptedText; // Return original if decryption fails
        }
    }
    
    /**
     * Checks if a plain text password matches an encrypted password
     * 
     * @param plainPassword The plain text password to check
     * @param encryptedPassword The encrypted password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            return false;
        }
        
        String encryptedInput = encrypt(plainPassword);
        return encryptedInput.equals(encryptedPassword);
    }
}
