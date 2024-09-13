package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exception.NoStudentAgeException;
import ru.hogwarts.school.exception.NullAgeException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping // POST http://localhost:8090/student
    public ResponseEntity<Student> createStudent(@RequestBody Student student) { //Для записи студентов по телу через свагер(постман)
        Student student1 = studentService.createStudent(student);
        return ResponseEntity.ok(student1); //В свагере увидим созданный объект в JSON
    }

    @GetMapping("{id}") // GET http://localhost:8090/student/1
    public ResponseEntity <Student> findStudent(@PathVariable Long id) { //Для получения студента из Мапы по индексу через свагер(постман)
        if (studentService.findStudent(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //Выводим 404 по варианту 1
        }
        return ResponseEntity.ok(studentService.findStudent(id));  //В свагере увидим выбранный объект в JSON
    }

    @PutMapping // PUT http://localhost:8090/student
    public ResponseEntity<Student> editStudent(@RequestBody Student student) { //Для редактирования студентов в Мапе через свагер(постман).
        // Если такого студента в Мапе нет, то выйдет 404
        if (studentService.editStudent(student) == null) {
            return ResponseEntity.notFound().build(); //Если студента с этим Id не найдено, то выскочит по умолчанию 404. Вариант 2а
        }
        return ResponseEntity.ok(studentService.editStudent(student)); //В свагере увидим отредактированный объект в JSON
    }

    @DeleteMapping("{id}")  // DELETE http://localhost:8090/student/1
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) { //Для удаления студента по id из Мапы через Свагер
        Student student = studentService.deleteStudent(id);
        if (student == null) {
            return ResponseEntity.status(405).build(); //Если студента с этим Id нет, то выскочит 405. Вариант 3
        }
        return ResponseEntity.ok(student); //При удалении студента по выбранному Id по умолчанию пропишется 404.
    }

    @GetMapping() // GET http://localhost:8090/student
    public ResponseEntity<Collection<Student>> getAllStudent() { //Для вывода всех студентов Мапы через свагер(постман)
        return ResponseEntity.ok(studentService.getAllStudent());
    }

    @GetMapping(path = "/get/by-age")  //ДЗ-3.2
        //localhost:8090/student/searchAge?age=22
    String getStudentByAge(@RequestParam(required = false) Integer age) {
        try {
            return "Студенты с таким возрастом: " + studentService.getStudentByAge(age);
        } catch (NullAgeException exc) {
            return "Параметр адреса не задан";
        } catch (NoStudentAgeException exception) {
            return "Студентов с таким возрастом в коллекции нет";
        }
    }

    // ДЗ-3.4, шаг 1.1
    @GetMapping("/age") // GET http://localhost:8090/student/age?min=22&max=23
    public ResponseEntity<List<Student>> findByAgeStudent(@RequestParam Integer min, @RequestParam(required = false) Integer max) {
        if (studentService.findByAgeBetween(min, max).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //Выводим 404 по варианту 1;
        }
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max)); //Вызов стандартногот метода поиска студентов по отрезку возраста
    }

    //ДЗ-3.4, шаг 4.2
    @GetMapping("/faculty") // GET http://localhost:8082/student/faculty?faculty_id=АО
    public ResponseEntity<Collection<Student>> findStudentsByFaculty_name(String faculty_name) {
        return ResponseEntity.ok(studentService.findStudentsByFaculty_name(faculty_name));
    }
}

