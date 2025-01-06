package com.sofoniaselala.file_haven_java_api.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
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
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Controller.DTOs.CreateFolderRequest;
import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Helpers.ColumnSorter;
import com.sofoniaselala.file_haven_java_api.Model.File;
import com.sofoniaselala.file_haven_java_api.Model.Folder;
import com.sofoniaselala.file_haven_java_api.Model.User;
import com.sofoniaselala.file_haven_java_api.Repository.FileRepository;
import com.sofoniaselala.file_haven_java_api.Repository.FolderRepository;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/folders")
public class FolderController {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;

    public FolderController(final FolderRepository folderRepository, final FileRepository fileRepository){
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
    }

    @GetMapping("/{folderId}")
    @ResponseStatus(HttpStatus.FOUND)
    public Map<String, Object> getFolder(@PathVariable("folderId") Integer folderId, @RequestParam Map<String, String> params) {
        Map<String, Object> responseBody = new HashMap<>();
        AppUserDetails user = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //authenticated user
        Sort sort = ColumnSorter.getSort(params.get("sortByUpdatedAt"), params.get("sortByName"));
        Optional<Folder> parentFolder = this.folderRepository.findByIdAndUser_Id(folderId, user.getId());
        if(!parentFolder.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder does not exist.");

        List<Folder> folders = this.folderRepository.findAllByUser_IdAndParentFolder_Id(user.getId(), folderId, sort);
        List<File> files = this.fileRepository.findAllByUser_IdAndParentFolder_Id(user.getId(), folderId, sort);

        responseBody.put("success", true);
        responseBody.put("parentFolderName", parentFolder.get().getName());
        responseBody.put("folder", folders);
        responseBody.put("files", files);

        return responseBody;
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
        Folder newFolderObject = new Folder(folderRequest.name(), userObject, parentFolder);

        Folder newFolder = this.folderRepository.save(newFolderObject);
        responseBody.put("success", true);
        responseBody.put("folder", newFolder);
        
        return responseBody;
    }

    @DeleteMapping("/{folderId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteFolder(@PathVariable("folderId") Integer folderId){
        Map<String, Object> responseBody = new HashMap<>();
        this.folderRepository.deleteById(folderId);
        responseBody.put("success", true);
        return responseBody;
    }
    
    @PutMapping("/{folderId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> updateFolder(@PathVariable("folderId") Integer folderId, @Valid @RequestBody CreateFolderRequest updateFolderRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        Folder oldFolder = this.folderRepository.findById(folderId).get();
        Folder updatedFolderObject = new Folder(updateFolderRequest.name(), oldFolder.getUser(), oldFolder.getParentFolder());
        updatedFolderObject.setId(folderId);
        updatedFolderObject.setCreatedAt(oldFolder.getCreatedAt());
        Folder updatedFolder = this.folderRepository.save(updatedFolderObject);

        responseBody.put("success", true);
        responseBody.put("updatedFolder", updatedFolder);
        
        return responseBody;
    }
    
    
}