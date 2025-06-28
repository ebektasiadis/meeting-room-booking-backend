package com.ebektasiadis.meetingroombooking.exception.meetingroom;

import com.ebektasiadis.meetingroombooking.constants.ProblemTypes;
import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MeetingRoomNotFoundException extends AbstractApiException {
    private final Long meetingRoomId;

    public MeetingRoomNotFoundException(Long meetingRoomId) {
        super(
                String.format("Meeting room with id %d couldn't be found.", meetingRoomId),
                "Meeting Room not found.",
                ProblemTypes.MEETING_ROOM_NOT_FOUND_ERROR
        );

        this.meetingRoomId = meetingRoomId;
    }

    @Override
    public Map<String, Object> getProblemDetailProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("meetingRoomId", this.meetingRoomId);

        return properties;
    }
}
