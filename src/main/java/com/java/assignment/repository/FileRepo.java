package com.java.assignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.assignment.domain.File;
import com.java.assignment.domain.UserDetails;

public interface FileRepo extends JpaRepository<File, Long> {

	File findByUserDetails(UserDetails userDetails);

}
