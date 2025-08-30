package com.ebektasiadis.meetingroombooking.exception.meetingroom;

import com.ebektasiadis.meetingroombooking.exception.common.AbstractApiException;
import com.ebektasiadis.meetingroombooking.exception.common.Extension;
import com.ebektasiadis.meetingroombooking.exception.common.ResponseProblemDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseProblemDetail(
        status = HttpStatus.CONFLICT,
        type = "meeting-room-name-exists",
        title = "Meeting room name is already in use.",
        extensions = {
                @Extension(name = "meetingRoomName", type = String.class),
        }
)
public class MeetingRoomNameExistsException extends AbstractApiException {
    private final String meetingRoomName;

    public MeetingRoomNameExistsException(String meetingRoomName) {
        super(
                String.format("Meeting room with the name %s already exists.", meetingRoomName)
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
