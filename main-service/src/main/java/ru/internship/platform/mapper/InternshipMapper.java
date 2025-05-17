package ru.internship.platform.mapper;

import ru.internship.platform.dto.AddInternship;
import ru.internship.platform.dto.InternshipDto;
import ru.internship.platform.dto.InternshipItem;
import ru.internship.platform.entity.Internship;
import ru.internship.platform.entity.archive.ArchiveInternship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = LessonMapper.class)
public interface InternshipMapper {
    @Mapping(target = "lessonItemList", source = "lessons")
    InternshipDto toInternshipDto(Internship internship);

    InternshipItem toInternshipItem(Internship internship);

    List<InternshipItem> toInternshipItemList(List<Internship> internshipList);

    Internship toInternship(AddInternship addInternship);

    @Mapping(target = "userInternships", ignore = true)
    @Mapping(target = "performances", ignore = true)
    ArchiveInternship toArchiveInternship(Internship internship);
}
