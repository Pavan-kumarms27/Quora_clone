package com.quora.quora.controller;

import com.quora.quora.dto.AnswerDTO;
import com.quora.quora.model.User;
import com.quora.quora.repository.UserRepository;
import com.quora.quora.service.AnswerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnswerController {

    private final AnswerService answerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * CREATE Answer
     * URL: POST /api/answers
     * Body: { "questionId": 1, "content": "my answer" }
     * authorName will be set from logged-in user
     */
    @PostMapping
    public AnswerDTO createAnswer(@RequestBody AnswerDTO answerDTO,
                                  Principal principal) {
        if (principal != null) {
            answerDTO.setAuthorName(principal.getName());
        }
        return answerService.createAnswer(answerDTO);
    }

    /**
     * READ ALL Answers for a Question
     * URL: GET /api/answers/question/{questionId}
     */
    @GetMapping("/question/{questionId}")
    public List<AnswerDTO> getAnswersForQuestion(@PathVariable Long questionId) {
        return answerService.getAllAnswersForQuestion(questionId);
    }

    /**
     * UPDATE Answer (password required)
     * URL: PUT /api/answers/{id}?password=YourPassword
     * Body: { "questionId": 1, "content": "updated answer" }
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnswer(@PathVariable Long id,
                                          @RequestBody AnswerDTO answerDTO,
                                          @RequestParam("password") String password,
                                          Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // get existing answer
        AnswerDTO existing = answerService.getAnswerById(id); // make sure this exists in service

        // only author can update
        if (!existing.getAuthorName().equals(principal.getName())) {
            return ResponseEntity.badRequest().body("You can only update your own answers");
        }

        // load user & verify password
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        // keep author and questionId same
        answerDTO.setAuthorName(principal.getName());
        answerDTO.setQuestionId(existing.getQuestionId());

        AnswerDTO updated = answerService.updateAnswer(id, answerDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE Answer (password required)
     * URL: DELETE /api/answers/{id}?password=YourPassword
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long id,
                                          @RequestParam("password") String password,
                                          Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        AnswerDTO existing = answerService.getAnswerById(id);

        if (!existing.getAuthorName().equals(principal.getName())) {
            return ResponseEntity.badRequest().body("You can only delete your own answers");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        answerService.deleteAnswer(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
