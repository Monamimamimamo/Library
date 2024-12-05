package com.urfu.library.controller;

import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.controller.dto.UserDto;
import com.urfu.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NameAlreadyBoundException;

/**
 * Контроллер для обработки запросов связанных сущностью User
 * @author Alexandr FIlatov
 */
@Validated
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создание аккаунта пользователя
     * HttpStatus: CREATED, в случае успеха.
     * HttpStatus: UNPROCESSABLE_ENTITY, в случае существования пользователя с таким именем или некорректного ввода данных.
     * @author Alexandr FIlatov
     */
    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserDto userDto) throws NameAlreadyBoundException {
        User user = new User(userDto.username(), userDto.email(), userDto.password(), Role.ROLE_USER);
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Создание аккаунта администратора
     * HttpStatus: CREATED, в случае успеха.
     * HttpStatus: UNPROCESSABLE_ENTITY, в случае существования админа с таким именем или некорректного ввода данных.
     * HttpStatus: FORBIDDEN, в случае, если пользователь не является Админом.
     * @author Alexandr FIlatov
     */
    @PostMapping("/admin/signup")
    public ResponseEntity<String> createAdminUser(@RequestBody @Valid UserDto userDto) throws NameAlreadyBoundException {
        User user = new User(userDto.username(), userDto.email(), userDto.password(), Role.ROLE_ADMIN);
        userService.createUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
