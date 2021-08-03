package com.starter.springboot.auth;

import com.starter.springboot.services.OtpService;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

@Description(value = "Resource for generating and validating OTP requests.")
@RestController
@RequestMapping("/api/otp")
public class OTPResourceController {

    private OtpService otpService;

    /**
     * Constructor dependency injector.
     * @param otpService - otp service
     */
    public OTPResourceController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping(value = "generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generateOTP(@RequestParam String key) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException
    {
  
        Map<String, String> response = new HashMap<>(2);

        // generate OTP.
        SimpleEntry<String,Integer> entry = otpService.generateOtp(key);
        Integer otp = entry.getValue();
        String secretkey = entry.getKey();

        if (otp==null)
        {
            response.put("status", "error");
            response.put("message", "OTP can not be generated.");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // success message
        response.put("status", "success");
        response.put("message", "OTP successfully generated.");
        response.put("OTP", ""+otp );
        response.put("secretkey", secretkey);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validateOTP(@RequestParam Integer otp,@RequestParam String key)
    {

        Map<String, Boolean> response = new HashMap<>(2);

        // check authentication
        if (key == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // validate provided OTP.
        Boolean isValid = otpService.validateOTP(key, otp);
        if (!isValid)
        {
           
            response.put("isValid",false);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // success message
        response.put("isValid", true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
