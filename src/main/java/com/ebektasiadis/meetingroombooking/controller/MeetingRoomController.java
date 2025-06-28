package com.ebektasiadis.meetingroombooking.controller;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.service.MeetingRoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/meeting-rooms")
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Autowired
    public MeetingRoomController(MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping
    public ResponseEntity<Iterable<MeetingRoomResponse>> getAllMeetingRooms() {
        Iterable<MeetingRoomResponse> meetingRooms = meetingRoomService.getAllMeetingRooms();
        return ResponseEntity.ok(meetingRooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoomResponse> getMeetingRoomById(@PathVariable Long id) {
        MeetingRoomResponse meetingRoom = meetingRoomService.getMeetingRoomById(id);
        return ResponseEntity.ok(meetingRoom);
    }

    @PostMapping
    public ResponseEntity<MeetingRoomResponse> createMeetingRoom(@Valid @RequestBody MeetingRoomRequest meetingRoomRequest) {
        MeetingRoomResponse createdRoom = meetingRoomService.createMeetingRoom(meetingRoomRequest);

        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRoom.id())
                .toUri();

        return ResponseEntity.created(resourceLocation).body(createdRoom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingRoomResponse> updateMeetingRoom(@PathVariable Long id, @Valid @RequestBody MeetingRoomRequest meetingRoomRequest) {
        MeetingRoomResponse updatedRoom = meetingRoomService.updateMeetingRoom(id, meetingRoomRequest);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return ResponseEntity.noContent().build();
    }
}
