package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.*;
import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import com.maverick.eventcontrolhub.registration.RegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ScanService {
    private final RegistrationRepository registrationRepository;
    private final StageScanRepository stageScanRepository;

    public ScanService(RegistrationRepository registrationRepository,
                       StageScanRepository stageScanRepository) {
        this.registrationRepository = registrationRepository;
        this.stageScanRepository = stageScanRepository;
    }

    @Transactional(readOnly = true)
    public ScanValidationResponse validate(String qrToken) {
        Registration registration = findUsableRegistration(qrToken);
        return validationResponse(registration, "QR token is valid");
    }

    @Transactional
    public StageScanDto stage(ScanRequest request, Employee scanner) {
        Registration registration = findUsableRegistration(request.qrToken());
        ensureStageEnabled(registration.getEvent(), request.stage());
        if (stageScanRepository.existsByRegistrationAndStage(registration, request.stage())) {
            throw new ApiException(HttpStatus.CONFLICT, request.stage() + " already used for this QR");
        }

        StageScan scan = stageScanRepository.save(new StageScan(
                registration.getEvent(),
                registration,
                request.stage(),
                LocalDateTime.now(),
                scanner,
                request.locationName() == null || request.locationName().isBlank() ? request.stage().name() : request.locationName()
        ));
        return StageScanDto.from(scan);
    }

    private Registration findUsableRegistration(String qrToken) {
        Registration registration = registrationRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "QR token was not found"));
        Event event = registration.getEvent();
        if (event.getStatus() != EventStatus.OPEN && event.getStatus() != EventStatus.LIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Event is not active for scanning");
        }
        if (registration.getStatus() != RegistrationStatus.REGISTERED && registration.getStatus() != RegistrationStatus.WALKIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Registration is not valid for scanning");
        }
        return registration;
    }

    private void ensureStageEnabled(Event event, Stage stage) {
        if (stage == Stage.ENTRY && !event.isEnableEntry()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ENTRY scanning is disabled for this event");
        }
        if (stage == Stage.FOOD && !event.isEnableFood()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "FOOD scanning is disabled for this event");
        }
        if (stage == Stage.GOODIES && !event.isEnableGoodies()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "GOODIES scanning is disabled for this event");
        }
    }

    private ScanValidationResponse validationResponse(Registration registration, String message) {
        return new ScanValidationResponse(
                true,
                registration.getEvent().getId(),
                registration.getEvent().getTitle(),
                registration.getId(),
                registration.getEmployeeName(),
                registration.getEmployeeId(),
                registration.getStatus(),
                stageScanRepository.existsByRegistrationAndStage(registration, Stage.ENTRY),
                stageScanRepository.existsByRegistrationAndStage(registration, Stage.FOOD),
                stageScanRepository.existsByRegistrationAndStage(registration, Stage.GOODIES),
                registration.getEvent().isEnableFood(),
                registration.getEvent().isEnableGoodies(),
                message
        );
    }
}
