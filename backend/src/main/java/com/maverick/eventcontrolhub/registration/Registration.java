package com.maverick.eventcontrolhub.registration;

import com.maverick.eventcontrolhub.common.RegistrationSource;
import com.maverick.eventcontrolhub.common.RegistrationStatus;
import com.maverick.eventcontrolhub.event.Event;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_registration_event_employee_id", columnNames = {"event_id", "employee_id"}),
        @UniqueConstraint(name = "uk_registration_event_employee_email", columnNames = {"event_id", "employee_email"}),
        @UniqueConstraint(name = "uk_registration_event_device", columnNames = {"event_id", "device_token"}),
        @UniqueConstraint(name = "uk_registration_qr_token", columnNames = "qr_token")
})
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationSource source;

    @Column(name = "qr_token", nullable = false)
    private String qrToken;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    protected Registration() {
    }

    public Registration(Event event, String employeeId, String employeeName, String employeeEmail,
                        RegistrationStatus status, RegistrationSource source, String qrToken,
                        String deviceToken, LocalDateTime registeredAt) {
        this.event = event;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.status = status;
        this.source = source;
        this.qrToken = qrToken;
        this.deviceToken = deviceToken;
        this.registeredAt = registeredAt;
    }

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public RegistrationStatus getStatus() { return status; }
    public RegistrationSource getSource() { return source; }
    public String getQrToken() { return qrToken; }
    public String getDeviceToken() { return deviceToken; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
