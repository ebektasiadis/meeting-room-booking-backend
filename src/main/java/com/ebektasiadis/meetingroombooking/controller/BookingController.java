package com.ebektasiadis.meetingroombooking.controller;

import com.ebektasiadis.meetingroombooking.documentation.DocumentedExceptions;
import com.ebektasiadis.meetingroombooking.dto.BookingRequest;
import com.ebektasiadis.meetingroombooking.dto.BookingResponse;
import com.ebektasiadis.meetingroombooking.exception.booking.*;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Bookings API")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Get all bookings", description = "Returns a list of bookings")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get booking by id", description = "Returns a single booking")
    @DocumentedExceptions({BookingNotFoundException.class})
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable("id") Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Creates a new booking", description = "Returns the created booking")
    @DocumentedExceptions({BookingNotFoundException.class, UserNotFoundException.class, MeetingRoomNotFoundException.class, BookingInvalidDateException.class, BookingPastStartDateException.class, BookingPastEndDateException.class, BookingDateConflictException.class})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse createdBooking = bookingService.createBooking(bookingRequest);

        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBooking.id())
                .toUri();

        return ResponseEntity.created(resourceLocation).body(createdBooking);
    }

    @Operation(summary = "Updates an existing booking", description = "Returns the updated booking")
    @DocumentedExceptions({BookingNotFoundException.class, UserNotFoundException.class, MeetingRoomNotFoundException.class, BookingInvalidDateException.class, BookingPastStartDateException.class, BookingPastEndDateException.class, BookingDateConflictException.class})
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable("id") Long id, @Valid @RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingRequest));
    }

    @Operation(summary = "Deletes an existing booking")
    @DocumentedExceptions({BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
