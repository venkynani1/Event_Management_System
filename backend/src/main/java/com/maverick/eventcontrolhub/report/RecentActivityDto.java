package com.maverick.eventcontrolhub.report;

import java.time.LocalDateTime;

public record RecentActivityDto(
        String type,
        String message,
        String actor,
        LocalDateTime occurredAt
) {
}
