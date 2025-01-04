package com.sofoniaselala.file_haven_java_api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client() {
        // Automatically picks up credentials from ~/.aws/credentials
        return S3Client.builder()
                .region(Region.US_EAST_2) 
                .credentialsProvider(ProfileCredentialsProvider.create()) // Use 'default'
                .build();
    }
}