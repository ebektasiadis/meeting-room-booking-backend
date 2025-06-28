package com.ebektasiadis.meetingroombooking.mapper;

import com.ebektasiadis.meetingroombooking.dto.BookingRequest;
import com.ebektasiadis.meetingroombooking.dto.BookingResponse;
import com.ebektasiadis.meetingroombooking.model.Booking;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.model.User;

public class BookingMapper {
    public static Booking toEntity(BookingRequest bookingRequest) {
        if (bookingRequest == null) {
            return null;
        }

        Booking booking = new Booking();

        User bookedBy = new User();
        bookedBy.setId(bookingRequest.userId());

        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(bookingRequest.meetingRoomId());

        booking.setStartTime(bookingRequest.startTime());
        booking.setEndTime(bookingRequest.endTime());
        booking.setPurpose(bookingRequest.purpose());
        booking.setBookedBy(bookedBy);
        booking.setMeetingRoom(meetingRoom);

        return booking;
    }

    public static BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        Long userId = booking.getBookedBy() != null ? booking.getBookedBy().getId() : null;
        Long meetingRoomId = booking.getMeetingRoom() != null ? booking.getMeetingRoom().getId() : null;

        return new BookingResponse(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPurpose(),
                userId,
                meetingRoomId
        );
    }
}
