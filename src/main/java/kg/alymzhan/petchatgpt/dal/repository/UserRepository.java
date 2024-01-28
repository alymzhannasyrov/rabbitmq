package kg.alymzhan.petchatgpt.dal.repository;

import kg.alymzhan.petchatgpt.dal.entity.User;
import kg.alymzhan.petchatgpt.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameOrEmail(String name, String email);

    List<User> findByStatusIn(EnumSet<UserStatus> statuses);

    List<User> findByStatus(UserStatus userStatus);
}
