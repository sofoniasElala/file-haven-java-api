package com.sofoniaselala.file_haven_java_api.Helpers;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileConverter {

    public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // Create a temporary file
        File file = File.createTempFile("upload", multipartFile.getOriginalFilename());
        
        // Transfer multipart file data to the file
        multipartFile.transferTo(file);

        // Delete the file on exit
        file.deleteOnExit();

        return file;
    }
}