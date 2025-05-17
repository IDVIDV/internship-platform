package ru.internship.platform.mapper;

import ru.internship.platform.entity.UserInternship;
import ru.internship.platform.entity.archive.ArchiveUserInternship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, InternshipMapper.class})
public interface UserInternshipMapper {
    ArchiveUserInternship toArchiveUserInternship(UserInternship userInternship);
}
