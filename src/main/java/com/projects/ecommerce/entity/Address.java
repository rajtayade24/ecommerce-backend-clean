package com.projects.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(length = 100)

    private String line1;
    
    private String line2;

    private String city;
    private String state;
    private String pincode;

    private boolean primaryAddress;

    private String country = "INDIA";
    private String phone;
    private String label; // home/work

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;
}