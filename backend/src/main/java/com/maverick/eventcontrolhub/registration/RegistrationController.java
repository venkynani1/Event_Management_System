package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.auth.MockAuthService;
import com.maverick.eventcontrolhub.common.Role;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {
    private final RegistrationService registrationService;
    private final MockAuthService authService;

    public RegistrationController(RegistrationService registrationService, MockAuthService authService) {
        this.registrationService = registrationService;
        this.authService = authService;
    }

    @GetMapping("/api/public/events/{registrationSlug}")
    public PublicEventDto publicEvent(@PathVariable String registrationSlug) {
        return registrationService.publicEvent(registrationSlug);
    }

    @PostMapping("/api/public/events/{registrationSlug}/register")
    public RegistrationDto registerPublic(@PathVariable String registrationSlug,
                                          @Valid @RequestBody PublicRegistrationRequest request) {
        return registrationService.registerPublic(registrationSlug, request);
    }

    @GetMapping("/api/public/walkins/{walkinSlug}")
    public PublicEventDto publicWalkinEvent(@PathVariable String walkinSlug) {
        return registrationService.publicWalkinEvent(walkinSlug);
    }

    @PostMapping("/api/public/walkins/{walkinSlug}/register")
    public RegistrationDto registerWalkin(@PathVariable String walkinSlug,
                                          @Valid @RequestBody PublicRegistrationRequest request) {
        return registrationService.registerWalkin(walkinSlug, request);
    }

    @GetMapping("/api/events/{eventId}/registrations")
    public List<RegistrationDto> registrations(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                               @PathVariable Long eventId) {
        authService.requireAnyRole(employeeCode, Role.HR_ADMIN, Role.SCANNER_OPERATOR);
        return registrationService.registrations(eventId);
    }
}
