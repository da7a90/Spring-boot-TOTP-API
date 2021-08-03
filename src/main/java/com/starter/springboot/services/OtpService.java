package com.starter.springboot.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

@Description(value = "Service responsible for handling OTP related functionality.")
@Service
public class OtpService {

    private final Logger LOGGER = LoggerFactory.getLogger(OtpService.class);

    private OtpGenerator otpGenerator;

    /**
     * Constructor dependency injector
     * @param otpGenerator - otpGenerator dependency
     */
    public OtpService(OtpGenerator otpGenerator)
    {
        this.otpGenerator = otpGenerator;
    }

    /**
     * Method for generate OTP number
     *
     * @param key - provided key (username in this case)
     * @return integer value (true|false)
     */
    public SimpleEntry<String, Integer> generateOtp(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException
    {
        // generate otp
        SimpleEntry<String, Integer> otp = otpGenerator.generateOTP(key);
        Integer otpValue = otp.getValue();
        if (otpValue == -1)
        {
            LOGGER.error("OTP generator is not working...");
            return  null;
        }

        LOGGER.info("Generated OTP: {}", otpValue);

         return otp;
    }

    /**
     * Method for validating provided OTP
     *
     * @param key - provided key
     * @param otpNumber - provided OTP number
     * @return boolean value (true|false)
     */
    public Boolean validateOTP(String key, Integer otpNumber)
    {
        LOGGER.info("received key: {}", key);
        // get OTP from cache
        Integer cacheOTP = otpGenerator.getOPTByKey(key);
        if (cacheOTP!=null && cacheOTP.equals(otpNumber))
        {
            otpGenerator.clearOTPFromCache(key);
            return true;
        }
        return false;
    }
}
