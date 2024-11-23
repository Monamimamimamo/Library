package com.urfu.library.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

/**
 * Класс конфигурации для настройки отправки на электронную почту.
 */
@Configuration
public class MailConfiguration {

    /**
     * Хост SMTP-сервера.
     */
    @Value("${mail.host}")
    private String host;

    /**
     * Порт SMTP-сервера.
     */
    @Value("${mail.port}")
    private int port;

    /**
     * Имя пользователя для аутентификации на SMTP-сервере.
     */
    @Value("${mail.username}")
    private String username;

    /**
     * Пароль для аутентификации на SMTP-сервере.
     */
    @Value("${mail.password}")
    private String password;

    /**
     * Указывает, требуется ли аутентификация SMTP.
     */
    @Value("${mail.auth}")
    private Boolean smtpAuth;

    /**
     * Указывает, использовать ли протокол TLS для соединения.
     */
    @Value("${mail.tls}")
    private Boolean tls;

    /**
     * Протокол для отправки почты.
     */
    @Value("${mail.protocol}")
    private String protocol;

    /**
     * Создаёт и настраивает бин JavaMailSenderImpl для отправки на электронную почту.
     */
    @Bean
    public JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

        Properties sendProperties = new Properties();
        sendProperties.setProperty("mail.smtp.auth", smtpAuth.toString());
        sendProperties.setProperty("mail.smtp.starttls.enable", tls.toString());
        sendProperties.setProperty("mail.transport.protocol", protocol);
        sendProperties.setProperty("mail.smtp.ssl.trust", "*");
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }
}