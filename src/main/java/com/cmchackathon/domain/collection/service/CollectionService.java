package com.cmchackathon.domain.collection.service;

import com.cmchackathon.domain.collection.dto.CollectionResponse;
import com.cmchackathon.domain.collection.entity.Collection;
import com.cmchackathon.domain.collection.repository.CollectionRepository;
import com.cmchackathon.domain.comment.dto.CommentResponse;
import com.cmchackathon.domain.comment.repository.CommentRepository;
import com.cmchackathon.domain.like.repository.LikeRepository;
import com.cmchackathon.domain.ticket.dto.TicketDetailResponse;
import com.cmchackathon.domain.ticket.entity.Ticket;
import com.cmchackathon.domain.ticket.repository.TicketRepository;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.domain.user.repository.UserRepository;
import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public void addCollection(Long userId, Long ticketId) {
        if (collectionRepository.existsByUserIdAndTicketIdAndDeletedAtIsNull(userId, ticketId)) {
            throw new BusinessException(ErrorCode.ALREADY_COLLECTED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.isShowYn()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        collectionRepository.save(Collection.builder()
                .user(user)
                .ticket(ticket)
                .build());
    }

    public void removeCollection(Long userId, Long ticketId) {
        Collection collection = collectionRepository.findByUserIdAndTicketIdAndDeletedAtIsNull(userId, ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COLLECTION_NOT_FOUND));

        collection.delete();
    }

    @Transactional(readOnly = true)
    public List<CollectionResponse> getMyCollections(Long userId) {
        return collectionRepository.findByUserIdWithDetails(userId)
                .stream()
                .map(CollectionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getCollectionDetail(Long userId, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.isShowYn()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        long likeCount = likeRepository.countByTicketId(ticketId);
        boolean isLiked = likeRepository.existsByUserIdAndTicketId(userId, ticketId);
        boolean isCollected = collectionRepository.existsByUserIdAndTicketIdAndDeletedAtIsNull(userId, ticketId);

        List<CommentResponse> comments = commentRepository.findByTicketIdAndDeletedAtIsNull(ticketId)
                .stream()
                .map(CommentResponse::from)
                .toList();

        return TicketDetailResponse.of(ticket, likeCount, isLiked, isCollected, comments);
    }
}