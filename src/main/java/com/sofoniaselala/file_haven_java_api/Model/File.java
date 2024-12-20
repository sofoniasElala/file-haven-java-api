package com.sofoniaselala.file_haven_java_api.Model;

import java.sql.Timestamp;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

//the following annotations generate Setter and Getter methods for each field
@Setter
@Getter

@Entity
@Table(name="file", uniqueConstraints=@UniqueConstraint(columnNames={"name"}))
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto increment strategy
    @Setter(AccessLevel.NONE)
    @Column(name="id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "createdAt")
    private Timestamp createdAt;

    @Column(name = "updatedAt")
    private Timestamp updatedAt;

    @Column(name = "type")
    private String type;

    @Column(name = "storage_url")
    private String storage_url;

    @Column(name = "storage_path")
    private String storage_path;

    @Column(name = "size")
    private long size;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer user;

    @ManyToOne
    @JoinColumn(name="folder_id", nullable=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer parentFolder;
}