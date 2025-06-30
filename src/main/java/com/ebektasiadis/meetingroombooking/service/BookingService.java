package com.ebektasiadis.meetingroombooking.service;

import com.ebektasiadis.meetingroombooking.dto.BookingRequest;
import com.ebektasiadis.meetingroombooking.dto.BookingResponse;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingDateConflictException;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingInvalidDateException;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;

import java.util.List;


public interface BookingService {
    List<BookingResponse> getAllBookings();

    BookingResponse getBookingById(Long id) throws BookingNotFoundException;

    BookingResponse createBooking(BookingRequest bookingRequest) throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException;

    BookingResponse updateBooking(Long id, BookingRequest bookingRequest) throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException;

    void deleteBooking(Long id) throws BookingNotFoundException;
}
