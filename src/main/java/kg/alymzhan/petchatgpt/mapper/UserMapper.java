package kg.alymzhan.petchatgpt.mapper;

import kg.alymzhan.petchatgpt.dal.entity.User;
import kg.alymzhan.petchatgpt.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userSecret", ignore = true)
    @Mapping(target = "status", constant = "CANDIDATE")
    @Mapping(target = "id", ignore = true)
    User toUserEntity(UserDto userDto);
    @Mapping(target = "userSecret", defaultExpression = "java(kg.alymzhan.petchatgpt.utils.UserUtils.generateUserSecret())")
    @Mapping(target = "status", constant = "ACTIVE")
    User toUpdatedUser(User user);
}
