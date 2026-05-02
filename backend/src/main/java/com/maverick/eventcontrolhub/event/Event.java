package com.maverick.eventcontrolhub.event;

import com.maverick.eventcontrolhub.common.EventStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events", uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_registration_slug", columnNames = "registration_slug"),
        @UniqueConstraint(name = "uk_event_walkin_slug", columnNames = "walkin_slug")
})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime registrationOpenAt;

    @Column(nullable = false)
    private LocalDateTime registrationCloseAt;

    @Column(nullable = false)
    private boolean registrationOpen;

    @Column(nullable = false)
    private boolean walkinAllowed;

    @Column(nullable = false)
    private boolean walkinOpen;

    private LocalDateTime walkinCloseAt;

    @Column(nullable = false)
    private boolean enableEntry = true;

    @Column(nullable = false)
    private boolean enableFood;

    @Column(nullable = false)
    private boolean enableGoodies;

    @Column(name = "registration_slug", nullable = false)
    private String registrationSlug;

    @Column(name = "walkin_slug")
    private String walkinSlug;

    @Column(nullable = false)
    private int maxCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;

    protected Event() {
    }

    public Event(String title, String description, String venue, LocalDateTime startTime, LocalDateTime endTime,
                 LocalDateTime registrationOpenAt, LocalDateTime registrationCloseAt, boolean registrationOpen,
                 boolean walkinAllowed, boolean walkinOpen, LocalDateTime walkinCloseAt, boolean enableEntry,
                 boolean enableFood, boolean enableGoodies, String registrationSlug, String walkinSlug,
                 int maxCapacity, EventStatus status) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registrationOpenAt = registrationOpenAt;
        this.registrationCloseAt = registrationCloseAt;
        this.registrationOpen = registrationOpen;
        this.walkinAllowed = walkinAllowed;
        this.walkinOpen = walkinOpen;
        this.walkinCloseAt = walkinCloseAt;
        this.enableEntry = enableEntry;
        this.enableFood = enableFood;
        this.enableGoodies = enableGoodies;
        this.registrationSlug = registrationSlug;
        this.walkinSlug = walkinSlug;
        this.maxCapacity = maxCapacity;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getRegistrationOpenAt() { return registrationOpenAt; }
    public void setRegistrationOpenAt(LocalDateTime registrationOpenAt) { this.registrationOpenAt = registrationOpenAt; }
    public LocalDateTime getRegistrationCloseAt() { return registrationCloseAt; }
    public void setRegistrationCloseAt(LocalDateTime registrationCloseAt) { this.registrationCloseAt = registrationCloseAt; }
    public boolean isRegistrationOpen() { return registrationOpen; }
    public void setRegistrationOpen(boolean registrationOpen) { this.registrationOpen = registrationOpen; }
    public boolean isWalkinAllowed() { return walkinAllowed; }
    public void setWalkinAllowed(boolean walkinAllowed) { this.walkinAllowed = walkinAllowed; }
    public boolean isWalkinOpen() { return walkinOpen; }
    public void setWalkinOpen(boolean walkinOpen) { this.walkinOpen = walkinOpen; }
    public LocalDateTime getWalkinCloseAt() { return walkinCloseAt; }
    public void setWalkinCloseAt(LocalDateTime walkinCloseAt) { this.walkinCloseAt = walkinCloseAt; }
    public boolean isEnableEntry() { return enableEntry; }
    public void setEnableEntry(boolean enableEntry) { this.enableEntry = enableEntry; }
    public boolean isEnableFood() { return enableFood; }
    public void setEnableFood(boolean enableFood) { this.enableFood = enableFood; }
    public boolean isEnableGoodies() { return enableGoodies; }
    public void setEnableGoodies(boolean enableGoodies) { this.enableGoodies = enableGoodies; }
    public String getRegistrationSlug() { return registrationSlug; }
    public void setRegistrationSlug(String registrationSlug) { this.registrationSlug = registrationSlug; }
    public String getWalkinSlug() { return walkinSlug; }
    public void setWalkinSlug(String walkinSlug) { this.walkinSlug = walkinSlug; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
}
