package com.cloudoptimizer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Recommendation {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private String title; private String category; private String resourceName; private String impact; private String status;
  private BigDecimal monthlySavings;
  @Column(length=1000) private String description;
  public Recommendation() {}
  public Recommendation(String title,String category,String resourceName,String impact,String status,BigDecimal monthlySavings,String description){this.title=title;this.category=category;this.resourceName=resourceName;this.impact=impact;this.status=status;this.monthlySavings=monthlySavings;this.description=description;}
  public Long getId(){return id;} public String getTitle(){return title;} public String getCategory(){return category;} public String getResourceName(){return resourceName;} public String getImpact(){return impact;} public String getStatus(){return status;} public BigDecimal getMonthlySavings(){return monthlySavings;} public String getDescription(){return description;}
  public void setStatus(String status){this.status=status;}
}
