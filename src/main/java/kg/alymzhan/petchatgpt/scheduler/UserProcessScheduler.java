package kg.alymzhan.petchatgpt.scheduler;

import kg.alymzhan.petchatgpt.dal.entity.User;
import kg.alymzhan.petchatgpt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static kg.alymzhan.petchatgpt.config.LoggingInterceptor.TRACE_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProcessScheduler {

    private final UserService userService;

    @Scheduled(cron = "*/3 * * * * *")
    public void userExecute() {
        ThreadContext.put(TRACE_ID_HEADER, UUID.randomUUID().toString());
        long start = System.currentTimeMillis();
        log.info("Start job");

        List<User> newUsers = userService.getNewUsers();

        if (CollectionUtils.isNotEmpty(newUsers)) {
            List<User> updatedUsers = newUsers
                    .stream()
                    .map(userService::processUser).toList();

            log.info("Count of updated: {}", updatedUsers.size());
        }

        log.info("Finished job: {} ms", System.currentTimeMillis() - start);
    }
}
