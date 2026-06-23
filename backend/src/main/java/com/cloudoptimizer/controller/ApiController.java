package com.cloudoptimizer.controller;

import com.cloudoptimizer.model.*;
import com.cloudoptimizer.service.CostOptimizationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/v1") @CrossOrigin(origins="*")
public class ApiController {
 private final CostOptimizationService service; public ApiController(CostOptimizationService service){this.service=service;}
 @GetMapping("/health") Map<String,String> health(){return Map.of("status","UP","service","cloud-cost-optimizer");}
 @GetMapping("/dashboard/summary") Map<String,Object> summary(){return service.summary();}
 @GetMapping("/dashboard/trends") List<Map<String,Object>> trends(){return service.trends();}
 @GetMapping("/resources") List<CloudResource> resources(){return service.allResources();}
 @GetMapping("/resources/{id}") ResponseEntity<CloudResource> resource(@PathVariable Long id){return service.resource(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PostMapping("/resources/discover") Map<String,Object> discover(){return service.discover();}
 @GetMapping("/recommendations") List<Recommendation> recommendations(){return service.allRecommendations();}
 @GetMapping("/recommendations/{id}") ResponseEntity<Recommendation> recommendation(@PathVariable Long id){return service.recommendation(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PatchMapping("/recommendations/{id}/status") Recommendation status(@PathVariable Long id,@RequestBody StatusRequest request){return service.updateStatus(id,request.status());}
 @GetMapping("/recommendations/summary") Map<String,Object> recommendationSummary(){return Map.of("totalSavings",service.allRecommendations().stream().map(Recommendation::getMonthlySavings).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add),"byCategory",Map.of("compute",403,"storage",170));}
 @GetMapping("/reports/overview") Map<String,Object> report(){return Map.of("summary",service.summary(),"generatedAt",java.time.Instant.now().toString(),"recommendations",service.allRecommendations());}
 @GetMapping(value="/reports/export",produces="text/csv") ResponseEntity<String> export(){String rows="resource,type,monthlyCost,utilization\n"+service.allResources().stream().map(r->r.getName()+","+r.getResourceType()+","+r.getMonthlyCost()+","+r.getUtilizationPercent()).reduce("",(a,b)->a+b+"\n");return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=cost-report.csv").body(rows);}
 record StatusRequest(@NotBlank String status) {}
}
