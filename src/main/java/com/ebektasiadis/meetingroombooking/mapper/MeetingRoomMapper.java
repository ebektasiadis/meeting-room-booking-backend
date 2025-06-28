package com.ebektasiadis.meetingroombooking.mapper;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;

public class MeetingRoomMapper {
    public static MeetingRoom toEntity(MeetingRoomRequest meetingRoomRequest) {
        if (meetingRoomRequest == null) {
            return null;
        }

        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setName(meetingRoomRequest.name());
        meetingRoom.setCapacity(meetingRoomRequest.capacity());
        meetingRoom.setLocation(meetingRoomRequest.location());
        meetingRoom.setHasProjector(meetingRoomRequest.hasProjector());
        meetingRoom.setHasWhiteboard(meetingRoomRequest.hasWhiteboard());

        return meetingRoom;
    }

    public static MeetingRoomResponse toResponse(MeetingRoom meetingRoom) {
        if (meetingRoom == null) {
            return null;
        }

        return new MeetingRoomResponse(
                meetingRoom.getId(),
                meetingRoom.getName(),
                meetingRoom.getCapacity(),
                meetingRoom.getLocation(),
                meetingRoom.getHasProjector(),
                meetingRoom.getHasWhiteboard()
        );
    }
}
