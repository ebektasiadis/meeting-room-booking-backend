package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.BookingRequest;
import com.ebektasiadis.meetingroombooking.dto.BookingResponse;
import com.ebektasiadis.meetingroombooking.exception.booking.*;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.exception.user.UserNotFoundException;
import com.ebektasiadis.meetingroombooking.model.Booking;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.model.User;
import com.ebektasiadis.meetingroombooking.repository.BookingRepository;
import com.ebektasiadis.meetingroombooking.repository.MeetingRoomRepository;
import com.ebektasiadis.meetingroombooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ebektasiadis.meetingroombooking.testutil.BookingTestBuilder.aBooking;
import static com.ebektasiadis.meetingroombooking.testutil.MeetingRoomTestBuilder.aMeetingRoom;
import static com.ebektasiadis.meetingroombooking.testutil.UserTestBuilder.aUser;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService")
public class BookingServiceImplTest {

    private static final ZonedDateTime NOW = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MeetingRoomRepository meetingRoomRepository;

    @Mock
    Clock clock;

    @InjectMocks
    BookingServiceImpl bookingService;

    User userJohnDoe;
    MeetingRoom meetingRoomFirst;
    Booking bookingFirst;
    Booking bookingSecond;

    Long nonExistingUserId;
    Long nonExistingMeetingRoomId;
    Long nonExistingBookingId;

    @BeforeEach
    void setUp() {
        nonExistingUserId = 999L;
        nonExistingMeetingRoomId = 999L;
        nonExistingBookingId = 999L;

        lenient().when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        lenient().when(clock.instant()).thenReturn(NOW.toInstant());

        userJohnDoe = aUser()
                .withId(1L)
                .withUsername("john_doe")
                .withEmail("john@doe.com")
                .build();

        meetingRoomFirst = aMeetingRoom()
                .withId(1L)
                .withName("Meeting Room 1")
                .withCapacity(5)
                .withLocation("Thessaloniki, Greece")
                .withHasProjector(true)
                .withHasWhiteboard(true)
                .build();

        bookingFirst = aBooking()
                .withId(1L)
                .withPurpose("Onboarding")
                .withMeetingRoom(meetingRoomFirst)
                .withBookedBy(userJohnDoe)
                .build();

        bookingSecond = aBooking()
                .withId(2L)
                .build();
    }

    @Nested
    @DisplayName("getAllBookings method")
    class GetAllBookings {

        @Test
        @DisplayName("should return all bookings")
        void getAllBookings_existingBookings_returnsAllBookings() {
            List<Booking> bookings = Arrays.asList(bookingFirst, bookingSecond);

            when(bookingRepository.findAll()).thenReturn(bookings);

            List<BookingResponse> bookingResponses = bookingService.getAllBookings();

            assertThat(bookingResponses).isNotNull();
            assertThat(bookingResponses.size()).isEqualTo(bookings.size());
            assertThat(bookingResponses)
                    .extracting(BookingResponse::id, BookingResponse::purpose, BookingResponse::userId, BookingResponse::meetingRoomId)
                    .contains(tuple(bookingFirst.getId(), bookingFirst.getPurpose(), bookingFirst.getBookedBy().getId(), bookingFirst.getMeetingRoom().getId()))
                    .contains(tuple(bookingSecond.getId(), bookingSecond.getPurpose(), bookingSecond.getBookedBy().getId(), bookingSecond.getMeetingRoom().getId()));

            verify(bookingRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list if no bookings exist")
        void getAllBookings_nonExistingBookings_returnsEmptyList() {
            when(bookingRepository.findAll()).thenReturn(Collections.emptyList());

            List<BookingResponse> bookingResponses = bookingService.getAllBookings();

            assertThat(bookingResponses).isNotNull();
            assertThat(bookingResponses.size()).isEqualTo(0);

            verify(bookingRepository).findAll();
        }
    }

    @Nested
    @DisplayName("getBookedById method")
    class GetBookingById {

        @Test
        @DisplayName("should throw BookingNotFoundException if booking id does not exist")
        void getBookedById_nonExistingBooking_throwsBookingNotFoundException() {
            when(bookingRepository.findById(nonExistingBookingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.getBookingById(nonExistingBookingId))
                    .isExactlyInstanceOf(BookingNotFoundException.class)
                    .asInstanceOf(type(BookingNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getBookingId()).isEqualTo(nonExistingBookingId);
                    });

            verify(bookingRepository).findById(nonExistingBookingId);
        }

        @Test
        @DisplayName("should return booking if booking id exists")
        void getBookedById_existingBooking_returnsBooking() {
            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));

            BookingResponse bookingResponse = bookingService.getBookingById(bookingFirst.getId());

            assertThat(bookingResponse).isNotNull();
            assertThat(bookingResponse.userId()).isEqualTo(bookingFirst.getBookedBy().getId());
            assertThat(bookingResponse.meetingRoomId()).isEqualTo(meetingRoomFirst.getId());
            assertThat(bookingResponse)
                    .usingRecursiveComparison()
                    .ignoringFields("userId", "meetingRoomId")
                    .isEqualTo(bookingFirst);

            verify(bookingRepository).findById(bookingFirst.getId());
        }
    }

