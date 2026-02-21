package ru.vsu.cs.iachnyi_m_a.java.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
    public static String encode(String password) {
        return getEncryptedPassword(password);
    }

    public static boolean matches(String password, String encodedPassword) {
        return getEncryptedPassword(password).equals(encodedPassword);
    }

    private static String getEncryptedPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16)
                        .substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException _) {

        }
        return generatedPassword;
    }
}
