package com.maverick.eventcontrolhub.event;

import com.maverick.eventcontrolhub.common.EventStatus;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String title,
        String description,
        String venue,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime registrationOpenAt,
        LocalDateTime registrationCloseAt,
        boolean registrationOpen,
        boolean walkinAllowed,
        boolean walkinOpen,
        LocalDateTime walkinCloseAt,
        boolean enableEntry,
        boolean enableFood,
        boolean enableGoodies,
        String registrationSlug,
        String walkinSlug,
        int maxCapacity,
        EventStatus status
) {
    public static EventDto from(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getStartTime(),
                event.getEndTime(),
                event.getRegistrationOpenAt(),
                event.getRegistrationCloseAt(),
                event.isRegistrationOpen(),
                event.isWalkinAllowed(),
                event.isWalkinOpen(),
                event.getWalkinCloseAt(),
                event.isEnableEntry(),
                event.isEnableFood(),
                event.isEnableGoodies(),
                event.getRegistrationSlug(),
                event.getWalkinSlug(),
                event.getMaxCapacity(),
                event.getStatus()
        );
    }
}
