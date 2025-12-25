package com.quora.quora.controller;

import com.quora.quora.dto.QuestionDTO;
import com.quora.quora.service.QuestionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // optional, helpful if you call from JS/another origin
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO,
                                      Principal principal) {
        if (principal != null) {
            questionDTO.setAuthorName(principal.getName());
        }
        return questionService.createQuestion(questionDTO);
    }

    /**
     * READ ALL Questions
     * URL: GET /api/questions
     */
    @GetMapping
    public List<QuestionDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    /**
     * READ ONE Question by id
     * URL: GET /api/questions/{id}
     */
    @GetMapping("/{id}")
    public QuestionDTO getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    /**
     * UPDATE Question
     * URL: PUT /api/questions/{id}
     * Body: { "title": "...", "content": "..." }
     */
    @PutMapping("/{id}")
    public QuestionDTO updateQuestion(@PathVariable Long id,
                                      @RequestBody QuestionDTO questionDTO,
                                      Principal principal) {
        if (principal != null) {
            // you can decide if you want to overwrite author or not
            questionDTO.setAuthorName(principal.getName());
        }
        return questionService.updateQuestion(id, questionDTO);
    }

    /**
     * DELETE Question
     * URL: DELETE /api/questions/{id}
     */
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }
}
