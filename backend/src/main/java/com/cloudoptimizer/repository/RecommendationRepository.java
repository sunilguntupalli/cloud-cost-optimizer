package com.cloudoptimizer.repository;
import com.cloudoptimizer.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {}
