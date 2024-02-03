package com.nn.homework.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Policy")
public class Policy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 8)
  private String chdrnum;

  @Column(nullable = false, length = 8)
  private String cownnum;

  @Column(length = 50)
  private String ownerName;

  @Column(length = 8)
  private String lifcNum;

  @Column(length = 50)
  private String lifcName;

  @Column(length = 3)
  private String aracde;

  @Column(length = 5)
  private String agntnum;

  @Column(length = 50)
  private String mailAddress;
}

