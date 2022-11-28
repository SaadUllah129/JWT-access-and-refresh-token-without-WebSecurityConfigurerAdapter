package com.security.testing.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.security.testing.entity.Student;
import com.security.testing.service.StudentService;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {
	
	private StudentService service;
	
	public StudentManagementController(StudentService service) {
		super();
		this.service = service;
	}


	@GetMapping
	public List<Student> getAllStudents(){
		return service.getAllStudents();
	}
	
	@PostMapping
	public String registerNewStudent(@RequestBody Student student) {
		service.saveStudent(student);
		return "Registered a new student";
	}
	
	@PutMapping(path = "{studentId}")
	public void updateStudent(@PathVariable Long studentId, @RequestBody Student student) {
		service.updateStudent(student);
	}
	
	@DeleteMapping(path = "{studentId}")
	public void deleteStudent(@PathVariable Long studentId) {
		service.deleteStudent(studentId);
	}

}
