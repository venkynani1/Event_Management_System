package com.maverick.eventcontrolhub.qr;

import com.maverick.eventcontrolhub.common.Stage;
import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stage_scans", uniqueConstraints = {
        @UniqueConstraint(name = "uk_stage_scan_event_registration_stage", columnNames = {"event_id", "registration_id", "stage"})
})
public class StageScan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "scanned_by_id")
    private Employee scannedBy;

    @Column(nullable = false)
    private String locationName;

    protected StageScan() {
    }

    public StageScan(Event event, Registration registration, Stage stage, LocalDateTime scannedAt,
                     Employee scannedBy, String locationName) {
        this.event = event;
        this.registration = registration;
        this.stage = stage;
        this.scannedAt = scannedAt;
        this.scannedBy = scannedBy;
        this.locationName = locationName;
    }

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public Registration getRegistration() { return registration; }
    public Stage getStage() { return stage; }
    public LocalDateTime getScannedAt() { return scannedAt; }
    public Employee getScannedBy() { return scannedBy; }
    public String getLocationName() { return locationName; }
}
