package com.app.repository;

import com.app.entity.TourCancel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourCancelRepository extends JpaRepository<TourCancel, Integer> {
    Page<TourCancel> findAll(Specification<TourCancel> spec, Pageable pageable);

}

