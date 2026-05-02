package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.Stage;

import java.time.LocalDateTime;

public record StageScanDto(
        Long id,
        Long eventId,
        Long registrationId,
        Stage stage,
        LocalDateTime scannedAt,
        String locationName,
        String scannedBy
) {
    public static StageScanDto from(StageScan scan) {
        return new StageScanDto(
                scan.getId(),
                scan.getEvent().getId(),
                scan.getRegistration().getId(),
                scan.getStage(),
                scan.getScannedAt(),
                scan.getLocationName(),
                scan.getScannedBy().getName()
        );
    }
}
