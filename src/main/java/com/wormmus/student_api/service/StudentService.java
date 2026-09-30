//StudentService
package com.wormmus.student_api.service;

import com.wormmus.student_api.dto.StudentRequest;
import com.wormmus.student_api.dto.StudentResponse;
import com.wormmus.student_api.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.wormmus.student_api.entity.Student;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.wormmus.student_api.exception.*;

import java.util.List;

@Service
public class StudentService {


    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<StudentResponse> getAllStudents(int page, int size, String sortBy, String direction) {

        Sort sort = createSort(sortBy, direction);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> students = studentRepository.findAll(pageable);

        return students.map(this::mapToStudentResponse);
    }

    public StudentResponse getStudentById(Long id){
        Student student = studentRepository.findById(id).orElseThrow(()-> new StudentNotFoundException("Student with ID " + id + " not found"));

        return mapToStudentResponse(student);
    }

    public void deleteStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        studentRepository.delete(student);
    }

    public StudentResponse addNewStudent(StudentRequest studentRequest) {
        Student student = new Student();
        mapRequestToStudent(studentRequest, student);

        if (studentRepository.existsByEmail(student.getEmail()))
            throw new EmailAlreadyExistsException("Student with email " + student.getEmail() + " already exists");
        Student savedStudent = studentRepository.save(student);

        return mapToStudentResponse(savedStudent);
    }

    public StudentResponse updateStudent(StudentRequest studentRequest, Long id) {

        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student with ID " + id + " not found"));

        if (studentRepository.existsByEmail(studentRequest.getEmail())
                && !student.getEmail().equals(studentRequest.getEmail())) {

            throw new EmailAlreadyExistsException("Email is already taken by another student.");
        }

        mapRequestToStudent(studentRequest, student);

        Student savedStudent = studentRepository.save(student);

        return mapToStudentResponse(savedStudent);
    }

    public Page<StudentResponse> searchStudents(String query, int page, int size, String sortBy, String direction) {

        Sort sort = createSort(sortBy, direction);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> students = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query,
                query,
                query,
                pageable

        );

        return students.map(this::mapToStudentResponse);
    }

    private StudentResponse mapToStudentResponse(Student student) {

        StudentResponse studentResponse = new StudentResponse();

        studentResponse.setId(student.getId());
        studentResponse.setFirstName(student.getFirstName());
        studentResponse.setLastName(student.getLastName());
        studentResponse.setEmail(student.getEmail());
        studentResponse.setAge(student.getAge());

        return studentResponse;
    }

    private void mapRequestToStudent(StudentRequest studentRequest, Student student){

        student.setEmail(studentRequest.getEmail());
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setAge(studentRequest.getAge());

    }

    private Sort createSort(String sortBy, String direction) {

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new InvalidSortDirectionException(
                    "Sort direction must be either 'asc' or 'desc'"
            );
        }

        if (!sortBy.equals("firstName")
                && !sortBy.equals("lastName")
                && !sortBy.equals("id")
                && !sortBy.equals("email")
                && !sortBy.equals("age")) {
            throw new InvalidSortFieldException(
                    "You can only sort by 'firstName', 'lastName', 'id', 'email', or 'age'"
            );
        }

        Sort.Direction directionEnum = Sort.Direction.fromString(direction);

        return Sort.by(directionEnum, sortBy);
    }


}
