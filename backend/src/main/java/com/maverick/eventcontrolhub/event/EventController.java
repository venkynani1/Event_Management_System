package com.maverick.eventcontrolhub.event;

import com.maverick.eventcontrolhub.auth.MockAuthService;
import com.maverick.eventcontrolhub.common.Role;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final MockAuthService authService;

    public EventController(EventService eventService, MockAuthService authService) {
        this.eventService = eventService;
        this.authService = authService;
    }

    @PostMapping
    public EventDto create(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                           @Valid @RequestBody EventRequest request) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.create(request);
    }

    @GetMapping
    public List<EventDto> all() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventDto one(@PathVariable Long id) {
        return eventService.findDto(id);
    }

    @PutMapping("/{id}")
    public EventDto update(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                           @PathVariable Long id,
                           @Valid @RequestBody EventRequest request) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.update(id, request);
    }

    @PostMapping("/{id}/open")
    public EventDto open(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                         @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.open(id);
    }

    @PostMapping("/{id}/complete")
    public EventDto complete(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                             @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.complete(id);
    }

    @PostMapping("/{id}/registration/open")
    public EventDto openRegistration(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                     @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.openRegistration(id);
    }

    @PostMapping("/{id}/registration/close")
    public EventDto closeRegistration(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                      @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.closeRegistration(id);
    }

    @PostMapping("/{id}/registration/extend")
    public EventDto extendRegistration(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                       @PathVariable Long id,
                                       @RequestBody ExtendRequest request) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.extendRegistration(id, request.closeAt());
    }

    @PostMapping("/{id}/walkins/open")
    public EventDto openWalkins(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.openWalkins(id);
    }

    @PostMapping("/{id}/walkins/close")
    public EventDto closeWalkins(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                 @PathVariable Long id) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.closeWalkins(id);
    }

    @PostMapping("/{id}/walkins/extend")
    public EventDto extendWalkins(@RequestHeader(value = MockAuthService.MOCK_AUTH_HEADER, required = false) String employeeCode,
                                  @PathVariable Long id,
                                  @RequestBody ExtendRequest request) {
        authService.requireRole(employeeCode, Role.HR_ADMIN);
        return eventService.extendWalkins(id, request.closeAt());
    }

    public record ExtendRequest(LocalDateTime closeAt) {
    }
}
