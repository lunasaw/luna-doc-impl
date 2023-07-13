package io.github.lunasaw.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SessionRepository sessionRepository;

    public void login(String username) {
        Session session = sessionRepository.findById(getSessionId());
        if (session == null) {
            session = sessionRepository.createSession();
        }
        session.setAttribute("username", username);
        sessionRepository.save(session);
    }

    @Override
    public String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

}
