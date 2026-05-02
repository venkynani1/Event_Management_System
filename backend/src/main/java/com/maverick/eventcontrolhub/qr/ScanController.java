package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.auth.MockAuthService;
import com.maverick.eventcontrolhub.common.Role;
import com.maverick.eventcontrolhub.common.Stage;
import com.maverick.eventcontrolhub.employee.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scan")
public class ScanController {
    private final ScanService scanService;
    private final MockAuthService authService;

    public ScanController(ScanService scanService, MockAuthService authService) {
        this.scanService = scanService;
        this.authService = authService;
    }

    @PostMapping("/validate")
    public ScanValidationResponse validate(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                           @Valid @RequestBody ValidateScanRequest request) {
        authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        return scanService.validate(request.qrToken());
    }

    @PostMapping("/stage")
    public StageScanDto stage(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                              @Valid @RequestBody ScanRequest request) {
        Employee scanner = authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        return scanService.stage(request, scanner);
    }

    @PostMapping("/checkin")
    public StageScanDto checkIn(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                @RequestBody LegacyScanRequest request) {
        Employee scanner = authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        return scanService.stage(new ScanRequest(request.qrToken(), Stage.ENTRY, request.stationName()), scanner);
    }

    @PostMapping("/claim")
    public StageScanDto claim(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                              @RequestBody LegacyScanRequest request) {
        Employee scanner = authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        Stage stage = request.entitlementCode() == null ? request.stage() : Stage.valueOf(request.entitlementCode());
        return scanService.stage(new ScanRequest(request.qrToken(), stage, request.stationName()), scanner);
    }

    public record LegacyScanRequest(String qrToken, String entitlementCode, Stage stage, String stationName) {
    }

    public record ValidateScanRequest(@NotBlank String qrToken) {
    }
}
