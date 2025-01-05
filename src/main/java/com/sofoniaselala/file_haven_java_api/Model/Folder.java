package com.sofoniaselala.file_haven_java_api.Model;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

//the following annotations generate Setter and Getter methods for each field
@Setter
@Getter

@Entity
@Table(name="folder", uniqueConstraints=@UniqueConstraint(columnNames={"name"}))
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment strategy
    //@Setter(AccessLevel.NONE)
    @Column(name="id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "createdAt")
    private Timestamp createdAt = Timestamp.from(Instant.now().plus(5, ChronoUnit.HOURS)); //default to current time in UTC 

    @Column(name = "updatedAt")
    private Timestamp updatedAt = Timestamp.from(Instant.now().plus(5, ChronoUnit.HOURS)); //default to current time in UTC 

    @ManyToOne
    @JsonIgnore // this annotation stops the serialization of a column/field. in this case user field and it won't go and get user record in its entirety
    @JoinColumn(name="user_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="folder_id", nullable=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder")
    private Set<Folder> folders;

    @OneToMany(mappedBy = "parentFolder")
    private Set<File> files;

    // Default constructor required by JPA
    public Folder() {}

    // Custom constructor for instantiation by custom code
    public Folder(String name, User user, Folder parentFolder) {
        this.name = name;
        this.user = user;
        this.parentFolder = parentFolder;
    }
}