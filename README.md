

## Running the project
**mvn install** <br>
**mvn spring-boot:run**
<br>

1. Generate OTP <br>
Route: /api/otp/generate <br>
Method: POST <br>
Parameters: key=cleartextString
Output: {otp : 6-digit OTP with a default duration of 5 minutes, secretkey: secret key string generated from the clear text string}

2. Validate OTP <br>
Route: /api/otp/validate
Method: POST <br>
Parameters: otp=otpnumber, secretkey=secretkey
Output: {isValid: Boolean}
<br>
