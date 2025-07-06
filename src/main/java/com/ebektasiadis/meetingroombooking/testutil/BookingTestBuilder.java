package com.ebektasiadis.meetingroombooking.testutil;

import com.ebektasiadis.meetingroombooking.model.Booking;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.model.User;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.ebektasiadis.meetingroombooking.testutil.MeetingRoomTestBuilder.aMeetingRoom;
import static com.ebektasiadis.meetingroombooking.testutil.UserTestBuilder.aUser;

public class BookingTestBuilder {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private User bookedBy;
    private MeetingRoom meetingRoom;

    private BookingTestBuilder() {
        this.id = 1L;
        this.startTime = LocalDateTime.now(FIXED_CLOCK);
        this.endTime = LocalDateTime.now(FIXED_CLOCK).plusHours(2);
        this.purpose = "Default Purpose";
        this.bookedBy = aUser().build();
        this.meetingRoom = aMeetingRoom().build();
    }

    private BookingTestBuilder(BookingTestBuilder bookingTestBuilder) {
        this.id = bookingTestBuilder.id;
        this.startTime = bookingTestBuilder.startTime;
        this.endTime = bookingTestBuilder.endTime;
        this.purpose = bookingTestBuilder.purpose;
        this.bookedBy = bookingTestBuilder.bookedBy;
        this.meetingRoom = bookingTestBuilder.meetingRoom;
    }

    public static BookingTestBuilder aBooking() {
        return new BookingTestBuilder();
    }

    public BookingTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BookingTestBuilder withStartTime(LocalDateTime startDate) {
        this.startTime = startDate;
        return this;
    }

    public BookingTestBuilder withEndTime(LocalDateTime endDate) {
        this.endTime = endDate;
        return this;
    }

    public BookingTestBuilder withPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public BookingTestBuilder withBookedBy(User bookedBy) {
        this.bookedBy = bookedBy;
        return this;
    }

    public BookingTestBuilder withMeetingRoom(MeetingRoom meetingRoom) {
        this.meetingRoom = meetingRoom;
        return this;
    }

    public BookingTestBuilder with(User bookedBy) {
        this.bookedBy = bookedBy;
        return this;
    }

    public BookingTestBuilder with(MeetingRoom meetingRoom) {
        this.meetingRoom = meetingRoom;
        return this;
    }

    public Booking build() {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setBookedBy(bookedBy);
        booking.setMeetingRoom(meetingRoom);
        return booking;
    }

    public BookingTestBuilder but() {
        return new BookingTestBuilder(this);
    }
}
