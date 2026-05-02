package com.maverick.eventcontrolhub.entitlement;

import com.maverick.eventcontrolhub.event.Event;
import jakarta.persistence.*;

@Entity
@Table(name = "event_entitlements", uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_entitlement_type", columnNames = {"event_id", "entitlement_type_id"})
})
public class EventEntitlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entitlement_type_id")
    private EntitlementType entitlementType;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int perPersonLimit;

    protected EventEntitlement() {
    }

    public EventEntitlement(Event event, EntitlementType entitlementType, int totalQuantity, int perPersonLimit) {
        this.event = event;
        this.entitlementType = entitlementType;
        this.totalQuantity = totalQuantity;
        this.perPersonLimit = perPersonLimit;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public EntitlementType getEntitlementType() {
        return entitlementType;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getPerPersonLimit() {
        return perPersonLimit;
    }
}
