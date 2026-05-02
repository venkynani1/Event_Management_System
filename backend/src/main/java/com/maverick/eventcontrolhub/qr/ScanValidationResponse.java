package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.RegistrationStatus;

public record ScanValidationResponse(
        boolean valid,
        Long eventId,
        String eventTitle,
        Long registrationId,
        String employeeName,
        String employeeId,
        RegistrationStatus registrationStatus,
        boolean entryScanned,
        boolean foodScanned,
        boolean goodiesScanned,
        boolean foodEnabled,
        boolean goodiesEnabled,
        String message
) {
}
