package com.ebektasiadis.meetingroombooking.testutil;

import com.ebektasiadis.meetingroombooking.model.MeetingRoom;

public class MeetingRoomTestBuilder {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private Boolean hasProjector;
    private Boolean hasWhiteboard;

    private MeetingRoomTestBuilder() {
        this.id = 1L;
        this.name = "Default Meeting Room";
        this.capacity = 1;
        this.location = "Default Location";
        this.hasProjector = false;
        this.hasWhiteboard = false;
    }

    private MeetingRoomTestBuilder(MeetingRoomTestBuilder meetingRoomTestBuilder) {
        this.id = meetingRoomTestBuilder.id;
        this.name = meetingRoomTestBuilder.name;
        this.capacity = meetingRoomTestBuilder.capacity;
        this.location = meetingRoomTestBuilder.location;
        this.hasProjector = meetingRoomTestBuilder.hasProjector;
        this.hasWhiteboard = meetingRoomTestBuilder.hasWhiteboard;
    }

    public static MeetingRoomTestBuilder aMeetingRoom() {
        return new MeetingRoomTestBuilder();
    }

    public MeetingRoomTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MeetingRoomTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MeetingRoomTestBuilder withCapacity(Integer capacity) {
        this.capacity = capacity;
        return this;
    }

    public MeetingRoomTestBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public MeetingRoomTestBuilder withHasProjector(Boolean hasProjector) {
        this.hasProjector = hasProjector;
        return this;
    }

    public MeetingRoomTestBuilder withHasWhiteboard(Boolean hasWhiteboard) {
        this.hasWhiteboard = hasWhiteboard;
        return this;
    }

    public MeetingRoom build() {
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(id);
        meetingRoom.setName(name);
        meetingRoom.setCapacity(capacity);
        meetingRoom.setLocation(location);
        meetingRoom.setHasProjector(hasProjector);
        meetingRoom.setHasWhiteboard(hasWhiteboard);
        return meetingRoom;
    }

    public MeetingRoomTestBuilder but() {
        return new MeetingRoomTestBuilder(this);
    }
}
