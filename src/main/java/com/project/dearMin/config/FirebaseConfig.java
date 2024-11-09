package com.project.dearMin.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FirebaseConfig {

    @Autowired
    private FirebaseProperties firebaseProperties;

    @Bean
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                Map<String, Object> credentialsMap = new HashMap<>();
                credentialsMap.put("type", firebaseProperties.getType());
                credentialsMap.put("project_id", firebaseProperties.getProjectId());
                credentialsMap.put("private_key_id", firebaseProperties.getPrivateKeyId());
                credentialsMap.put("private_key", firebaseProperties.getPrivateKey().replace("\\n", "\n"));
                credentialsMap.put("client_email", firebaseProperties.getClientEmail());
                credentialsMap.put("client_id", firebaseProperties.getClientId());
                credentialsMap.put("auth_uri", firebaseProperties.getAuthUri());
                credentialsMap.put("token_uri", firebaseProperties.getTokenUri());
                credentialsMap.put("auth_provider_x509_cert_url", firebaseProperties.getAuthProviderX509CertUrl());
                credentialsMap.put("client_x509_cert_url", firebaseProperties.getClientX509CertUrl());

                Gson gson = new Gson();
                String jsonCredentials = gson.toJson(credentialsMap);
                ByteArrayInputStream serviceAccount = new ByteArrayInputStream(jsonCredentials.getBytes());

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(firebaseProperties.getProjectId() + ".appspot.com")
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
