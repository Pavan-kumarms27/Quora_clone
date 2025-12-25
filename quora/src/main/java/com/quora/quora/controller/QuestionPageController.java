package com.quora.quora.controller;

import com.quora.quora.dto.AnswerDTO;
import com.quora.quora.dto.QuestionDTO;
import com.quora.quora.model.User;
import com.quora.quora.repository.UserRepository;
import com.quora.quora.service.AnswerService;
import com.quora.quora.service.QuestionService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuestionPageController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ================== QUESTIONS LIST + CREATE ==================

    @GetMapping("/questions")
    public String showQuestions(Model model,
                                Principal principal,
                                @RequestParam(value = "error", required = false) String error) {
        List<QuestionDTO> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        model.addAttribute("username", principal.getName());
        model.addAttribute("error", error);
        return "home"; // templates/home.html
    }

    @PostMapping("/questions/create")
    public String createQuestion(@RequestParam String title,
                                 @RequestParam String content,
                                 Principal principal) {

        QuestionDTO dto = new QuestionDTO();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setAuthorName(principal.getName());

        questionService.createQuestion(dto);
        return "redirect:/questions";
    }

    // ================== QUESTION EDIT / DELETE (with password) ==================

    @GetMapping("/questions/edit/{id}")
    public String editQuestionForm(@PathVariable Long id,
                                   Model model,
                                   Principal principal,
                                   @RequestParam(value = "error", required = false) String error) {
        QuestionDTO question = questionService.getQuestionById(id);

        if (!question.getAuthorName().equals(principal.getName())) {
            return "redirect:/questions?error=You can only edit your own questions";
        }

        model.addAttribute("question", question);
        model.addAttribute("error", error);
        return "question-edit"; // templates/question-edit.html
    }

    @PostMapping("/questions/edit/{id}")
    public String editQuestion(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam String password,
                               Principal principal,
                               Model model) {

        QuestionDTO question = questionService.getQuestionById(id);

        if (!question.getAuthorName().equals(principal.getName())) {
            return "redirect:/questions?error=You can only edit your own questions";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("question", question);
            model.addAttribute("error", "Invalid password");
            return "question-edit";
        }

        QuestionDTO updated = new QuestionDTO();
        updated.setTitle(title);
        updated.setContent(content);
        updated.setAuthorName(principal.getName());

        questionService.updateQuestion(id, updated);
        return "redirect:/questions";
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id,
                                 @RequestParam String password,
                                 Principal principal) {

        QuestionDTO question = questionService.getQuestionById(id);

        if (!question.getAuthorName().equals(principal.getName())) {
            return "redirect:/questions?error=You can only delete your own questions";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/questions?error=Invalid password";
        }

        questionService.deleteQuestion(id);
        return "redirect:/questions";
    }

    // ================== ANSWERS LIST + CREATE ==================

    @GetMapping("/answers")
    public String showAnswers(@RequestParam("questionId") Long questionId,
                              Model model,
                              Principal principal,
                              @RequestParam(value = "error", required = false) String error) {

        QuestionDTO question = questionService.getQuestionById(questionId);
        List<AnswerDTO> answers = answerService.getAllAnswersForQuestion(questionId);

        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        model.addAttribute("username", principal.getName());
        model.addAttribute("error", error);

        return "answer"; // templates/answer.html
    }

    @PostMapping("/answers/create")
    public String createAnswer(@RequestParam Long questionId,
                               @RequestParam String content,
                               Principal principal) {

        AnswerDTO dto = new AnswerDTO();
        dto.setQuestionId(questionId);
        dto.setContent(content);
        dto.setAuthorName(principal.getName());

        answerService.createAnswer(dto);
        // you can also redirect back to /answers?questionId=questionId
        return "redirect:/answers?questionId=" + questionId;
    }

    // ================== ANSWER EDIT / DELETE (with password) ==================

    // 👈 THIS is the mapping your "Edit answer" link needs
    @GetMapping("/answers/edit/{id}")
    public String editAnswerForm(@PathVariable Long id,
                                 @RequestParam("questionId") Long questionId,
                                 Model model,
                                 Principal principal,
                                 @RequestParam(value = "error", required = false) String error) {

        AnswerDTO answer = answerService.getAnswerById(id);

        if (!answer.getAuthorName().equals(principal.getName())) {
            return "redirect:/answers?questionId=" + questionId +
                    "&error=You can only edit your own answers";
        }

        model.addAttribute("answer", answer);
        model.addAttribute("questionId", questionId);
        model.addAttribute("error", error);
        return "answer-edit"; // templates/answer-edit.html
    }

    @PostMapping("/answers/edit/{id}")
    public String editAnswer(@PathVariable Long id,
                             @RequestParam("questionId") Long questionId,
                             @RequestParam String content,
                             @RequestParam String password,
                             Principal principal,
                             Model model) {

        AnswerDTO answer = answerService.getAnswerById(id);

        if (!answer.getAuthorName().equals(principal.getName())) {
            return "redirect:/answers?questionId=" + questionId +
                    "&error=You can only edit your own answers";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("answer", answer);
            model.addAttribute("questionId", questionId);
            model.addAttribute("error", "Invalid password");
            return "answer-edit";
        }

        AnswerDTO updated = new AnswerDTO();
        updated.setId(id);
        updated.setQuestionId(questionId);
        updated.setContent(content);
        updated.setAuthorName(principal.getName());

        answerService.updateAnswer(id, updated);
        return "redirect:/answers?questionId=" + questionId;
    }

    @PostMapping("/answers/delete/{id}")
    public String deleteAnswer(@PathVariable Long id,
                               @RequestParam("questionId") Long questionId,
                               @RequestParam String password,
                               Principal principal) {

        AnswerDTO answer = answerService.getAnswerById(id);

        if (!answer.getAuthorName().equals(principal.getName())) {
            return "redirect:/answers?questionId=" + questionId +
                    "&error=You can only delete your own answers";
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/answers?questionId=" + questionId + "&error=Invalid password";
        }

        answerService.deleteAnswer(id);
        return "redirect:/answers?questionId=" + questionId;
    }
}
