package ru.internship.platform.mapper;

import ru.internship.platform.dto.Performance;
import ru.internship.platform.dto.TaskForkDto;
import ru.internship.platform.dto.TaskForkItem;
import ru.internship.platform.entity.TaskFork;
import ru.internship.platform.entity.archive.ArchivePerformance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CommitMapper.class, UserMapper.class})
public interface TaskForkMapper {
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "commitDtoList", source = "commits")
    TaskForkDto toTaskForkDto(TaskFork taskFork);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "user.id")
    TaskForkItem toTaskForkItem(TaskFork taskFork);

    List<TaskForkItem> toTaskForkItemList(List<TaskFork> taskForkList);

    Performance toPerformance(TaskFork taskFork);

    List<Performance> toPerformanceList(List<TaskFork> taskForkList);

    @Mapping(target = "internship", ignore = true)
    @Mapping(target = "forkUrl", source = "url")
    ArchivePerformance toArchivePerformance(TaskFork taskFork);
}
