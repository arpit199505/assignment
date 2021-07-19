package com.java.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.assignment.domain.UserDetails;

@Repository
public interface FormRepository extends JpaRepository<UserDetails, Long> {

	UserDetails findByEmail(String email);


}
