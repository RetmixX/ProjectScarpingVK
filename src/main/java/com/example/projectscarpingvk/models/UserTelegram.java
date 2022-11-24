package com.example.projectscarpingvk.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "users_telegram")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTelegram {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "domain")
    private String domain;
}
