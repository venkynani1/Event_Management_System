package com.maverick.eventcontrolhub.email;

import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DevelopmentEmailService implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(DevelopmentEmailService.class);

    @Override
    public void sendRegistrationConfirmation(Event event, Registration registration) {
        log.info("""
                Development email placeholder:
                To: {}
                Subject: Registration confirmed - {}
                Body: Hi {}, your registration is confirmed. QR token: {}
                """,
                registration.getEmployeeEmail(),
                event.getTitle(),
                registration.getEmployeeName(),
                registration.getQrToken()
        );
    }
}
