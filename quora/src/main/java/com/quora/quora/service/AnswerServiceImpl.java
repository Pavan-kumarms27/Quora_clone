package com.quora.quora.service;

import com.quora.quora.dto.AnswerDTO;
import com.quora.quora.model.Answer;
import com.quora.quora.model.Question;
import com.quora.quora.model.User;
import com.quora.quora.repository.AnswerRepository;
import com.quora.quora.repository.QuestionRepository;
import com.quora.quora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;   // 👈 added

    @Override
    public AnswerDTO createAnswer(AnswerDTO dto) {
        Answer answer = mapToEntity(dto);
        Answer saved = answerRepository.save(answer);
        return mapToDTO(saved);
    }

    @Override
    public List<AnswerDTO> getAllAnswersForQuestion(Long questionId) {
        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public AnswerDTO updateAnswer(Long id, AnswerDTO dto) {
        Answer existing = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        // only updating content; question + author remain same
        existing.setContent(dto.getContent());

        Answer saved = answerRepository.save(existing);
        return mapToDTO(saved);
    }

    @Override
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    @Override
    public AnswerDTO getAnswerById(Long id) {
        return answerRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
    }

    // ================== MAPPERS ==================

    private AnswerDTO mapToDTO(Answer a) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(a.getId());
        dto.setQuestionId(a.getQuestion().getId());
        dto.setContent(a.getContent());
        dto.setAuthorName(a.getAuthor().getUsername());   // 👈 from User
        return dto;
    }

    private Answer mapToEntity(AnswerDTO dto) {
        Answer a = new Answer();
        a.setId(dto.getId()); // can be null for create

        // load question from DB
        Question q = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        a.setQuestion(q);

        a.setContent(dto.getContent());

        // load author from DB using username from DTO
        User author = userRepository.findByUsername(dto.getAuthorName())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getAuthorName()));
        a.setAuthor(author);   // 👈 set relation

        return a;
    }
}
