package com.cloudoptimizer.service;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.cloudoptimizer.model.CloudResource;
import com.cloudoptimizer.model.Recommendation;
import com.cloudoptimizer.repository.CloudResourceRepository;
import com.cloudoptimizer.repository.RecommendationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
public class CostOptimizationService {
  private final CloudResourceRepository resources; private final RecommendationRepository recommendations;
  @Value("${azure.subscription-id:}") private String subscriptionId;
  public CostOptimizationService(CloudResourceRepository resources, RecommendationRepository recommendations){this.resources=resources;this.recommendations=recommendations;}
  @PostConstruct void seed(){ if(resources.count()==0){
    resources.saveAll(List.of(new CloudResource("/subscriptions/demo/resourceGroups/prod/providers/Microsoft.Compute/virtualMachines/payments-vm","payments-vm","Virtual Machine","East US","Running",14,new BigDecimal("482.00")),new CloudResource("/subscriptions/demo/resourceGroups/prod/providers/Microsoft.Compute/virtualMachines/analytics-vm","analytics-vm","Virtual Machine","West US 2","Running",28,new BigDecimal("625.00")),new CloudResource("/subscriptions/demo/resourceGroups/data/providers/Microsoft.Storage/storageAccounts/archive","archive-data","Storage Account","East US","Active",9,new BigDecimal("238.00")),new CloudResource("/subscriptions/demo/resourceGroups/prod/providers/Microsoft.Compute/disks/legacy","legacy-disk","Managed Disk","East US","Unattached",0,new BigDecimal("96.00"))));
    recommendations.saveAll(List.of(new Recommendation("Right-size payments-vm","Compute","payments-vm","High","OPEN",new BigDecimal("215.00"),"Average CPU utilization is 14%. Move from Standard_D8s_v5 to Standard_D4s_v5."),new Recommendation("Delete unattached disk","Storage","legacy-disk","Medium","OPEN",new BigDecimal("96.00"),"This managed disk has not been attached in 35 days."),new Recommendation("Move archive data to cool tier","Storage","archive-data","Medium","OPEN",new BigDecimal("74.00"),"Data has not been accessed in 90 days and is a candidate for cool storage."),new Recommendation("Schedule analytics workload","Compute","analytics-vm","High","IN_PROGRESS",new BigDecimal("188.00"),"Stop the non-production analytics node outside business hours."))); }}
  public List<CloudResource> allResources(){return resources.findAll();}
  public Optional<CloudResource> resource(Long id){return resources.findById(id);}
  public List<Recommendation> allRecommendations(){return recommendations.findAll();}
  public Optional<Recommendation> recommendation(Long id){return recommendations.findById(id);}
  public Recommendation updateStatus(Long id,String status){ Recommendation r=recommendations.findById(id).orElseThrow(); r.setStatus(status.toUpperCase()); return recommendations.save(r);}
  public Map<String,Object> summary(){ BigDecimal spend=resources.findAll().stream().map(CloudResource::getMonthlyCost).reduce(BigDecimal.ZERO,BigDecimal::add); BigDecimal savings=recommendations.findAll().stream().filter(r->!"DISMISSED".equals(r.getStatus())).map(Recommendation::getMonthlySavings).reduce(BigDecimal.ZERO,BigDecimal::add); return Map.of("monthlySpend",spend,"potentialSavings",savings,"resources",resources.count(),"openRecommendations",recommendations.findAll().stream().filter(r->"OPEN".equals(r.getStatus())).count(),"period",YearMonth.now().toString()); }
  public List<Map<String,Object>> trends(){return List.of(point("Jan",1870,220),point("Feb",1760,265),point("Mar",1684,341),point("Apr",1558,412),point("May",1491,498),point("Jun",1441,573));}
  private Map<String,Object> point(String month,int spend,int savings){return Map.of("month",month,"spend",spend,"savings",savings);}
  public Map<String,Object> discover(){ if(subscriptionId.isBlank()) return Map.of("discovered",0,"mode","demo","message","Azure credentials are not configured; demo inventory remains active."); try { TokenCredential credential=new DefaultAzureCredentialBuilder().build(); AzureResourceManager.authenticate(credential, new com.azure.core.management.profile.AzureProfile(com.azure.core.management.AzureEnvironment.AZURE)).withSubscription(subscriptionId).genericResources().list().forEach(r -> resources.save(new CloudResource(r.id(),r.name(),r.type(),r.regionName(),"Discovered",0,BigDecimal.ZERO))); return Map.of("discovered",resources.count(),"mode","azure"); } catch(Exception e){return Map.of("discovered",0,"mode","fallback","message","Azure discovery could not run: "+e.getClass().getSimpleName());} }
}
