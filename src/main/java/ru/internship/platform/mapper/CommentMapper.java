package ru.internship.platform.mapper;

import ru.internship.platform.dto.AddComment;
import ru.internship.platform.dto.CommentDto;
import ru.internship.platform.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
//    @Mapping(target = "user",
//            expression = "java(new ds.dsinternshipcontrolsystem.entity." +
//                    "User(addComment.getUserId(), null, null, null, null, null, " +
//                    "null, null, null, null, null, null, null, null, null, null))"
//    )
    @Mapping(target = "commit",
            expression = "java(new ds.dsinternshipcontrolsystem.entity." +
                    "Commit(addComment.getCommitId(), null, null, null, null, null))"
    )
    Comment toComment(AddComment addComment);

    @Mapping(target = "commitUrl", source = "commit.url")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toCommentDtoList(List<Comment> commentList);
}
