package com.urfu.library.service;


import com.urfu.library.model.MessageTypes;
import com.urfu.library.model.Reservation;
import com.urfu.library.model.User;
import com.urfu.library.model.Role;
import com.urfu.library.model.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

/**
 * Класс реализует модульные тесты для сервиса рассылки на почты
 */
class MailerServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MailerService mailerService;

    private User user;
    private Reservation reservation;

    /**
     * Метод инициализации для каждого теста.
     * Создаёт тестовый экземпляр пользователя и бронирования.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "testuser", "testuser@example.com", "password", Role.ROLE_USER);

        reservation = new Reservation(1L, 1L, 1L, false, false,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5));
    }

    /**
     * Тестирует отправки сообщений об истечении дедлайна.
     * Проверяет, что письма отправляются как пользователю, так и администратору,
     * а их содержание корректно.
     */
    @Test
    void testNotifyDeadlineExpired() {
        Mockito.when(userRepository.findById(reservation.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(new User(2L, "admin", "admin@example.com", "adminpass", Role.ROLE_ADMIN)));
        String expectedSubject = MessageTypes.DEADLINE_EXPIRED_USER.getSubject();
        String expectedBodyForUser = MessageTypes.DEADLINE_EXPIRED_USER.formatMessage(
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername()
        );
        String expectedBodyForAdmin = MessageTypes.DEADLINE_EXPIRED_ADMIN.formatMessage(
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername(),
                user.getEmail()
        );

        mailerService.notifyDeadlineExpired(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(2)).send(captor.capture());

        SimpleMailMessage userMessage = captor.getAllValues().stream()
                .filter(msg -> msg.getTo() != null && msg.getTo()[0].equals(user.getEmail()))
                .findFirst()
                .orElse(null);


        Assertions.assertNotNull(userMessage);
        Assertions.assertEquals(expectedSubject, userMessage.getSubject());
        Assertions.assertEquals(expectedBodyForUser, userMessage.getText());


        SimpleMailMessage adminMessage = captor.getAllValues().stream()
                .filter(msg -> msg.getTo() != null && msg.getTo()[0].equals("admin@example.com"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(adminMessage);
        Assertions.assertEquals(expectedSubject, adminMessage.getSubject());
        Assertions.assertEquals(expectedBodyForAdmin, adminMessage.getText());
    }

    /**
     * Тестирует отправку сообщения о скором наступлении дедлайна.
     * Проверяет, что письмо отправляется пользователю с корректным содержанием.
     */
    @Test
    void testNotifyDeadline() {
        Mockito.when(userRepository.findById(reservation.getUserId())).thenReturn(Optional.of(user));

        String expectedSubject = MessageTypes.NOTIFY.getSubject();

        String expectedMessage5 = MessageTypes.NOTIFY.formatMessage(
                "5 дней",
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername()
        );

        mailerService.notifyDeadline(reservation, 5);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();

        Assertions.assertNotNull(sentMessage);
        Assertions.assertEquals(user.getEmail(), sentMessage.getTo()[0]);
        Assertions.assertEquals(expectedSubject, sentMessage.getSubject());
        Assertions.assertEquals(expectedMessage5, sentMessage.getText());

        String expectedMessage3 = MessageTypes.NOTIFY.formatMessage(
                "3 дня",
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername()
        );

        mailerService.notifyDeadline(reservation, 3);

        captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(2)).send(captor.capture()); // Второй вызов
        sentMessage = captor.getValue();
        Assertions.assertEquals(expectedMessage3, sentMessage.getText());

        String expectedMessage1 = MessageTypes.NOTIFY.formatMessage(
                "1 день",
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername()
        );

        mailerService.notifyDeadline(reservation, 1);

        captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(3)).send(captor.capture()); // Третий вызов
        sentMessage = captor.getValue();
        Assertions.assertEquals(expectedMessage1, sentMessage.getText());
    }


    /**
     * Тестирует отправку сообщения о возвращении книги.
     * Проверяет, что письмо отправляется администратору с корректным содержанием.
     */
    @Test
    void testNotifyReturned() {
        Mockito.when(userRepository.findById(reservation.getUserId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(new User(2L, "admin", "admin@example.com", "adminpass", Role.ROLE_ADMIN)));

        String expectedSubject = MessageTypes.RETURNED.getSubject();
        String expectedBody = MessageTypes.RETURNED.formatMessage(
                reservation.getBookId(),
                reservation.getStartDate(),
                reservation.getFinishDate(),
                user.getUsername(),
                user.getEmail()
        );

        mailerService.notifyReturned(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender, Mockito.times(1)).send(captor.capture());

        SimpleMailMessage adminMessage = captor.getAllValues()
                .stream()
                .filter(msg -> msg.getTo() != null && msg.getTo()[0].equals("admin@example.com"))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(adminMessage);
        Assertions.assertEquals(expectedSubject, adminMessage.getSubject());
        Assertions.assertEquals(expectedBody, adminMessage.getText());
    }

    /**
     * Тестирует отправку сообщения, если пользователь не найден.
     * Проверяет, что письмо не отправляется.
     */
    @Test
    void testNotifyDeadlineWithNoUser() {
        Mockito.when(userRepository.findById(reservation.getUserId())).thenReturn(Optional.empty());

        mailerService.notifyDeadline(reservation, 5);
        mailerService.notifyDeadlineExpired(reservation);
        mailerService.notifyReturned(reservation);

        Mockito.verify(mailSender, Mockito.never()).send(Mockito.any(SimpleMailMessage.class));
    }
}
