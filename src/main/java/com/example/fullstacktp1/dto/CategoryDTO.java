package com.example.fullstacktp1.dto;

import com.example.fullstacktp1.model.Category;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class CategoryDTO {

    private Long id;
    private String name;
    private Date creationDate;
    private boolean isRoot;
    private Set<Category> children;

    // Constructeur
    public CategoryDTO(Long id, String name, Date creationDate, boolean isRoot, Set<Category> children) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.isRoot = isRoot;
        this.children = children;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }
}
