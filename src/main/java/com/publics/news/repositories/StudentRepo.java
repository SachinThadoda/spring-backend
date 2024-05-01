package com.publics.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.publics.news.models.Student;

public interface StudentRepo extends JpaRepository<Student, Long> {

}
