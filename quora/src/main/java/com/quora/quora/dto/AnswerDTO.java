package com.quora.quora.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long id;
    private Long questionId;
    private String content;
    private String authorName;

    public AnswerDTO() {}

    public AnswerDTO(Long id, Long questionId, String content, String authorName) {
        this.id = id;
        this.questionId = questionId;
        this.content = content;
        this.authorName = authorName;
    }
}
