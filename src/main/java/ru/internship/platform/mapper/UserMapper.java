package ru.internship.platform.mapper;

import ru.internship.platform.dto.RegisterUser;
import ru.internship.platform.dto.UserDto;
import ru.internship.platform.entity.User;
import ru.internship.platform.entity.archive.ArchiveUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterUser registerUser);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> userList);

    @Mapping(target = "userInternships", ignore = true)
    @Mapping(target = "performances", ignore = true)
    ArchiveUser toArchiveUser(User user);

    List<ArchiveUser> toArchiveUserList(List<User> userList);
}
