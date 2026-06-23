package com.cloudoptimizer.repository;
import com.cloudoptimizer.model.CloudResource;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CloudResourceRepository extends JpaRepository<CloudResource, Long> {}
