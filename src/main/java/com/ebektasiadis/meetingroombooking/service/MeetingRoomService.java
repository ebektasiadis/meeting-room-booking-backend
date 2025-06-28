package com.ebektasiadis.meetingroombooking.service;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNameExistsException;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;

public interface MeetingRoomService {
    Iterable<MeetingRoomResponse> getAllMeetingRooms();

    MeetingRoomResponse getMeetingRoomById(Long id) throws MeetingRoomNotFoundException;

    MeetingRoomResponse createMeetingRoom(MeetingRoomRequest meetingRoomRequest) throws MeetingRoomNameExistsException;

    MeetingRoomResponse updateMeetingRoom(Long id, MeetingRoomRequest meetingRoomRequest) throws MeetingRoomNotFoundException, MeetingRoomNameExistsException;

    void deleteMeetingRoom(Long id) throws MeetingRoomNotFoundException;
}
