package com.quora.quora.service;

import java.util.List;

import com.quora.quora.dto.AnswerDTO;

public interface AnswerService {

    AnswerDTO createAnswer(AnswerDTO answerDTO);

    List<AnswerDTO> getAllAnswersForQuestion(Long questionId);

    AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO);

    void deleteAnswer(Long id);

    AnswerDTO getAnswerById(Long id);
}
