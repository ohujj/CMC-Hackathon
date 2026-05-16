package com.cmchackathon.domain.comment.service;

import com.cmchackathon.domain.comment.entity.Comment;
import com.cmchackathon.domain.comment.repository.CommentRepository;
import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (comment.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        comment.delete();
    }
}