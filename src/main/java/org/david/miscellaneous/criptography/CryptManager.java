package org.david.miscellaneous.criptography;

import com.google.crypto.tink.subtle.Hkdf;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptManager {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final int SALT_LENGTH = 32;
  private static final int DERIVED_KEY_LENGTH = 64;
  private static final String HASH_SALT_SEPARATOR = "::";

  public static String hashPassword(String password) {
    var salt = generateSalt();
    var saltString = Base64.getEncoder().encodeToString(salt);
    try {
      var hashedPassword =
          Hkdf.computeHkdf(
              "HmacSha256",
              password.getBytes(StandardCharsets.UTF_8),
              salt,
              new byte[0],
              DERIVED_KEY_LENGTH);
      var passwordString = Base64.getEncoder().encodeToString(hashedPassword);
      return passwordString + HASH_SALT_SEPARATOR + saltString;
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean verifyPassword(String password, String hashedPassword) {
    var passArray = hashedPassword.split(HASH_SALT_SEPARATOR);
    var expectedHash = Base64.getDecoder().decode(passArray[0]);
    var expectedSalt = Base64.getDecoder().decode(passArray[1]);
    byte[] derivedKey;
    try {
      derivedKey =
          Hkdf.computeHkdf(
              "HmacSha256",
              password.getBytes(StandardCharsets.UTF_8),
              expectedSalt,
              new byte[0],
              DERIVED_KEY_LENGTH);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
    return constantTimeEquals(derivedKey, expectedHash);
  }

  private static byte[] generateSalt() {
    var byteArray = new byte[SALT_LENGTH];
    SECURE_RANDOM.nextBytes(byteArray);
    return byteArray;
  }

  private static boolean constantTimeEquals(byte[] derivedKey, byte[] expectedHash) {
    if (derivedKey.length != expectedHash.length) {
      return false;
    }
    return MessageDigest.isEqual(derivedKey, expectedHash);
  }
}
