package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.event.Event;

import java.time.LocalDateTime;

public record PublicEventDto(
        Long id,
        String title,
        String description,
        String venue,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean registrationOpen,
        boolean walkinOpen,
        boolean walkinAllowed,
        boolean enableEntry,
        boolean enableFood,
        boolean enableGoodies
) {
    public static PublicEventDto from(Event event) {
        return new PublicEventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getStartTime(),
                event.getEndTime(),
                event.isRegistrationOpen(),
                event.isWalkinOpen(),
                event.isWalkinAllowed(),
                event.isEnableEntry(),
                event.isEnableFood(),
                event.isEnableGoodies()
        );
    }
}
