package com.cmchackathon.domain.comment.service;

import com.cmchackathon.domain.comment.entity.Comment;
import com.cmchackathon.domain.comment.repository.CommentRepository;
import com.cmchackathon.domain.ticket.entity.Ticket;
import com.cmchackathon.domain.ticket.repository.TicketRepository;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;


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

    public void createComment(Long userId, Long ticketId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        commentRepository.save(Comment.builder()
                .user(user)
                .ticket(ticket)
                .content(content)
                .build());
    }
}