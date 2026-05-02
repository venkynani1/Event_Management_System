package com.maverick.eventcontrolhub.report;

import com.maverick.eventcontrolhub.auth.MockAuthService;
import com.maverick.eventcontrolhub.common.Role;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/reports")
public class ReportController {
    private final ReportService reportService;
    private final MockAuthService authService;

    public ReportController(ReportService reportService, MockAuthService authService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @GetMapping("/summary")
    public ReportSummaryDto summary(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                    @PathVariable Long eventId) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return reportService.summary(eventId);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                         @PathVariable Long eventId) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        byte[] csv = reportService.exportCsv(eventId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(reportService.exportFilename(eventId))
                        .build()
                        .toString())
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }
}
