package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.common.*;
import com.maverick.eventcontrolhub.email.EmailService;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.event.EventService;
import com.maverick.eventcontrolhub.qr.QrTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EventService eventService;
    private final QrTokenService qrTokenService;
    private final EmailService emailService;

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventService eventService,
                               QrTokenService qrTokenService,
                               EmailService emailService) {
        this.registrationRepository = registrationRepository;
        this.eventService = eventService;
        this.qrTokenService = qrTokenService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public PublicEventDto publicEvent(String slug) {
        return PublicEventDto.from(eventService.findByRegistrationSlug(slug));
    }

    @Transactional(readOnly = true)
    public PublicEventDto publicWalkinEvent(String slug) {
        return PublicEventDto.from(eventService.findByWalkinSlug(slug));
    }

    @Transactional
    public RegistrationDto registerPublic(String slug, PublicRegistrationRequest request) {
        Event event = eventService.findByRegistrationSlug(slug);
        ensureRegistrationOpen(event);
        Registration registration = create(event, request, RegistrationSource.NORMAL);
        emailService.sendRegistrationConfirmation(event, registration);
        return RegistrationDto.from(registration);
    }

    @Transactional
    public RegistrationDto registerWalkin(String slug, PublicRegistrationRequest request) {
        Event event = eventService.findByWalkinSlug(slug);
        ensureWalkinOpen(event);
        Registration registration = create(event, request, RegistrationSource.WALKIN);
        emailService.sendRegistrationConfirmation(event, registration);
        return RegistrationDto.from(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationDto> registrations(Long eventId) {
        Event event = eventService.find(eventId);
        return registrationRepository.findByEventOrderByRegisteredAtDesc(event).stream()
                .map(RegistrationDto::from)
                .toList();
    }

    private Registration create(Event event, PublicRegistrationRequest request, RegistrationSource source) {
        if (registrationRepository.existsByEventAndEmployeeIdIgnoreCase(event, request.employeeId())) {
            throw new ApiException(HttpStatus.CONFLICT, "This employee ID is already registered for this event");
        }
        if (registrationRepository.existsByEventAndEmployeeEmailIgnoreCase(event, request.employeeEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "This email is already registered for this event");
        }
        if (registrationRepository.existsByEventAndDeviceToken(event, request.deviceToken())) {
            throw new ApiException(HttpStatus.CONFLICT, "This browser has already submitted a registration for this event");
        }
        long occupied = registrationRepository.countByEventAndStatusIn(
                event,
                List.of(RegistrationStatus.REGISTERED, RegistrationStatus.WALKIN)
        );
        if (occupied >= event.getMaxCapacity()) {
            throw new ApiException(HttpStatus.CONFLICT, "Event capacity is full");
        }

        RegistrationStatus status = source == RegistrationSource.WALKIN ? RegistrationStatus.WALKIN : RegistrationStatus.REGISTERED;
        Registration registration = new Registration(
                event,
                request.employeeId().trim(),
                request.employeeName().trim(),
                request.employeeEmail().trim().toLowerCase(),
                status,
                source,
                qrTokenService.generateToken(),
                request.deviceToken(),
                LocalDateTime.now()
        );
        return registrationRepository.save(registration);
    }

    private void ensureRegistrationOpen(Event event) {
        LocalDateTime now = LocalDateTime.now();
        if (!event.isRegistrationOpen() || now.isBefore(event.getRegistrationOpenAt()) || now.isAfter(event.getRegistrationCloseAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Registration is closed for this event");
        }
    }

    private void ensureWalkinOpen(Event event) {
        LocalDateTime now = LocalDateTime.now();
        if (!event.isWalkinAllowed() || !event.isWalkinOpen()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Walk-in registration is closed for this event");
        }
        if (event.getWalkinCloseAt() != null && now.isAfter(event.getWalkinCloseAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Walk-in registration timer has expired");
        }
    }
}
