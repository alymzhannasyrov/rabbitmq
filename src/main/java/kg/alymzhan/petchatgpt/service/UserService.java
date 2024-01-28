package kg.alymzhan.petchatgpt.service;

import kg.alymzhan.petchatgpt.dal.entity.User;
import kg.alymzhan.petchatgpt.dal.repository.UserRepository;
import kg.alymzhan.petchatgpt.dto.UserDto;
import kg.alymzhan.petchatgpt.enums.UserStatus;
import kg.alymzhan.petchatgpt.exception.UserAlreadyExistException;
import kg.alymzhan.petchatgpt.exception.UserNotFoundException;
import kg.alymzhan.petchatgpt.mapper.UserMapper;
import kg.alymzhan.petchatgpt.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public static final EnumSet<UserStatus> ACTIVE_STATUSES = EnumSet.of(UserStatus.ACTIVE, UserStatus.CANDIDATE);

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<UserDto> collect = userRepository.findByStatusIn(ACTIVE_STATUSES)
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Users: {}", MappingUtils.beautifyJson(collect));
        return collect;
    }

    @Transactional
    public UserDto addUser(UserDto newUser) {
        log.info("Check user: {}", MappingUtils.beautifyJson(newUser));

        checkIfUserExists(newUser);

        User userEntity = userMapper.toUserEntity(newUser);

        User savedUser = userRepository.saveAndFlush(userEntity);
        log.info("User successfully saved: {}", MappingUtils.beautifyJson(savedUser));
        return userMapper.toUserDto(savedUser);
    }

    private void checkIfUserExists(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByNameOrEmail(userDto.name(), userDto.email());

        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistException(userMapper.toUserDto(optionalUser.get()));
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toUserDto(user);
    }

    public List<User> getNewUsers() {
        return userRepository.findByStatus(UserStatus.CANDIDATE);
    }

    public User processUser(User user) {
        User updatedUser = userMapper.toUpdatedUser(user);
        log.info("User: {}", MappingUtils.beautifyJson(updatedUser));
        userRepository.save(updatedUser);
        return updatedUser;
    }
}
