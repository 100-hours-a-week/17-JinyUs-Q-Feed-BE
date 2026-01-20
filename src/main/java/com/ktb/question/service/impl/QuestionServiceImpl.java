package com.ktb.question.service.impl;

import com.ktb.question.domain.Question;
import com.ktb.question.domain.QuestionCategory;
import com.ktb.question.domain.QuestionType;
import com.ktb.question.dto.PaginationResponse;
import com.ktb.question.dto.QuestionCreateRequest;
import com.ktb.question.dto.QuestionDetailResponse;
import com.ktb.question.dto.QuestionListResponse;
import com.ktb.question.dto.QuestionSearchResponse;
import com.ktb.question.dto.QuestionSummaryResponse;
import com.ktb.question.dto.QuestionUpdateRequest;
import com.ktb.question.exception.QuestionNotFoundException;
import com.ktb.question.exception.SearchKeywordTooShortException;
import com.ktb.question.repository.QuestionRepository;
import com.ktb.question.service.QuestionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private static final int MIN_SEARCH_KEYWORD_LENGTH = 2;

    private final QuestionRepository questionRepository;

    @Override
    public QuestionListResponse getQuestions(QuestionCategory category, QuestionType type, Long cursor, int size) {
        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Question> questions = questionRepository.findActiveByFilters(type, category, cursor, pageable);

        return new QuestionListResponse(
                toSummaryResponses(questions.getContent()),
                toPaginationResponse(questions)
        );
    }

    @Override
    public QuestionDetailResponse getQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        return toDetailResponse(question);
    }

    @Override
    public QuestionSearchResponse search(String keyword, QuestionCategory category, QuestionType type, Long cursor, int size) {
        validateKeyword(keyword);

        PageRequest pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Question> questions = questionRepository.searchActiveByKeyword(keyword, type, category, cursor, pageable);

        return new QuestionSearchResponse(
                toSummaryResponses(questions.getContent()),
                toPaginationResponse(questions)
        );
    }

    @Override
    @Transactional
    public QuestionDetailResponse createQuestion(QuestionCreateRequest request) {
        Question question = Question.create(request.content(), request.type(), request.category());
        Question saved = questionRepository.save(question);

        return toDetailResponse(saved);
    }

    @Override
    @Transactional
    public QuestionDetailResponse updateQuestion(Long questionId, QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        if (request.content() != null) {
            question.updateContent(request.content());
        }
        if (request.type() != null) {
            question.updateType(request.type());
        }
        if (request.category() != null) {
            question.updateCategory(request.category());
        }
        if (request.useYn() != null) {
            if (request.useYn()) {
                question.activate();
            } else {
                question.delete();
            }
        }

        return toDetailResponse(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        if (question.isUseYn()) {
            question.delete();
        }
    }

    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().length() < MIN_SEARCH_KEYWORD_LENGTH) {
            throw new SearchKeywordTooShortException(MIN_SEARCH_KEYWORD_LENGTH);
        }
    }

    private List<QuestionSummaryResponse> toSummaryResponses(List<Question> questions) {
        return questions.stream()
                .map(question -> new QuestionSummaryResponse(
                        question.getId(),
                        question.getContent(),
                        question.getType(),
                        question.getCategory()
                ))
                .toList();
    }

    private QuestionDetailResponse toDetailResponse(Question question) {
        return new QuestionDetailResponse(
                question.getId(),
                question.getContent(),
                question.getType(),
                question.getCategory(),
                question.isUseYn(),
                question.getCreatedAt(),
                question.getUpdatedAt(),
                question.getDeletedAt()
        );
    }

    private PaginationResponse toPaginationResponse(Slice<Question> slice) {
        Long nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            Question last = slice.getContent().get(slice.getContent().size() - 1);
            nextCursor = slice.hasNext() ? last.getId() : null;
        }
        return new PaginationResponse(nextCursor, slice.hasNext(), slice.getSize());
    }
}
