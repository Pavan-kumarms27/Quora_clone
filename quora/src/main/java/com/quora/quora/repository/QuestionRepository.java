package com.quora.quora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quora.quora.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
