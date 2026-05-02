package com.maverick.eventcontrolhub.checkin;

import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_ins", uniqueConstraints = {
        @UniqueConstraint(name = "uk_checkin_registration", columnNames = "registration_id")
})
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @Column(nullable = false)
    private LocalDateTime checkedInAt;

    @Column(nullable = false)
    private String gateName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "scanned_by_id")
    private Employee scannedBy;

    protected CheckIn() {
    }

    public CheckIn(Event event, Registration registration, LocalDateTime checkedInAt, String gateName, Employee scannedBy) {
        this.event = event;
        this.registration = registration;
        this.checkedInAt = checkedInAt;
        this.gateName = gateName;
        this.scannedBy = scannedBy;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public Registration getRegistration() {
        return registration;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public String getGateName() {
        return gateName;
    }

    public Employee getScannedBy() {
        return scannedBy;
    }
}
