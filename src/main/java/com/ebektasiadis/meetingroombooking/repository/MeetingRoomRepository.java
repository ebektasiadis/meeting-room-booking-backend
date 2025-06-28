package com.ebektasiadis.meetingroombooking.repository;

import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
    Optional<MeetingRoom> findByName(String name);
}
