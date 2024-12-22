package com.sofoniaselala.file_haven_java_api.Model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

//the following annotations generate Setter and Getter methods for each field
@Setter
@Getter

@Entity
@Table(name="user", uniqueConstraints=@UniqueConstraint(columnNames={"username"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment strategy
    @Setter(AccessLevel.NONE)
    @Column(name="id")
    private Integer id;

     // Default constructor required by JPA
     public User() {}

     // Custom constructor for instantiation by custom code
     public User(String username, String password) {
         this.username = username;
         this.password = password;
     }

    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Folder> folders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<File> files;
}