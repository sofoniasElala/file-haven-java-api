package com.sofoniaselala.file_haven_java_api.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Controller.DTOs.CreateFolderRequest;
import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Model.Folder;
import com.sofoniaselala.file_haven_java_api.Model.User;
import com.sofoniaselala.file_haven_java_api.Repository.FolderRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/folders")
public class FolderController {
    private final FolderRepository folderRepository;

    public FolderController(final FolderRepository folderRepository){
        this.folderRepository = folderRepository;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public Folder getFolder(@PathVariable("id") Integer id) {
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        Optional<Folder> folderOptional = this.folderRepository.findByIdAndUser_Id(id, user.getId());
        if(!folderOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder does not exist.");

        Folder folder = folderOptional.get();
        return folder;
    }

    @PostMapping(path = {"/", "/{folderId}"}) // folderId is optional
    @ResponseStatus(HttpStatus.OK)
    public  Map<String, Object>  createFolder(@PathVariable(required = false) Optional<Integer> folderIdOptional, @Valid @RequestBody CreateFolderRequest folderRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails authenticatedUser = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        Folder parentFolder = null;
        if(folderIdOptional.isPresent()){
            parentFolder = new Folder();
            parentFolder.setId(folderIdOptional.get());
        }
        User userObject = new User();
        userObject.setId(authenticatedUser.getId());
        Folder newFolder = new Folder(folderRequest.name(), userObject, parentFolder);

        this.folderRepository.save(newFolder);
        responseBody.put("success", true);
        
        return responseBody;
    }
    
    
}