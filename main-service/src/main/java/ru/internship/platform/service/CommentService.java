package ru.internship.platform.service;

import ru.internship.platform.repository.CommitRepository;
import ru.internship.platform.dto.AddComment;
import ru.internship.platform.dto.CommentDto;
import ru.internship.platform.entity.Comment;
import ru.internship.platform.entity.Commit;
import ru.internship.platform.mapper.CommentMapper;
import ru.internship.platform.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final MessageService messageService;
    private final CommitRepository commitRepository;

    public void addComment(AddComment addComment) {
        Comment comment = commentMapper.toComment(addComment);
        Commit commit = commitRepository.findById(comment.getCommit().getId()).orElse(null);

        if (commit == null) {
            throw new EntityNotFoundException(String.format("Commit with id %d does not exist",
                    addComment.getCommitId()));
        }

        comment.setCommit(commit);
        comment.setUser(commit.getTaskFork().getUser());
        commentRepository.save(comment);
        messageService.noticeStudentOnCommitCheck(comment);
    }

    public List<CommentDto> getAllCommentsByCommitId(Integer commitId) {
        return commentMapper.toCommentDtoList(commentRepository.findAllByCommitId(commitId));
    }
}
