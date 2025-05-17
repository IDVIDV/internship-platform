package ru.internship.platform.mapper;

import ru.internship.platform.dto.AddLesson;
import ru.internship.platform.dto.LessonDto;
import ru.internship.platform.dto.LessonItem;
import ru.internship.platform.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface LessonMapper {
    @Mapping(target = "internshipId", source = "internship.id")
    @Mapping(target = "taskItemList", source = "tasks")
    LessonDto toLessonDto(Lesson lesson);

    @Mapping(target = "internshipId", source = "internship.id")
    LessonItem toLessonItem(Lesson lesson);

    List<LessonItem> toLessonItemList(List<Lesson> lessonList);

    @Mapping(target = "internship",
            expression = "java(new ru.internship.platform.entity." +
                    "Internship(addLesson.getInternshipId(), null, null, null, null, null, null, null))")
    Lesson toLesson(AddLesson addLesson);
}
