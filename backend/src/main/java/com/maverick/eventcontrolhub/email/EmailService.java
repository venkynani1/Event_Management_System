package com.maverick.eventcontrolhub.email;

import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;

public interface EmailService {
    void sendRegistrationConfirmation(Event event, Registration registration);
}
