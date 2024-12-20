package com.sofoniaselala.file_haven_java_api.Repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sofoniaselala.file_haven_java_api.Model.Folder;

@Repository
public interface FolderRepository extends CrudRepository<Folder, Integer> {
}