    @Nested
    @DisplayName("createBooking method")
    class CreateBooking {

        @Test
        @DisplayName("should throw UserNotFoundException if user id does not exist")
        void createBooking_nonExistingBookedBy_throwsUserNotFoundException() {
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), LocalDateTime.now(clock), "Onboarding", nonExistingUserId, meetingRoomFirst.getId());

            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(UserNotFoundException.class)
                    .asInstanceOf(type(UserNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getUserId()).isEqualTo(nonExistingUserId);
                    });

            verify(userRepository).findById(nonExistingMeetingRoomId);
            verify(meetingRoomRepository, never()).findById(any(Long.class));
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw MeetingRoomNotFoundException if meeting room id does not exist")
        void createBooking_nonExistingMeetingRoom_throwsMeetingRoomNotFoundException() {
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), LocalDateTime.now(clock), "Onboarding", userJohnDoe.getId(), nonExistingMeetingRoomId);

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(nonExistingMeetingRoomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(MeetingRoomNotFoundException.class)
                    .asInstanceOf(type(MeetingRoomNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getMeetingRoomId()).isEqualTo(nonExistingMeetingRoomId);
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(nonExistingMeetingRoomId);
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingPastStartDateException if start time has passed")
        void createBooking_pastStartDate_throwsBookingPastStartDateException() {
            LocalDateTime startTime = LocalDateTime.now(clock).minusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, LocalDateTime.now(clock), "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(BookingPastStartDateException.class)
                    .asInstanceOf(type(BookingPastStartDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartDate()).isEqualTo(startTime);
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingPastEndDateException if start time has passed")
        void createBooking_pastEndDate_throwsBookingPastEndDateException() {
            LocalDateTime endTime = LocalDateTime.now(clock).minusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(BookingPastEndDateException.class)
                    .asInstanceOf(type(BookingPastEndDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getEndDate()).isEqualTo(endTime);
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingInvalidDateException if startTime is after endTime")
        void createBooking_startDateBeforeEndDate_throwsBookingInvalidStartTimeException() {
            LocalDateTime startTime = LocalDateTime.now(clock).plusSeconds(1);
            LocalDateTime endTime = LocalDateTime.now(clock);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(BookingInvalidDateException.class)
                    .asInstanceOf(type(BookingInvalidDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartTime()).isEqualTo(startTime);
                        assertThat(ex.getEndTime()).isEqualTo(endTime);
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingInvalidDateException if startTime is equal to endTime")
        void createBooking_startDateEqualEndDate_throwsBookingInvalidStartTimeException() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(BookingInvalidDateException.class)
                    .asInstanceOf(type(BookingInvalidDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartTime()).isEqualTo(startTime);
                        assertThat(ex.getEndTime()).isEqualTo(endTime);
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingDateConflictException if there are conflict bookings")
        void createBooking_conflictBookingExist_throwsBookingDateConflictException() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock).plusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), bookingFirst.getMeetingRoom().getId());

            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));
            when(bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.singletonList(bookingFirst));

            assertThatThrownBy(() -> bookingService.createBooking(bookingRequest))
                    .isExactlyInstanceOf(BookingDateConflictException.class)
                    .asInstanceOf(type(BookingDateConflictException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getMeetingRoomId()).isEqualTo(bookingFirst.getMeetingRoom().getId());
                    });

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should return created booking")
        void createBooking_returnsBooking() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock).plusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), bookingFirst.getMeetingRoom().getId());
            Booking createdBooking = aBooking()
                    .withId(3L)
                    .withStartTime(bookingRequest.startTime())
                    .withEndTime(bookingRequest.endTime())
                    .withPurpose(bookingRequest.purpose())
                    .withBookedBy(userJohnDoe)
                    .withMeetingRoom(meetingRoomFirst)
                    .build();

            when(userRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(bookingRequest.meetingRoomId())).thenReturn(Optional.of(meetingRoomFirst));
            when(bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
            when(bookingRepository.save(any(Booking.class))).thenReturn(createdBooking);

            BookingResponse bookingResponse = bookingService.createBooking(bookingRequest);

            assertThat(bookingResponse).isNotNull();
            assertThat(bookingResponse.id()).isEqualTo(3L);
            assertThat(bookingResponse.userId()).isEqualTo(bookingRequest.userId());
            assertThat(bookingResponse.meetingRoomId()).isEqualTo(meetingRoomFirst.getId());
            assertThat(bookingResponse)
                    .usingRecursiveComparison()
                    .comparingOnlyFields("purpose", "startTime", "endTime")
                    .isEqualTo(createdBooking);

            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository).save(any(Booking.class));
        }
    }

    @Nested
    @DisplayName("updateBooking method")
    class UpdateBooking {

        @Test
        @DisplayName("should throw BookingNotFoundException if id does not exist")
        void updateBooking_nonExistingBooking_throwsBookingNotFoundException() {
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), LocalDateTime.now(clock), "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(bookingRepository.findById(nonExistingBookingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.updateBooking(nonExistingBookingId, bookingRequest))
                    .isExactlyInstanceOf(BookingNotFoundException.class)
                    .asInstanceOf(type(BookingNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getBookingId()).isEqualTo(nonExistingBookingId);
                    });
        }

        @Test
        @DisplayName("should throw UserNotFoundException if user id does not exist")
        void updateBooking_nonExistingBookedBy_throwsUserNotFoundException() {
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), LocalDateTime.now(clock), "Onboarding", nonExistingUserId, meetingRoomFirst.getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(UserNotFoundException.class)
                    .asInstanceOf(type(UserNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getUserId()).isEqualTo(nonExistingUserId);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(nonExistingMeetingRoomId);
            verify(meetingRoomRepository, never()).findById(any(Long.class));
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw MeetingRoomNotFoundException if meeting room id does not exist")
        void updateBooking_nonExistingMeetingRoom_throwsMeetingRoomNotFoundException() {
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), LocalDateTime.now(clock), "Onboarding", userJohnDoe.getId(), nonExistingMeetingRoomId);

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(nonExistingMeetingRoomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(MeetingRoomNotFoundException.class)
                    .asInstanceOf(type(MeetingRoomNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getMeetingRoomId()).isEqualTo(nonExistingMeetingRoomId);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(nonExistingMeetingRoomId);
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingPastStartDateException if start time has passed")
        void updateBooking_pastStartDate_throwsBookingPastStartDateException() {
            LocalDateTime startTime = LocalDateTime.now(clock).minusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, LocalDateTime.now(clock), "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(BookingPastStartDateException.class)
                    .asInstanceOf(type(BookingPastStartDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartDate()).isEqualTo(startTime);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingPastEndDateException if end time has passed")
        void updateBooking_pastEndDate_throwsBookingPastEndDateException() {
            LocalDateTime endTime = LocalDateTime.now(clock).minusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(LocalDateTime.now(clock), endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(BookingPastEndDateException.class)
                    .asInstanceOf(type(BookingPastEndDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getEndDate()).isEqualTo(endTime);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingInvalidDateException if startTime is after endTime")
        void updateBooking_startDateBeforeEndDate_throwsBookingInvalidStartTimeException() {
            LocalDateTime startTime = LocalDateTime.now(clock).plusSeconds(1);
            LocalDateTime endTime = LocalDateTime.now(clock);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(BookingInvalidDateException.class)
                    .asInstanceOf(type(BookingInvalidDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartTime()).isEqualTo(startTime);
                        assertThat(ex.getEndTime()).isEqualTo(endTime);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingInvalidDateException if startTime is equal to endTime")
        void updateBooking_startDateEqualEndDate_throwsBookingInvalidStartTimeException() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), meetingRoomFirst.getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(BookingInvalidDateException.class)
                    .asInstanceOf(type(BookingInvalidDateException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getStartTime()).isEqualTo(startTime);
                        assertThat(ex.getEndTime()).isEqualTo(endTime);
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository, never()).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should throw BookingDateConflictException if there are conflict bookings")
        void updateBooking_conflictBookingExist_throwsBookingDateConflictException() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock).plusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), bookingFirst.getMeetingRoom().getId());

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(userJohnDoe.getId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));
            when(bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(bookingFirst.getId()))).thenReturn(Collections.singletonList(bookingFirst));

            assertThatThrownBy(() -> bookingService.updateBooking(bookingFirst.getId(), bookingRequest))
                    .isExactlyInstanceOf(BookingDateConflictException.class)
                    .asInstanceOf(type(BookingDateConflictException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getMeetingRoomId()).isEqualTo(bookingFirst.getMeetingRoom().getId());
                    });

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(bookingFirst.getId()));
            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("should return updated booking")
        void updateBooking_existingBooking_returnsUpdatedBooking() {
            LocalDateTime startTime = LocalDateTime.now(clock);
            LocalDateTime endTime = LocalDateTime.now(clock).plusSeconds(1);
            BookingRequest bookingRequest = new BookingRequest(startTime, endTime, "Onboarding", userJohnDoe.getId(), bookingFirst.getMeetingRoom().getId());
            Booking createdBooking = aBooking()
                    .withId(3L)
                    .withStartTime(bookingRequest.startTime())
                    .withEndTime(bookingRequest.endTime())
                    .withPurpose(bookingRequest.purpose())
                    .withBookedBy(userJohnDoe)
                    .withMeetingRoom(meetingRoomFirst)
                    .build();

            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));
            when(userRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(userJohnDoe));
            when(meetingRoomRepository.findById(bookingRequest.meetingRoomId())).thenReturn(Optional.of(meetingRoomFirst));
            when(bookingRepository.findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(bookingFirst.getId()))).thenReturn(Collections.emptyList());
            when(bookingRepository.save(any(Booking.class))).thenReturn(createdBooking);

            BookingResponse bookingResponse = bookingService.updateBooking(bookingFirst.getId(), bookingRequest);

            assertThat(bookingResponse).isNotNull();
            assertThat(bookingResponse.id()).isEqualTo(3L);
            assertThat(bookingResponse.userId()).isEqualTo(bookingRequest.userId());
            assertThat(bookingResponse.meetingRoomId()).isEqualTo(meetingRoomFirst.getId());
            assertThat(bookingResponse)
                    .usingRecursiveComparison()
                    .comparingOnlyFields("purpose", "startTime", "endTime")
                    .isEqualTo(createdBooking);

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(userRepository).findById(userJohnDoe.getId());
            verify(meetingRoomRepository).findById(meetingRoomFirst.getId());
            verify(bookingRepository).findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(any(MeetingRoom.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(bookingFirst.getId()));
            verify(bookingRepository).save(any(Booking.class));
        }
    }

    @Nested
    @DisplayName("deleteBooking method")
    class DeleteBooking {

        @Test
        @DisplayName("should throw BookingNotFoundException if id does not exist")
        void deleteBooking_nonExistingBooking_throwsBookingNotFoundException() {
            when(bookingRepository.findById(nonExistingBookingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.deleteBooking(nonExistingBookingId))
                    .isExactlyInstanceOf(BookingNotFoundException.class)
                    .asInstanceOf(type(BookingNotFoundException.class))
                    .satisfies(ex -> {
                        assertThat(ex.getBookingId()).isEqualTo(nonExistingBookingId);
                    });

            verify(bookingRepository).findById(nonExistingBookingId);
            verify(bookingRepository, never()).deleteById(bookingFirst.getId());
        }

        @Test
        @DisplayName("should not throw if id exists")
        void deleteBooking_existingBooking_doesNotThrowException() {
            when(bookingRepository.findById(bookingFirst.getId())).thenReturn(Optional.of(bookingFirst));

            assertThatNoException().isThrownBy(() -> bookingService.deleteBooking(bookingFirst.getId()));

            verify(bookingRepository).findById(bookingFirst.getId());
            verify(bookingRepository).deleteById(bookingFirst.getId());
        }
    }
}
