package com.quora.quora.service;

import com.quora.quora.dao.QuestionDAO;
import com.quora.quora.dto.QuestionDTO;
import com.quora.quora.model.Question;
import com.quora.quora.model.User;
import com.quora.quora.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;
    private final UserRepository userRepository;   // 👈 inject user repo

    public QuestionServiceImpl(QuestionDAO questionDAO, UserRepository userRepository) {
        this.questionDAO = questionDAO;
        this.userRepository = userRepository;
    }

    // ========== MAPPERS ==========

    private QuestionDTO mapToDTO(Question q) {
        return new QuestionDTO(
                q.getId(),
                q.getTitle(),
                q.getContent(),
                q.getAuthor().getUsername(),   // 👈 from User
                q.getCreatedAt()
        );
    }

    private Question mapToEntity(QuestionDTO dto) {
        Question q = new Question();
        q.setId(dto.getId());
        q.setTitle(dto.getTitle());
        q.setContent(dto.getContent());

        // load author by username from DTO
        User author = userRepository.findByUsername(dto.getAuthorName())
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + dto.getAuthorName()));
        q.setAuthor(author);                    // 👈 relation instead of String

        return q;
    }

    // ========== SERVICE METHODS ==========

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return questionDAO.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDTO getQuestionById(Long id) {
        Question q = questionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return mapToDTO(q);
    }

    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question saved = questionDAO.save(mapToEntity(questionDTO));
        return mapToDTO(saved);
    }

    @Override
    public QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO) {
        Question existing = questionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existing.setTitle(questionDTO.getTitle());
        existing.setContent(questionDTO.getContent());

        // if you want to allow changing author:
        User author = userRepository.findByUsername(questionDTO.getAuthorName())
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + questionDTO.getAuthorName()));
        existing.setAuthor(author);

        Question updated = questionDAO.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionDAO.deleteById(id);
    }
}
