package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.employee.Employee;
import com.maverick.eventcontrolhub.event.Event;
import com.maverick.eventcontrolhub.registration.Registration;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "entitlement_claims")
public class EntitlementClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "registration_id")
    private Registration registration;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entitlement_type_id")
    private EntitlementType entitlementType;

    @Column(nullable = false)
    private LocalDateTime claimedAt;

    @Column(nullable = false)
    private String counterName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "claimed_by_id")
    private Employee claimedBy;

    protected EntitlementClaim() {
    }

    public EntitlementClaim(Event event, Registration registration, EntitlementType entitlementType,
                            LocalDateTime claimedAt, String counterName, Employee claimedBy) {
        this.event = event;
        this.registration = registration;
        this.entitlementType = entitlementType;
        this.claimedAt = claimedAt;
        this.counterName = counterName;
        this.claimedBy = claimedBy;
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

    public EntitlementType getEntitlementType() {
        return entitlementType;
    }

    public LocalDateTime getClaimedAt() {
        return claimedAt;
    }

    public String getCounterName() {
        return counterName;
    }

    public Employee getClaimedBy() {
        return claimedBy;
    }
}
