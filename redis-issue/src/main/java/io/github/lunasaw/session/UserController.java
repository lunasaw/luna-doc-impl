package io.github.lunasaw.session;

import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService                   userService;

    private final RedisIndexedSessionRepository sessionRepository;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username) {
        userService.login(username);
        return "login success";
    }

    @GetMapping("/user")
    public String getUser() {
        Session session = sessionRepository.findById(userService.getSessionId());
        String username = session.getAttribute("username");
        if (username != null) {
            return "Logged in as: " + username;
        } else {
            return "Not logged in";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        sessionRepository.deleteById(userService.getSessionId());
        return "Logout successful";
    }

}
