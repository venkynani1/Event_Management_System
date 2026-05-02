package com.maverick.eventcontrolhub.dashboard;

public record DashboardSummary(
        long totalEvents,
        Long eventId,
        String eventTitle,
        long registeredCount,
        long normalRegistrationCount,
        long checkedInCount,
        long foodClaimedCount,
        long goodiesClaimedCount,
        long walkInCount,
        long pendingCheckInCount,
        boolean registrationOpen,
        boolean walkinOpen,
        boolean enableEntry,
        boolean enableFood,
        boolean enableGoodies
) {
}
