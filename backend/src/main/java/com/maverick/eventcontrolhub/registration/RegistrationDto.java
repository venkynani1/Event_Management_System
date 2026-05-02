package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.common.RegistrationSource;
import com.maverick.eventcontrolhub.common.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationDto(
        Long id,
        Long eventId,
        String employeeId,
        String employeeName,
        String employeeEmail,
        RegistrationStatus status,
        RegistrationSource source,
        String qrToken,
        String deviceToken,
        LocalDateTime registeredAt
) {
    public static RegistrationDto from(Registration registration) {
        return new RegistrationDto(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getEmployeeId(),
                registration.getEmployeeName(),
                registration.getEmployeeEmail(),
                registration.getStatus(),
                registration.getSource(),
                registration.getQrToken(),
                registration.getDeviceToken(),
                registration.getRegisteredAt()
        );
    }
}
