package com.coresaken.jobportal.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_detail")
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "first_name", length = 30)
    String firstName;

    @Column(name = "last_name", length = 30)
    String lastName;

    @Column(name = "created_at")
    Date createdAt;

    @Column(name = "last_login")
    Date lastLogin;
}