package com.nn.homework.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

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
  @Setter(AccessLevel.NONE)
  private String mailAddress;

  public void setMailAddress(String mailAddress) {
    int maxLength = 50; // Maximum length for MAIL_ADDRESS column
    if (mailAddress != null && mailAddress.length() > maxLength) {
      this.mailAddress = mailAddress.substring(0, maxLength);
    } else {
      this.mailAddress = mailAddress;
    }
  }
}

