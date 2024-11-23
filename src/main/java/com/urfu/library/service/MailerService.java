package com.urfu.library.service;

import com.urfu.library.model.MessageTypes;
import com.urfu.library.model.Reservation;
import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для отправки уведомлений пользователям о различных событиях.
 */
@Service
public class MailerService {
    @Value("${mail.username}")
    private String username;

    private JavaMailSender mailSender;
    private final UserRepository userRepository;

    public MailerService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    /**
     * Отправляет уведомление о просроченной книге пользователю и администраторам.
     */
    public void notifyDeadlineExpired(Reservation reservation) {
        Optional<User> optionalUser = userRepository.findById(reservation.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            send(MessageTypes.DEADLINE_EXPIRED_USER, optionalUser.get(),
                    reservation.getBookId(),
                    reservation.getStartDate(),
                    reservation.getFinishDate(),
                    user.getUsername());

            for (User admin : userRepository.findAll()) {
                if (admin.getRole() == Role.ROLE_ADMIN) {
                    send(MessageTypes.DEADLINE_EXPIRED_ADMIN, admin,
                            reservation.getBookId(),
                            reservation.getStartDate(),
                            reservation.getFinishDate(),
                            user.getUsername(),
                            user.getEmail()
                    );
                }
            }
        }
    }

    /**
     * Отправляет напоминание пользователю о необходимости вернуть книгу,
     * если до дедлайна осталось менее 5 дней.
     */
    public void notifyDeadline(Reservation reservation, long daysLeft) {
        Optional<User> optionalUser = userRepository.findById(reservation.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            send(MessageTypes.NOTIFY, user,
                    reservation.getBookId(),
                    reservation.getStartDate(),
                    reservation.getFinishDate(),
                    user.getUsername(),
                    getDayMessage(daysLeft)
            );
        }
    }


    /**
     * Отправляет уведомление об успешном возврате книги пользователю и администраторам.
     */
    public void notifyReturned(Reservation reservation) {
        Optional<User> optionalUser = userRepository.findById(reservation.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            for (User admin : userRepository.findAll()) {
                if (admin.getRole() == Role.ROLE_ADMIN) {
                    send(MessageTypes.RETURNED, admin,
                            reservation.getBookId(),
                            reservation.getStartDate(),
                            reservation.getFinishDate(),
                            user.getUsername(),
                            user.getEmail()
                    );
                }
            }
        }
    }

    /**
     * Определяет правильную форму слова "день" в зависимости от числа
     */
    private String getDayMessage(long daysLeft) {
        if (daysLeft % 10 == 1 && daysLeft % 100 != 11) {
            return daysLeft + " день";
        } else if ((daysLeft % 10 >= 2 && daysLeft % 10 <= 4) && (daysLeft % 100 < 10 || daysLeft % 100 >= 20)) {
            return daysLeft + " дня";
        } else {
            return daysLeft + " дней";
        }
    }

    /**
     * Преобразует аргументы в сообщение.
     */
    private void send(MessageTypes type, User user, Object... args) {
        String messageBody = type.formatMessage(args);
        sendMail(user.getEmail(), type.getSubject(), messageBody);
    }

    /**
     * Отправляет сообщение на указанный адрес.
     */
    private void sendMail(String toAddress, String subject, String messageBody) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(messageBody);
        mailSender.send(simpleMailMessage);
    }
}
