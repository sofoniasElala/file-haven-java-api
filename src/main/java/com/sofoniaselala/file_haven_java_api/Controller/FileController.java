package com.sofoniaselala.file_haven_java_api.Controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Controller.DTOs.CreateFolderRequest;
import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Helpers.FileConverter;
import com.sofoniaselala.file_haven_java_api.Model.File;
import com.sofoniaselala.file_haven_java_api.Model.Folder;
import com.sofoniaselala.file_haven_java_api.Model.User;
import com.sofoniaselala.file_haven_java_api.Repository.FileRepository;
import com.sofoniaselala.file_haven_java_api.Services.S3Service;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/files")
public class FileController {
    private final FileRepository fileRepository;
    private final S3Service s3Service;

    public FileController(final FileRepository fileRepository, S3Service s3Service){
        this.fileRepository = fileRepository;
        this.s3Service = s3Service;
    }

    @GetMapping("/{fileId}")
    @ResponseStatus(HttpStatus.FOUND)
    public Map<String, Object> getFile(@PathVariable("fileId") Integer fileId) {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        Optional<File> fileOptional = this.fileRepository.findByIdAndUser_Id(fileId, user.getId());
        if(!fileOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File does not exist.");

        File file = fileOptional.get();
        responseBody.put("success", true);
        responseBody.put("file", file);
        return responseBody;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public  Map<String, Object> createFile(@RequestParam("file") MultipartFile uploadedFile, @RequestParam("folderId") String folderId) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails authenticatedUser = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Folder parentFolder = new Folder();

        if(folderId.equals("null")) parentFolder = null;
        else  parentFolder.setId(Integer.parseInt(folderId));

        User user = new User();
        user.setId(authenticatedUser.getId());
        File file = new File(
            uploadedFile.getOriginalFilename(), 
            uploadedFile.getSize(), 
            uploadedFile.getContentType(), 
            "https://file-haven-files.s3.us-east-2.amazonaws.com/users/" + authenticatedUser.getId() + "/" + uploadedFile.getOriginalFilename(), 
            authenticatedUser.getId() + "/" + uploadedFile.getOriginalFilename(),
            user,
            parentFolder
            );
        File newFile = this.fileRepository.save(file);

        s3Service.uploadFile(uploadedFile.getOriginalFilename(), authenticatedUser.getId(), FileConverter.convertMultipartFileToFile(uploadedFile));
        responseBody.put("success", true);
        responseBody.put("file", newFile);
        return responseBody;
    }
    

    @PutMapping("/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> updateFile(@PathVariable("fileId") Integer fileId, @Valid @RequestBody CreateFolderRequest updateFileRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        File oldFile = this.fileRepository.findByIdAndUser_Id(fileId, user.getId()).get();
        File updatedFileObject = oldFile;
        s3Service.renameFile(oldFile.getName(), user.getId(), updateFileRequest.name());
        updatedFileObject.setName(updateFileRequest.name());
        updatedFileObject.setStorage_url("https://file-haven-files.s3.us-east-2.amazonaws.com/users/" + user.getId() + "/" + updateFileRequest.name());
        updatedFileObject.setStorage_path(+ user.getId() + "/" + updateFileRequest.name());
        updatedFileObject.setUpdatedAt(Timestamp.from(Instant.now().plus(5, ChronoUnit.HOURS))); //current time in UTC 
        File updatedFile = this.fileRepository.save(updatedFileObject);

        responseBody.put("success", true);
        responseBody.put("file", updatedFile);
        
        return responseBody;
    }

    @GetMapping("/{fileName}/download")
    @ResponseStatus(HttpStatus.FOUND)
    public Map<String, Object> download(@PathVariable("fileName") String fileName) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        Optional<File> fileOptional = this.fileRepository.findByNameAndUser_Id(fileName, user.getId());
        if(!fileOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File does not exist.");

        Map<String, Object> base64FileData = s3Service.downloadFile(fileName, user.getId());
        responseBody.put("success", true);
        responseBody.put("base64File", base64FileData.get("base64"));
        responseBody.put("type", base64FileData.get("type"));
        return responseBody;
    }

    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteFile(@PathVariable("fileId") Integer fileId){
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        File fileToDelete = this.fileRepository.findByIdAndUser_Id(fileId, user.getId()).get();
        s3Service.deleteFile("users/" + user.getId() + "/" + fileToDelete.getName());
        this.fileRepository.deleteById(fileId);
        responseBody.put("success", true);
        return responseBody;
    }
}