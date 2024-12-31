package com.sofoniaselala.file_haven_java_api.Controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Model.Folder;
import com.sofoniaselala.file_haven_java_api.Repository.FolderRepository;



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
    
}