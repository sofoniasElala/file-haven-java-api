package com.sofoniaselala.file_haven_java_api.Repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sofoniaselala.file_haven_java_api.Model.File;

@Repository
public interface FileRepository extends CrudRepository<File, Integer> {
}