package com.security.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.testing.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{
	

}
