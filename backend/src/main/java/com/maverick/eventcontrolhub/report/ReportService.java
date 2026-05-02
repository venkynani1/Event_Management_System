package com.maverick.eventcontrolhub.report;

import com.maverick.eventcontrolhub.common.RegistrationSource;
import com.maverick.eventcontrolhub.common.Stage;
import com.maverick.eventcontrolhub.dashboard.DashboardService;
import com.maverick.eventcontrolhub.dashboard.DashboardSummary;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.event.EventService;
import com.maverick.eventcontrolhub.qr.StageScan;
import com.maverick.eventcontrolhub.qr.StageScanRepository;
import com.maverick.eventcontrolhub.registration.Registration;
import com.maverick.eventcontrolhub.registration.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final EventService eventService;
    private final DashboardService dashboardService;
    private final RegistrationRepository registrationRepository;
    private final StageScanRepository stageScanRepository;

    public ReportService(EventService eventService,
                         DashboardService dashboardService,
                         RegistrationRepository registrationRepository,
                         StageScanRepository stageScanRepository) {
        this.eventService = eventService;
        this.dashboardService = dashboardService;
        this.registrationRepository = registrationRepository;
        this.stageScanRepository = stageScanRepository;
    }

    @Transactional(readOnly = true)
    public ReportSummaryDto summary(Long eventId) {
        DashboardSummary dashboard = dashboardService.forEvent(eventId);
        Event event = eventService.find(eventId);
        return new ReportSummaryDto(
                dashboard.totalEvents(),
                dashboard.eventId(),
                dashboard.eventTitle(),
                dashboard.registeredCount(),
                dashboard.normalRegistrationCount(),
                dashboard.checkedInCount(),
                dashboard.foodClaimedCount(),
                dashboard.goodiesClaimedCount(),
                dashboard.walkInCount(),
                dashboard.pendingCheckInCount(),
                dashboard.registrationOpen(),
                dashboard.walkinOpen(),
                dashboard.enableEntry(),
                dashboard.enableFood(),
                dashboard.enableGoodies(),
                recentActivity(event)
        );
    }

    @Transactional(readOnly = true)
    public byte[] exportCsv(Long eventId) {
        Event event = eventService.find(eventId);

        StringBuilder csv = new StringBuilder();
        csv.append(header(event)).append('\n');

        for (Registration registration : registrationRepository.findByEventOrderByRegisteredAtDesc(event)) {
            Optional<StageScan> entry = stageScanRepository.findByRegistrationAndStage(registration, Stage.ENTRY);
            Optional<StageScan> food = stageScanRepository.findByRegistrationAndStage(registration, Stage.FOOD);
            Optional<StageScan> goodies = stageScanRepository.findByRegistrationAndStage(registration, Stage.GOODIES);

            java.util.ArrayList<String> values = new java.util.ArrayList<>(List.of(
                    registration.getEmployeeId(),
                    registration.getEmployeeName(),
                    registration.getEmployeeEmail(),
                    registration.getSource() == RegistrationSource.WALKIN ? "WALKIN" : "NORMAL",
                    format(registration.getRegisteredAt()),
                    registration.getQrToken(),
                    entry.isPresent() ? "Yes" : "No",
                    entry.map(value -> format(value.getScannedAt())).orElse("")
            ));
            if (event.isEnableFood()) {
                values.add(food.isPresent() ? "Yes" : "No");
                values.add(food.map(value -> format(value.getScannedAt())).orElse(""));
            }
            if (event.isEnableGoodies()) {
                values.add(goodies.isPresent() ? "Yes" : "No");
                values.add(goodies.map(value -> format(value.getScannedAt())).orElse(""));
            }

            csv.append(row(values.toArray(String[]::new))).append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public String exportFilename(Long eventId) {
        Event event = eventService.find(eventId);
        String slug = event.getTitle().toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return (slug.isBlank() ? "event" : slug) + "-report.csv";
    }

    private List<RecentActivityDto> recentActivity(Event event) {
        List<RecentActivityDto> registrationActivity = registrationRepository.findByEventOrderByRegisteredAtDesc(event).stream()
                .map(registration -> new RecentActivityDto(
                        registration.getSource().name().equals("WALKIN") ? "WALKIN" : "REGISTRATION",
                        registration.getEmployeeName() + " registered for " + event.getTitle(),
                        registration.getEmployeeName(),
                        registration.getRegisteredAt()
                ))
                .toList();

        List<RecentActivityDto> scanActivity = stageScanRepository.findByEventOrderByScannedAtDesc(event).stream()
                .map(scan -> new RecentActivityDto(
                        scan.getStage().name(),
                        scan.getRegistration().getEmployeeName() + " completed " + scan.getStage().name() + " at " + scan.getLocationName(),
                        scan.getScannedBy().getName(),
                        scan.getScannedAt()
                ))
                .toList();

        return java.util.stream.Stream.of(registrationActivity, scanActivity)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(RecentActivityDto::occurredAt).reversed())
                .limit(10)
                .toList();
    }

    private String header(Event event) {
        java.util.ArrayList<String> headers = new java.util.ArrayList<>(List.of(
                "employeeId",
                "employeeName",
                "employeeEmail",
                "registration source",
                "registeredAt",
                "QR token",
                "entry status",
                "entry time"
        ));
        if (event.isEnableFood()) {
            headers.add("food status");
            headers.add("food time");
        }
        if (event.isEnableGoodies()) {
            headers.add("goodies status");
            headers.add("goodies time");
        }
        return String.join(",", headers);
    }

    private String row(String... values) {
        return String.join(",", java.util.Arrays.stream(values).map(this::cell).toList());
    }

    private String cell(String value) {
        String escaped = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String format(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }
}
