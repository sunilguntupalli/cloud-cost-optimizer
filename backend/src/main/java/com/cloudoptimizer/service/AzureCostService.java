package com.cloudoptimizer.service;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.cloudoptimizer.model.CostSnapshot;
import com.cloudoptimizer.repository.CostSnapshotRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
public class AzureCostService {
  private final CostSnapshotRepository snapshots; private final ObjectMapper mapper; private final RestClient client=RestClient.create();
  @Value("${azure.subscription-id:}") private String subscriptionId;
  public AzureCostService(CostSnapshotRepository snapshots,ObjectMapper mapper){this.snapshots=snapshots;this.mapper=mapper;}
  public Map<String,Object> latest(){return snapshots.findTopByOrderByObservedAtDesc().<Map<String,Object>>map(this::toMap).orElse(Map.of("source","demo","amount",0,"currency","USD"));}
  public Map<String,Object> sync(){
    if(subscriptionId.isBlank()) return Map.of("status","DEMO","message","Set AZURE_SUBSCRIPTION_ID and Azure identity variables to import live cost data.","snapshot",latest());
    try {String token=new DefaultAzureCredentialBuilder().build().getToken(new TokenRequestContext().addScopes("https://management.azure.com/.default")).block().getToken(); LocalDate today=LocalDate.now(ZoneOffset.UTC),start=today.withDayOfMonth(1); Map<String,Object> q=Map.of("type","ActualCost","timeframe","Custom","timePeriod",Map.of("from",start+"T00:00:00Z","to",today+"T23:59:59Z"),"dataset",Map.of("granularity","None","aggregation",Map.of("totalCost",Map.of("name","PreTaxCost","function","Sum")))); String body=client.post().uri("https://management.azure.com/subscriptions/{id}/providers/Microsoft.CostManagement/query?api-version=2023-11-01",subscriptionId).contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+token).body(mapper.writeValueAsString(q)).retrieve().body(String.class); JsonNode value=mapper.readTree(body).path("properties").path("rows").path(0).path(0); BigDecimal amount=value.isNumber()?value.decimalValue():BigDecimal.ZERO; CostSnapshot snapshot=snapshots.save(new CostSnapshot(Instant.now(),YearMonth.now().toString(),"USD","azure-cost-management",amount)); return Map.of("status","SYNCED","snapshot",toMap(snapshot));
    }catch(Exception e){return Map.of("status","FAILED","message","Azure Cost Management query failed: "+e.getClass().getSimpleName(),"snapshot",latest());}
  }
  public void seed(BigDecimal amount){if(snapshots.count()==0)snapshots.save(new CostSnapshot(Instant.now(),YearMonth.now().toString(),"USD","demo",amount));}
  private Map<String,Object> toMap(CostSnapshot s){return Map.of("amount",s.getAmount(),"currency",s.getCurrency(),"period",s.getPeriod(),"source",s.getSource(),"observedAt",s.getObservedAt().toString());}
}
