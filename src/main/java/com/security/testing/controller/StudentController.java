package com.security.testing.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.testing.entity.Student;
import com.security.testing.service.StudentService;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
	
	
	private StudentService service;
	
	
	@GetMapping
	public List<Student> getStudents(){
		return service.getAllStudents();
	}


	public StudentController(StudentService service) {
		super();
		this.service = service;
	}

}
