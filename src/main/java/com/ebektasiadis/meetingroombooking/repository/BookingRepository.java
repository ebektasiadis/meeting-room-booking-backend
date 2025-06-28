package com.ebektasiadis.meetingroombooking.repository;

import com.ebektasiadis.meetingroombooking.model.Booking;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    public List<Booking> findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfter(MeetingRoom meetingRoom, LocalDateTime endTime, LocalDateTime startTime);

    public List<Booking> findFirstByMeetingRoomAndStartTimeBeforeAndEndTimeAfterAndIdNot(MeetingRoom meetingRoom, LocalDateTime endTime, LocalDateTime startTime, Long id);
}

