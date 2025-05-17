package ru.internship.platform.mapper;

import ru.internship.platform.dto.AddTask;
import ru.internship.platform.dto.TaskDto;
import ru.internship.platform.dto.TaskItem;
import ru.internship.platform.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = TaskForkMapper.class)
public interface TaskMapper {
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "taskForkItemList", source = "taskForks")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "lessonId", source = "lesson.id")
    TaskItem toTaskItem(Task task);

    List<TaskItem> toTaskItemList(List<Task> taskList);

    @Mapping(target = "lesson",
            expression = "java(new ru.internship.platform.entity." +
                    "Lesson(addTask.getLessonId(), null, null, null, null))")
    Task toTask(AddTask addTask);
}
