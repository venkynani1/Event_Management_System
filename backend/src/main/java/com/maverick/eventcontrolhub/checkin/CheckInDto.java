package com.maverick.eventcontrolhub.checkin;

import java.time.LocalDateTime;

public record CheckInDto(
        Long id,
        Long eventId,
        Long registrationId,
        LocalDateTime checkedInAt,
        String gateName,
        String scannedBy
) {
    public static CheckInDto from(CheckIn checkIn) {
        return new CheckInDto(
                checkIn.getId(),
                checkIn.getEvent().getId(),
                checkIn.getRegistration().getId(),
                checkIn.getCheckedInAt(),
                checkIn.getGateName(),
                checkIn.getScannedBy().getName()
        );
    }
}
