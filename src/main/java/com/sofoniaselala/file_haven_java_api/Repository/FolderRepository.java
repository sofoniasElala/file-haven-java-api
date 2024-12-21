package com.sofoniaselala.file_haven_java_api.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sofoniaselala.file_haven_java_api.Model.Folder;


@Repository
public interface FolderRepository extends CrudRepository<Folder, Integer> {
    Optional<Folder> findByIdAndUser_Id(Integer id, Integer user);
    List<Folder> findAllByParentFolder_Id(Integer id, Sort sort);
}