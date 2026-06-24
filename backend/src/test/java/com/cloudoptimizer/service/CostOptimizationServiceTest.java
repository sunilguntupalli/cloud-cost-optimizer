package com.cloudoptimizer.service;

import com.cloudoptimizer.model.Recommendation;
import com.cloudoptimizer.repository.CloudResourceRepository;
import com.cloudoptimizer.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CostOptimizationServiceTest {
  private final CloudResourceRepository resources=mock(CloudResourceRepository.class);
  private final RecommendationRepository recommendations=mock(RecommendationRepository.class);
  private final CostOptimizationService service=new CostOptimizationService(resources,recommendations,mock(AzureCostService.class));

  @Test void changesARecommendationToAnApprovedWorkflowStatus(){
    Recommendation recommendation=new Recommendation("Right-size","Compute","vm-1","High","OPEN",new BigDecimal("120"),"Low utilization");
    when(recommendations.findById(42L)).thenReturn(Optional.of(recommendation)); when(recommendations.save(recommendation)).thenReturn(recommendation);
    assertEquals("IN_PROGRESS",service.updateStatus(42L,"in_progress").getStatus());
  }
  @Test void rejectsAnUnknownWorkflowStatus(){
    ResponseStatusException error=assertThrows(ResponseStatusException.class,()->service.updateStatus(42L,"approved"));
    assertEquals(400,error.getStatusCode().value());
  }
}
