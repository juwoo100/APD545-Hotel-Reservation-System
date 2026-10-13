package ca.seneca.application.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Cross-cutting concern: BCrypt password hashing.
 * Work factor 12 — strong enough for production.
 */
public class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String plaintext) {
        return BCrypt.withDefaults().hashToString(12, plaintext.toCharArray());
    }

    public static boolean verify(String plaintext, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plaintext.toCharArray(), hash);
        return result.verified;
    }
}
