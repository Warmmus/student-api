//StudentController
package com.wormmus.student_api.controller;

import com.wormmus.student_api.dto.StudentRequest;
import com.wormmus.student_api.dto.StudentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.wormmus.student_api.service.StudentService;

import java.util.List;


@RestController
@Validated
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public Page<StudentResponse> getStudents(@Min(0)@RequestParam(defaultValue = "0") int page,
                                             @Min(1)@Max(100)@RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "id") String sortBy,
                                             @RequestParam(defaultValue = "asc") String direction)
    {
        return studentService.getAllStudents(page, size, sortBy, direction);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id){
        StudentResponse studentResponse = studentService.getStudentById(id);
        return ResponseEntity.ok(studentResponse);
    }

    @GetMapping("/students/search")
    public Page<StudentResponse> searchStudents(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @Max(100)@RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @Size(min = 3, message = "Search query must be at least 3 characters")@NotBlank(message = "Search query must not be blank")@RequestParam String query
    ) {
        return studentService.searchStudents(query, page, size, sortBy, direction);
    }

    @PostMapping("/student")
    public ResponseEntity<StudentResponse> addStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse savedStudent = studentService.addNewStudent(studentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@Valid @RequestBody StudentRequest studentRequest, @PathVariable Long id){
        return ResponseEntity.ok(studentService.updateStudent(studentRequest, id));
    }
}
