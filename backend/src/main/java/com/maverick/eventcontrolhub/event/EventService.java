package com.maverick.eventcontrolhub.event;

import com.maverick.eventcontrolhub.common.ApiException;
import com.maverick.eventcontrolhub.common.EventStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private static final String SLUG_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom secureRandom = new SecureRandom();
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventDto create(EventRequest request) {
        validateWindow(request);
        Event event = eventRepository.save(new Event(
                request.title(),
                request.description(),
                request.venue(),
                request.startTime(),
                request.endTime(),
                request.registrationOpenAt(),
                request.registrationCloseAt(),
                request.registrationOpen(),
                request.walkinAllowed(),
                request.walkinAllowed() && request.walkinOpen(),
                request.walkinCloseAt(),
                true,
                request.enableFood(),
                request.enableGoodies(),
                uniqueRegistrationSlug(),
                request.walkinAllowed() ? uniqueWalkinSlug() : null,
                request.maxCapacity(),
                EventStatus.OPEN
        ));
        return EventDto.from(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream().map(EventDto::from).toList();
    }

    @Transactional(readOnly = true)
    public EventDto findDto(Long id) {
        return EventDto.from(find(id));
    }

    public Event find(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public Event findByRegistrationSlug(String slug) {
        return eventRepository.findByRegistrationSlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Public registration link was not found"));
    }

    public Event findByWalkinSlug(String slug) {
        return eventRepository.findByWalkinSlug(slug)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Walk-in registration link was not found"));
    }

    @Transactional
    public EventDto update(Long id, EventRequest request) {
        validateWindow(request);
        Event event = find(id);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setVenue(request.venue());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setRegistrationOpenAt(request.registrationOpenAt());
        event.setRegistrationCloseAt(request.registrationCloseAt());
        event.setRegistrationOpen(request.registrationOpen());
        event.setWalkinAllowed(request.walkinAllowed());
        event.setWalkinOpen(request.walkinAllowed() && request.walkinOpen());
        event.setWalkinCloseAt(request.walkinCloseAt());
        event.setEnableEntry(true);
        event.setEnableFood(request.enableFood());
        event.setEnableGoodies(request.enableGoodies());
        if (request.walkinAllowed() && event.getWalkinSlug() == null) {
            event.setWalkinSlug(uniqueWalkinSlug());
        }
        if (!request.walkinAllowed()) {
            event.setWalkinSlug(null);
        }
        event.setMaxCapacity(request.maxCapacity());
        return EventDto.from(event);
    }

    @Transactional
    public EventDto open(Long id) {
        Event event = find(id);
        event.setStatus(EventStatus.OPEN);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto complete(Long id) {
        Event event = find(id);
        event.setStatus(EventStatus.COMPLETED);
        event.setRegistrationOpen(false);
        event.setWalkinOpen(false);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto openRegistration(Long id) {
        Event event = find(id);
        event.setRegistrationOpen(true);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto closeRegistration(Long id) {
        Event event = find(id);
        event.setRegistrationOpen(false);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto extendRegistration(Long id, LocalDateTime registrationCloseAt) {
        Event event = find(id);
        if (!registrationCloseAt.isAfter(event.getRegistrationOpenAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Registration close time must be after open time");
        }
        event.setRegistrationCloseAt(registrationCloseAt);
        event.setRegistrationOpen(true);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto openWalkins(Long id) {
        Event event = find(id);
        if (!event.isWalkinAllowed()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Walk-ins are not enabled for this event");
        }
        event.setWalkinOpen(true);
        if (event.getWalkinSlug() == null) {
            event.setWalkinSlug(uniqueWalkinSlug());
        }
        return EventDto.from(event);
    }

    @Transactional
    public EventDto closeWalkins(Long id) {
        Event event = find(id);
        event.setWalkinOpen(false);
        return EventDto.from(event);
    }

    @Transactional
    public EventDto extendWalkins(Long id, LocalDateTime walkinCloseAt) {
        Event event = find(id);
        if (!event.isWalkinAllowed()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Walk-ins are not enabled for this event");
        }
        event.setWalkinCloseAt(walkinCloseAt);
        event.setWalkinOpen(true);
        if (event.getWalkinSlug() == null) {
            event.setWalkinSlug(uniqueWalkinSlug());
        }
        return EventDto.from(event);
    }

    private void validateWindow(EventRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Event end time must be after start time");
        }
        if (!request.registrationCloseAt().isAfter(request.registrationOpenAt())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Registration close time must be after open time");
        }
    }

    private String uniqueRegistrationSlug() {
        String slug;
        do {
            slug = "reg-" + randomToken(12);
        } while (eventRepository.findByRegistrationSlug(slug).isPresent());
        return slug;
    }

    private String uniqueWalkinSlug() {
        String slug;
        do {
            slug = "walkin-" + randomToken(12);
        } while (eventRepository.findByWalkinSlug(slug).isPresent());
        return slug;
    }

    private String randomToken(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(SLUG_CHARS.charAt(secureRandom.nextInt(SLUG_CHARS.length())));
        }
        return builder.toString();
    }
}
