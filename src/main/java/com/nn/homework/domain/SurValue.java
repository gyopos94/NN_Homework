package com.nn.homework.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Data
@Table(name = "sur_value")
public class SurValue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 8)
  private String chdrnum;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal SurrenderValue;

  @Column(nullable = false, length = 1)
  private String company;

  @Column(length = 3)
  private String currency;

  @Column(length = 10)
  private String validDate;
}

