package com.sofoniaselala.file_haven_java_api.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sofoniaselala.file_haven_java_api.Model.File;

@Repository
public interface FileRepository extends CrudRepository<File, Integer> {
    Optional<File> findById(Integer id);
    List<File> findAllByParentFolder_Id(Integer id, Sort sort);
}