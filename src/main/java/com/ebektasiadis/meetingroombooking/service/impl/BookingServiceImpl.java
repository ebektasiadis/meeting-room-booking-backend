package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.BookingRequest;
import com.ebektasiadis.meetingroombooking.dto.BookingResponse;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingDateConflictException;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingInvalidDateException;
import com.ebektasiadis.meetingroombooking.exception.booking.BookingNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.mapper.BookingMapper;
import com.ebektasiadis.meetingroombooking.model.Booking;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.model.User;
import com.ebektasiadis.meetingroombooking.repository.BookingRepository;
import com.ebektasiadis.meetingroombooking.repository.MeetingRoomRepository;
import com.ebektasiadis.meetingroombooking.repository.UserRepository;
import com.ebektasiadis.meetingroombooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, MeetingRoomRepository meetingRoomRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(BookingMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public BookingResponse getBookingById(Long id) throws BookingNotFoundException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        return BookingMapper.toResponse(booking);
    }

    private void validateAndPrepareBooking(Booking booking) throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException {
        validateAndPrepareBooking(booking, null);
    }

    private void validateAndPrepareBooking(Booking booking, Long bookingIdToExclude)
            throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException {


        Long userId = booking.getBookedBy().getId();
        User bookedBy = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        booking.setBookedBy(bookedBy);


        Long meetingRoomId = booking.getMeetingRoom().getId();
        MeetingRoom meetingRoom = meetingRoomRepository.findById(meetingRoomId)
                .orElseThrow(() -> new MeetingRoomNotFoundException(meetingRoomId));
        booking.setMeetingRoom(meetingRoom);

        LocalDateTime startTime = booking.getStartTime();
        LocalDateTime endTime = booking.getEndTime();

        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new BookingInvalidDateException(startTime, endTime);
        }

        List<Booking> conflictingBookings;
        if (bookingIdToExclude != null) {
            conflictingBookings = bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(
                    booking.getMeetingRoom(), endTime, startTime, bookingIdToExclude
            );
        } else {
            conflictingBookings = bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(
                    booking.getMeetingRoom(), endTime, startTime
            );
        }

        if (!conflictingBookings.isEmpty()) {
            throw new BookingDateConflictException(booking.getMeetingRoom().getId());
        }
    }

    @Override
    public BookingResponse createBooking(BookingRequest bookingRequest) throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException {
        Booking booking = BookingMapper.toEntity(bookingRequest);

        validateAndPrepareBooking(booking);

        booking = bookingRepository.save(booking);

        return BookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest bookingRequest) throws BookingNotFoundException, UserNotFoundException, MeetingRoomNotFoundException, BookingInvalidDateException, BookingDateConflictException {
        Booking booking = BookingMapper.toEntity(bookingRequest);

        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        validateAndPrepareBooking(booking, id);

        existingBooking.setStartTime(booking.getStartTime());
        existingBooking.setEndTime(booking.getEndTime());
        existingBooking.setPurpose(booking.getPurpose());
        existingBooking.setBookedBy(booking.getBookedBy());
        existingBooking.setMeetingRoom(booking.getMeetingRoom());

        existingBooking = bookingRepository.save(existingBooking);

        return BookingMapper.toResponse(existingBooking);
    }

    @Override
    public void deleteBooking(Long id) throws BookingNotFoundException {
        bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        bookingRepository.deleteById(id);
    }
}
