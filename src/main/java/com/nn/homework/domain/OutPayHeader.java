package com.nn.homework.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "OutPay_Header")
public class OutPayHeader {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long outpayHeaderId;

  @Column(nullable = false, length = 8)
  private String clntnum;

  @Column(nullable = false, length = 8)
  private String chdrnum;

  @Column(nullable = false, length = 12)
  private String letterType;

  @Column(nullable = false)
  private LocalDate printDate;

  @Column(length = 6)
  private String dataID;

  @Column(length = 80)
  private String clntName;

  @Column(length = 80)
  private String clntAddress;

  @Column
  private LocalDate regDate;

  @Column(precision = 6, scale = 2)
  private BigDecimal benPercent;

  @Column(length = 2)
  private String role1;

  @Column(length = 2)
  private String role2;

  @Column(length = 8)
  private String cownNum;

  @Column(length = 80)
  private String cownName;

  @Column(length = 80)
  private String notice01;

  @Column(length = 80)
  private String notice02;

  @Column(length = 80)
  private String notice03;

  @Column(length = 80)
  private String notice04;

  @Column(length = 80)
  private String notice05;

  @Column(length = 80)
  private String notice06;

  @Column(length = 9)
  private String claimId;

  @Column
  private LocalDate tp2ProcessDate;

}

