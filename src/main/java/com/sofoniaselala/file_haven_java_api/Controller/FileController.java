package com.sofoniaselala.file_haven_java_api.Controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Model.File;
import com.sofoniaselala.file_haven_java_api.Repository.FileRepository;


@RestController
@RequestMapping("/files")
public class FileController {
    private final FileRepository fileRepository;

    public FileController(final FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public File getFile(@PathVariable("id") Integer id) {
        Optional<File> fileOptional = this.fileRepository.findById(id);
        if(!fileOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File does not exist.");

        File file = fileOptional.get();
        return file;
    }
}