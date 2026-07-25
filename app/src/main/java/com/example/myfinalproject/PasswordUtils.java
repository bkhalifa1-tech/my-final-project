package com.example.myfinalproject;

import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils {

    public static String newSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.decode(salt, Base64.DEFAULT));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.encodeToString(hashedPassword, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public static boolean matches(String password, String salt, String hash) {
        String newHash = hash(password, salt);
        return newHash.equals(hash);
    }
}
