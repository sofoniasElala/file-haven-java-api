package com.sofoniaselala.file_haven_java_api.Helpers;

import org.springframework.data.domain.Sort;

public class ColumnSorter {
    public static Sort getSort(String sortByUpdatedAtDirection, String sortByNameDirection){
        Sort.Direction updatedAtDirection = sortByUpdatedAtDirection != null ? sortByUpdatedAtDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC : null;
        Sort.Direction nameDirection =  sortByNameDirection != null ? sortByNameDirection.equals("desc")  ? Sort.Direction.DESC : Sort.Direction.ASC : null;
        Sort sortByUpdatedAt = updatedAtDirection != null ? Sort.by(updatedAtDirection, "updatedAt") : null;
        Sort sortByName = nameDirection != null ? Sort.by(nameDirection, "name") : null;
        Sort sort = sortByName != null ? sortByName : sortByUpdatedAt;

        return sort;
    }
}