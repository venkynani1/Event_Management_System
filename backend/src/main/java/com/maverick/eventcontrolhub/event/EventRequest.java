package com.maverick.eventcontrolhub.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String title,
        String description,
        @NotBlank String venue,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull LocalDateTime registrationOpenAt,
        @NotNull LocalDateTime registrationCloseAt,
        boolean registrationOpen,
        boolean walkinAllowed,
        boolean walkinOpen,
        LocalDateTime walkinCloseAt,
        boolean enableEntry,
        boolean enableFood,
        boolean enableGoodies,
        @Min(1) int maxCapacity
) {
}
