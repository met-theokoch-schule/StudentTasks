
package com.example.studenttask.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String openIdSubject; // aus "sub" Claim
    
    private String name; // aus "name" Claim
    private String email; // aus "email" Claim
    private String preferredUsername; // aus "preferred_username" Claim
    private String givenName; // aus "given_name" Claim (Vorname)
    private String familyName; // aus "family_name" Claim (Nachname)
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "user_groups",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();
    
    // Constructors
    public User() {}
    
    public User(String openIdSubject, String name, String email) {
        this.openIdSubject = openIdSubject;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOpenIdSubject() {
        return openIdSubject;
    }
    
    public void setOpenIdSubject(String openIdSubject) {
        this.openIdSubject = openIdSubject;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPreferredUsername() {
        return preferredUsername;
    }
    
    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }
    
    public String getGivenName() {
        return givenName;
    }
    
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    
    public String getFamilyName() {
        return familyName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    public Set<Group> getGroups() {
        return groups;
    }
    
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
    
    // Helper methods
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    public void addGroup(Group group) {
        this.groups.add(group);
    }
    
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }
    
    public boolean isTeacher() {
        return hasRole("ROLE_TEACHER");
    }
    
    public boolean isStudent() {
        return hasRole("ROLE_STUDENT");
    }
}
