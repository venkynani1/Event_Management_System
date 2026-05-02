package com.maverick.eventcontrolhub.dashboard;

import com.maverick.eventcontrolhub.auth.MockAuthService;
import com.maverick.eventcontrolhub.common.Role;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final MockAuthService authService;

    public DashboardController(DashboardService dashboardService, MockAuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping
    public DashboardSummary dashboard(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                      @PathVariable Long eventId) {
        authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        return dashboardService.forEvent(eventId);
    }
}
