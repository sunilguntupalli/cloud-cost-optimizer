package com.cloudoptimizer.repository;
import com.cloudoptimizer.model.CostSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CostSnapshotRepository extends JpaRepository<CostSnapshot,Long>{ Optional<CostSnapshot> findTopByOrderByObservedAtDesc(); }
