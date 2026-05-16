package com.cmchackathon.domain.like.service;

import com.cmchackathon.domain.like.entity.Like;
import com.cmchackathon.domain.like.repository.LikeRepository;
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
public class LikeService {

    private final LikeRepository likeRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public void addLike(Long userId, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (ticket.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.CANNOT_LIKE_OWN_TICKET);
        }

        if (likeRepository.existsByUserIdAndTicketId(userId, ticketId)) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        likeRepository.save(Like.builder()
                .user(user)
                .ticket(ticket)
                .build());
    }

    public void removeLike(Long userId, Long ticketId) {
        Like like = likeRepository.findByUserIdAndTicketId(userId, ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }
}
