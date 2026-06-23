package com.cloudoptimizer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class CloudResource {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private String azureResourceId;
  private String name;
  private String resourceType;
  private String region;
  private String status;
  private Integer utilizationPercent;
  private BigDecimal monthlyCost;
  public CloudResource() {}
  public CloudResource(String azureResourceId, String name, String resourceType, String region, String status, Integer utilizationPercent, BigDecimal monthlyCost) { this.azureResourceId=azureResourceId; this.name=name; this.resourceType=resourceType; this.region=region; this.status=status; this.utilizationPercent=utilizationPercent; this.monthlyCost=monthlyCost; }
  public Long getId(){return id;} public String getAzureResourceId(){return azureResourceId;} public String getName(){return name;} public String getResourceType(){return resourceType;} public String getRegion(){return region;} public String getStatus(){return status;} public Integer getUtilizationPercent(){return utilizationPercent;} public BigDecimal getMonthlyCost(){return monthlyCost;}
}
