package kg.alymzhan.petchatgpt.service;

import kg.alymzhan.petchatgpt.dto.Message;
import kg.alymzhan.petchatgpt.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyCustomBot extends TelegramLongPollingBot {

    private final RedisRepository redisRepository;
    private final ChatGptClientService chatGPTClientService;

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return "devbot";
    }

    @Override
    public String getBotToken() {
        return "6176575099:AAFh_3yMzOe9vR1xoQJ40maREsf5igTCuws";
    }

    @PostConstruct
    public void toAlive() {
        var timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("Поддерживаем связь ...");
                    execute(new GetMe());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 10 * 60 * 1000);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            var chatId = String.valueOf(update.getMessage().getChatId());
            var text = update.getMessage().getText();

            var lastName = update.getMessage().getFrom().getLastName();
            var firstName = update.getMessage().getFrom().getFirstName();
            var userName = update.getMessage().getFrom().getUserName();
            var isBot = update.getMessage().getFrom().getIsBot();

            if (update.getMessage().isCommand()) {
                if (update.getMessage().getText().equals("/deletecontext")) {
                    log.info("Удаление контекста пользователя: {} {}", lastName, firstName);
                    isToEndChat(chatId);
                    var endMessage = new SendMessage();
                    endMessage.setChatId(chatId);
                    endMessage.setText("Диалог завершен");
                    var messageId = execute(endMessage).getMessageId();
                    Thread.sleep(2000);
                    execute(DeleteMessage.builder().
                            chatId(chatId).messageId(messageId)
                            .build());
                } else if (update.getMessage().getText().equals("/start")) {
                    log.info("Поприветствуем пользователя {} {}", firstName, lastName);

                    var greetingMessage = new SendMessage();
                    greetingMessage.setChatId(chatId);
                    greetingMessage.setText("Приветствую, дорогой(ая) %s %s!\n\nБот является решением для ограничений, с которыми столкнулся разработчик)\n\nЭтот бот открывает вам доступ к продукту OpenAI - ChatGPT".formatted(firstName, lastName));
                    execute(greetingMessage);
                } else {
                    SendMessage endMessage = new SendMessage();
                    endMessage.setChatId(chatId);
                    endMessage.setText("Неизвестная команда");
                    execute(endMessage);
                }
            }

            if (update.getMessage().hasText() && !update.getMessage().isCommand()) {

                saveMessage(chatId, "user:" + text);
                var previousMessages = getPreviousMessages(chatId);

                if (previousMessages != null) {
                    previousMessages.add(new Message("user", text));
                    var response = sendMessageToOpenAI(previousMessages);
                    saveMessage(chatId, "assistant:" + response);
                    try {
                        var messageId = update.getMessage().getMessageId();

                        sendResponseToUser(chatId, response, messageId);
                    } catch (TelegramApiException e) {
                        log.error("Error during sendResponse {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    @SneakyThrows
    private void isToEndChat(String chatId) {
        var keys = redisRepository.findKeysByPattern(chatId);
        var deletedKeys = redisRepository.deleteAllKeys(keys);
        log.info("Удалено ключей {} кол-во значений {}", deletedKeys, keys.size());
    }

    private String sendMessageToOpenAI(List<Message> messages) {
        var answer = chatGPTClientService.sendMessageToOpenAI(messages);
        if (answer == null) {
            return "Нет ответа! Возможно ошибка от ChatGPT";
        } else {
            return answer.choices().get(0).message().content();
        }
    }

    private void saveMessage(String chatId, String message) {
        var savedKeys = redisRepository.saveKeys(chatId, message);
        log.info("Сохранено ключей {}", savedKeys);
    }

    private List<Message> getPreviousMessages(String chatId) {
        var messages = redisRepository.getObjectsByKey(chatId);
        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.stream()
                    .map(MyCustomBot::apply)
                    .collect(Collectors.toList());
        }
    }

    private void sendResponseToUser(String chatId, String message, Integer messageId) throws TelegramApiException {
        var maxMessageLength = 4096;
        var chunks = new ArrayList<String>();

        for (int i = 0; i < message.length(); i += maxMessageLength) {
            var endIndex = Math.min(i + maxMessageLength, message.length());
            chunks.add(message.substring(i, endIndex));
        }
        for (var chunk : chunks) {
            var sendMessage = new SendMessage(chatId, chunk);
            sendMessage.setReplyToMessageId(messageId);

            execute(sendMessage);
        }
    }

    private static Message apply(Object o) {
        var object = (String) o;
        var role = object.split(":")[0];
        return new Message(role, object.substring(object.indexOf(":") + 1));
    }
}
