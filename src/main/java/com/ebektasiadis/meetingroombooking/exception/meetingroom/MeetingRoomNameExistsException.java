package com.ebektasiadis.meetingroombooking.exception.meetingroom;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class MeetingRoomNameExistsException extends AbstractApiException {
    private final String meetingRoomName;

    public MeetingRoomNameExistsException(String meetingRoomName) {
        super(
                String.format("Meeting room with the name %s already exists.", meetingRoomName),
                "Meeting room name is already in use.",
                ProblemTypes.MEETING_ROOM_NAME_EXISTS_ERROR
        );

        this.meetingRoomName = meetingRoomName;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("meetingRoomName", meetingRoomName);

        return properties;
    }
}
