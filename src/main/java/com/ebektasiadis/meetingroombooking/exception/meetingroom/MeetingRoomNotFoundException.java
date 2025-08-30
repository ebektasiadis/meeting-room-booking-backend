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
        status = HttpStatus.NOT_FOUND,
        type = "meeting-room-not-found",
        title = "Meeting Room not found.",
        extensions = {
                @Extension(name = "meetingRoomId", type = Long.class),
        }
)
public class MeetingRoomNotFoundException extends AbstractApiException {
    private final Long meetingRoomId;

    public MeetingRoomNotFoundException(Long meetingRoomId) {
        super(
                String.format("Meeting room with id %d couldn't be found.", meetingRoomId)

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
