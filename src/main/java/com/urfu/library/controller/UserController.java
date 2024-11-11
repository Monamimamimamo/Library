package com.urfu.library.controller;

import com.urfu.library.model.User;
import com.urfu.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов связанных сущностью User
 * @author Alexandr FIlatov
 */
@RestController
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создание аккаунта пользователя
     * @author Alexandr FIlatov
     */
    @PostMapping("/api/signup")
    public ResponseEntity<String> createUser(@RequestBody @Valid User user) {
        if(!userService.createUser(user)) {
            return new ResponseEntity<>("Username already taken", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Создание аккаунта администратора
     * @author Alexandr FIlatov
     */
    @PostMapping("/api/admin/signup")
    public ResponseEntity<String> createAdminUser(@RequestBody @Valid User user) {
        if(!userService.createAdmin(user)) {
            return new ResponseEntity<>("Username already taken", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
