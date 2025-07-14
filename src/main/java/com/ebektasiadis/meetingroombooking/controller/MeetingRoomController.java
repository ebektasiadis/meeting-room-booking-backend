package com.ebektasiadis.meetingroombooking.controller;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Meeting rooms API")
@RestController
@RequestMapping("/api/v1/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @Operation(summary = "Get all meeting rooms", description = "Returns a list of meeting rooms")
    @GetMapping
    public ResponseEntity<Iterable<MeetingRoomResponse>> getAllMeetingRooms() {
        Iterable<MeetingRoomResponse> meetingRooms = meetingRoomService.getAllMeetingRooms();
        return ResponseEntity.ok(meetingRooms);
    }

    @Operation(summary = "Get meeting room by id", description = "Returns a single meeting room")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoomResponse> getMeetingRoomById(@PathVariable Long id) {
        MeetingRoomResponse meetingRoom = meetingRoomService.getMeetingRoomById(id);
        return ResponseEntity.ok(meetingRoom);
    }

    @Operation(summary = "Creates a new meeting room", description = "Returns the created meeting room")
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

    @Operation(summary = "Updates an existing meeting room", description = "Returns the updated meeting room")
    @PutMapping("/{id}")
    public ResponseEntity<MeetingRoomResponse> updateMeetingRoom(@PathVariable Long id, @Valid @RequestBody MeetingRoomRequest meetingRoomRequest) {
        MeetingRoomResponse updatedRoom = meetingRoomService.updateMeetingRoom(id, meetingRoomRequest);
        return ResponseEntity.ok(updatedRoom);
    }

    @Operation(summary = "Deletes an existing meeting room")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return ResponseEntity.noContent().build();
    }
}
