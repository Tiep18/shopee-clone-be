package com.pt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String token;
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
