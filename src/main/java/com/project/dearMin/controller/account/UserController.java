package com.project.dearMin.controller.account;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @PostMapping("/createUser")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();

        // 유효성 검사
        if (userData.get("email") == null || userData.get("email").isEmpty() ||
                userData.get("password") == null || userData.get("password").isEmpty()) {
            response.put("success", false);
            response.put("message", "Email and password must not be empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(userData.get("email"))
                    .setPassword(userData.get("password"));

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            response.put("success", true);
            response.put("message", "Successfully created user");
            response.put("uid", userRecord.getUid());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
