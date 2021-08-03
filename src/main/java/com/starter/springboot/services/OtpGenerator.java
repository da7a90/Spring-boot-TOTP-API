package com.starter.springboot.services;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.lang.StringBuilder;

import java.nio.charset.Charset;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;

@Description(value = "Service for generating and validating OTP.")
@Service
public class OtpGenerator  {

    private static final Integer EXPIRE_MIN = 5;
    private LoadingCache<String, Integer> otpCache;
    private final Duration duration = Duration.ofMinutes(EXPIRE_MIN);

    /**
     * Constructor configuration.
     */
    public OtpGenerator()
    {
        super();
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    public static SecretKey getKeyFromPassword(String password, String salt,TimeBasedOneTimePasswordGenerator totp)
  throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
    SecretKey originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), totp.getAlgorithm());
    return originalKey;
}

public String generateRandomString() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
      .limit(targetStringLength)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
    return generatedString;
}

public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
    byte[] rawData = secretKey.getEncoded();
    String encodedKey = Base64.getEncoder().encodeToString(rawData);
    return encodedKey;
}

public static SecretKey convertStringToSecretKey(String encodedKey) {
    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    return originalKey;
}

    /**
     * Method for generating OTP and put it in cache.
     *
     * @param key - cache key
     * @return cache value (generated OTP number)
     */
    public SimpleEntry<String, Integer> generateOTP(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException
    {
        final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(duration);

        SecretKey secretkey = getKeyFromPassword(key, generateRandomString(), totp);

        final Instant now = Instant.now();
        int OTP = totp.generateOneTimePassword(secretkey, now);
        String stringKey = convertSecretKeyToString(secretkey);
        otpCache.put(stringKey, OTP);
        SimpleEntry<String, Integer> pair = new SimpleEntry(stringKey,OTP);
      
        return pair;
    }

    /**
     * Method for getting OTP value by key.
     *
     * @param key - target key
     * @return OTP value
     */
    public Integer getOPTByKey(String key)
    {
        return otpCache.getIfPresent(key);
    }

    /**
     * Method for removing key from cache.
     *
     * @param key - target key
     */
    public void clearOTPFromCache(String key) {
        otpCache.invalidate(key);
    }
}
