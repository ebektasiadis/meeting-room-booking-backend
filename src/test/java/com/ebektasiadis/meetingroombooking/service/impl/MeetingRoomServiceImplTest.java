package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNameExistsException;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.repository.MeetingRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ebektasiadis.meetingroombooking.testutil.MeetingRoomTestBuilder.aMeetingRoom;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingRoomService")
public class MeetingRoomServiceImplTest {
    @Mock
    MeetingRoomRepository meetingRoomRepository;

    @InjectMocks
    MeetingRoomServiceImpl meetingRoomService;

    Long nonExistingMeetingRoomId;

    MeetingRoom meetingRoomFirst;
    MeetingRoom meetingRoomSecond;

    @BeforeEach
    void setUp() {
        nonExistingMeetingRoomId = 999L;

        meetingRoomFirst = aMeetingRoom()
                .withId(1L)
                .withName("Meeting Room 1")
                .withCapacity(5)
                .withLocation("Thessaloniki, Greece")
                .withHasProjector(true)
                .withHasWhiteboard(true)
                .build();

        meetingRoomSecond = aMeetingRoom()
                .withId(2L)
                .withName("Meeting Room 2")
                .withCapacity(8)
                .withLocation("Athens, Greece")
                .withHasProjector(true)
                .withHasWhiteboard(false)
                .build();
    }

    @Nested
    @DisplayName("getMeetingRoomById method")
    class GetMeetingRoomById {
        @Test
        @DisplayName("should return meeting room if meeting room id exists")
        void getMeetingRoomById_existingMeetingRoom_returnsMeetingRoom() {
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            MeetingRoomResponse meetingRoomResponse = meetingRoomService.getMeetingRoomById(meetingRoomFirst.getId());

            assertThat(meetingRoomResponse).isNotNull();
            assertThat(meetingRoomResponse.id()).isEqualTo(meetingRoomFirst.getId());
            assertThat(meetingRoomResponse.name()).isEqualTo(meetingRoomFirst.getName());
            assertThat(meetingRoomResponse.capacity()).isEqualTo(meetingRoomFirst.getCapacity());
            assertThat(meetingRoomResponse.location()).isEqualTo(meetingRoomFirst.getLocation());
            assertThat(meetingRoomResponse.hasProjector()).isEqualTo(meetingRoomFirst.getHasProjector());
            assertThat(meetingRoomResponse.hasWhiteboard()).isEqualTo(meetingRoomFirst.getHasWhiteboard());

            verify(meetingRoomRepository, times(1)).findById(meetingRoomFirst.getId());
        }

        @Test
        @DisplayName("should throw MeetingRoomNotFoundException if meeting room id does not exist")
        void getMeetingRoomById_nonExistingMeetingRoom_throwMeetingRoomNotFoundException() {
            when(meetingRoomRepository.findById(nonExistingMeetingRoomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingRoomService.getMeetingRoomById(nonExistingMeetingRoomId))
                    .isInstanceOf(MeetingRoomNotFoundException.class)
                    .extracting("meetingRoomId")
                    .isEqualTo(nonExistingMeetingRoomId);

            verify(meetingRoomRepository, times(1)).findById(nonExistingMeetingRoomId);
        }
    }

    @Nested
    @DisplayName("getAllMeetingRooms method")
    class GetAllMeetingRooms {
        @Test
        @DisplayName("should return all meeting rooms")
        void getAllMeetingRooms_existingMeetingRooms_returnsAllMeetingRooms() {
            List<MeetingRoom> meetingRooms = Arrays.asList(meetingRoomFirst, meetingRoomSecond);

            when(meetingRoomRepository.findAll()).thenReturn(meetingRooms);

            List<MeetingRoomResponse> meetingRoomResponses = meetingRoomService.getAllMeetingRooms();

            assertThat(meetingRoomResponses).isNotNull();
            assertThat(meetingRoomResponses.size()).isEqualTo(meetingRooms.size());
            assertThat(meetingRoomResponses)
                    .extracting("id", "name")
                    .contains(tuple(meetingRoomFirst.getId(), meetingRoomFirst.getName()))
                    .contains(tuple(meetingRoomSecond.getId(), meetingRoomSecond.getName()));

            verify(meetingRoomRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should return empty list if no meeting rooms exist")
        void getAllMeetingRooms_nonExistingMeetingRooms_returnsEmptyList() {
            when(meetingRoomRepository.findAll()).thenReturn(Collections.emptyList());

            List<MeetingRoomResponse> meetingRoomResponses = meetingRoomService.getAllMeetingRooms();

            assertThat(meetingRoomResponses).isNotNull();
            assertThat(meetingRoomResponses.size()).isEqualTo(0);

            verify(meetingRoomRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("createMeetingRoom method")
    class CreateMeetingRoom {

        @Test
        @DisplayName("should create meeting room")
        void createMeetingRoom_returnsMeetingRoom() {
            MeetingRoomRequest meetingRoomRequest = new MeetingRoomRequest("Meeting Room 3", 6, "Larisa, Greece", true, true);
            MeetingRoom createdRoom = aMeetingRoom()
                    .withId(3L)
                    .withName("Meeting Room 3")
                    .withCapacity(6)
                    .withLocation("Larisa, Greece")
                    .withHasProjector(true)
                    .withHasWhiteboard(true)
                    .build();

            when(meetingRoomRepository.findByName(meetingRoomRequest.name())).thenReturn(Optional.empty());
            when(meetingRoomRepository.save(any(MeetingRoom.class))).thenReturn(createdRoom);

            MeetingRoomResponse meetingRoomResponse = meetingRoomService.createMeetingRoom(meetingRoomRequest);

            assertThat(meetingRoomResponse).isNotNull();
            assertThat(meetingRoomResponse.id()).isEqualTo(createdRoom.getId());
            assertThat(meetingRoomResponse.name()).isEqualTo(meetingRoomRequest.name());
            assertThat(meetingRoomResponse.capacity()).isEqualTo(meetingRoomRequest.capacity());
            assertThat(meetingRoomResponse.location()).isEqualTo(meetingRoomRequest.location());
            assertThat(meetingRoomResponse.hasProjector()).isEqualTo(meetingRoomRequest.hasProjector());
            assertThat(meetingRoomResponse.hasWhiteboard()).isEqualTo(meetingRoomRequest.hasWhiteboard());

            verify(meetingRoomRepository, times(1)).findByName(meetingRoomRequest.name());
            verify(meetingRoomRepository, times(1)).save(any(MeetingRoom.class));
        }

        @Test
        @DisplayName("should throw MeetingRoomNameExistsException if name used by another meeting room")
        void createMeetingRoom_existingName_throwsMeetingRoomNameExistsException() {
            MeetingRoomRequest meetingRoomRequest = new MeetingRoomRequest(meetingRoomFirst.getName(), 6, "Larisa, Greece", true, true);

            when(meetingRoomRepository.findByName(meetingRoomFirst.getName())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatThrownBy(() -> meetingRoomService.createMeetingRoom(meetingRoomRequest))
                    .isInstanceOf(MeetingRoomNameExistsException.class)
                    .extracting("meetingRoomName")
                    .isEqualTo(meetingRoomRequest.name());

            verify(meetingRoomRepository, times(1)).findByName(meetingRoomFirst.getName());
            verify(meetingRoomRepository, never()).save(any(MeetingRoom.class));
        }
    }

    @Nested
    @DisplayName("updateMeetingRoom method")
    class UpdateMeetingRoom {
        @Test
        @DisplayName("should throw MeetingRoomNotFoundException if id does not exist")
        void updateMeetingRoom_nonExistingMeetingRoom_throwMeetingRoomNotFoundException() {
            when(meetingRoomRepository.findById(nonExistingMeetingRoomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingRoomService.updateMeetingRoom(nonExistingMeetingRoomId, any(MeetingRoomRequest.class)))
                    .isInstanceOf(MeetingRoomNotFoundException.class)
                    .extracting("meetingRoomId")
                    .isEqualTo(nonExistingMeetingRoomId);

            verify(meetingRoomRepository, times(1)).findById(nonExistingMeetingRoomId);
            verify(meetingRoomRepository, never()).save(any(MeetingRoom.class));
        }

        @Test
        @DisplayName("should throw MeetingRoomNameExistsException if new name is used by another meeting room")
        void updateMeetingRoom_newNameIsUsedByAnotherMeetingRoom_throwsMeetingRoomNameExistsException() {
            MeetingRoomRequest meetingRoomRequest = new MeetingRoomRequest(meetingRoomSecond.getName(), 6, "Larisa, Greece", true, true);

            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));
            when(meetingRoomRepository.findByName(meetingRoomSecond.getName())).thenReturn(Optional.of(meetingRoomSecond));

            assertThatThrownBy(() -> meetingRoomService.updateMeetingRoom(meetingRoomFirst.getId(), meetingRoomRequest))
                    .isInstanceOf(MeetingRoomNameExistsException.class)
                    .extracting("meetingRoomName")
                    .isEqualTo(meetingRoomSecond.getName());

            verify(meetingRoomRepository, times(1)).findById(meetingRoomFirst.getId());
            verify(meetingRoomRepository, times(1)).findByName(meetingRoomSecond.getName());
            verify(meetingRoomRepository, never()).save(any(MeetingRoom.class));
        }

        @Test
        @DisplayName("should return updated meeting room when name remains the same")
        void updateMeetingRoom_existingMeetingRoomSameName_returnUpdatedMeetingRoom() {
            MeetingRoomRequest meetingRoomRequest = new MeetingRoomRequest(meetingRoomFirst.getName(), 6, "Larisa, Greece", true, true);
            MeetingRoom updatedMeetingRoom = aMeetingRoom()
                    .withId(meetingRoomFirst.getId())
                    .withName(meetingRoomRequest.name())
                    .withCapacity(meetingRoomRequest.capacity())
                    .withLocation(meetingRoomRequest.location())
                    .withHasProjector(meetingRoomRequest.hasProjector())
                    .withHasWhiteboard(meetingRoomRequest.hasWhiteboard())
                    .build();

            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));
            when(meetingRoomRepository.save(any(MeetingRoom.class))).thenReturn(updatedMeetingRoom);

            MeetingRoomResponse meetingRoomResponse = meetingRoomService.updateMeetingRoom(meetingRoomFirst.getId(), meetingRoomRequest);

            assertThat(meetingRoomResponse).isNotNull();
            assertThat(meetingRoomResponse.id()).isEqualTo(meetingRoomFirst.getId());
            assertThat(meetingRoomResponse.name()).isEqualTo(meetingRoomRequest.name());
            assertThat(meetingRoomResponse.capacity()).isEqualTo(meetingRoomRequest.capacity());
            assertThat(meetingRoomResponse.location()).isEqualTo(meetingRoomRequest.location());
            assertThat(meetingRoomResponse.hasProjector()).isEqualTo(meetingRoomRequest.hasProjector());
            assertThat(meetingRoomResponse.hasWhiteboard()).isEqualTo(meetingRoomRequest.hasWhiteboard());

            verify(meetingRoomRepository, times(1)).findById(meetingRoomFirst.getId());
            verify(meetingRoomRepository, never()).findByName(meetingRoomRequest.name());
            verify(meetingRoomRepository, times(1)).save(any(MeetingRoom.class));
        }

        @Test
        @DisplayName("should return updated meeting room")
        void updateMeetingRoom_existingMeetingRoom_returnUpdatedMeetingRoom() {
            MeetingRoomRequest meetingRoomRequest = new MeetingRoomRequest("Meeting Room 3", 6, "Larisa, Greece", true, true);
            MeetingRoom updatedMeetingRoom = aMeetingRoom()
                    .withId(meetingRoomFirst.getId())
                    .withName(meetingRoomRequest.name())
                    .withCapacity(meetingRoomRequest.capacity())
                    .withLocation(meetingRoomRequest.location())
                    .withHasProjector(meetingRoomRequest.hasProjector())
                    .withHasWhiteboard(meetingRoomRequest.hasWhiteboard())
                    .build();

            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));
            when(meetingRoomRepository.findByName(updatedMeetingRoom.getName())).thenReturn(Optional.empty());
            when(meetingRoomRepository.save(any(MeetingRoom.class))).thenReturn(updatedMeetingRoom);

            MeetingRoomResponse meetingRoomResponse = meetingRoomService.updateMeetingRoom(meetingRoomFirst.getId(), meetingRoomRequest);

            assertThat(meetingRoomResponse).isNotNull();
            assertThat(meetingRoomResponse.id()).isEqualTo(meetingRoomFirst.getId());
            assertThat(meetingRoomResponse.name()).isEqualTo(meetingRoomRequest.name());
            assertThat(meetingRoomResponse.capacity()).isEqualTo(meetingRoomRequest.capacity());
            assertThat(meetingRoomResponse.location()).isEqualTo(meetingRoomRequest.location());
            assertThat(meetingRoomResponse.hasProjector()).isEqualTo(meetingRoomRequest.hasProjector());
            assertThat(meetingRoomResponse.hasWhiteboard()).isEqualTo(meetingRoomRequest.hasWhiteboard());

            verify(meetingRoomRepository, times(1)).findById(meetingRoomFirst.getId());
            verify(meetingRoomRepository, times(1)).findByName(meetingRoomRequest.name());
            verify(meetingRoomRepository, times(1)).save(any(MeetingRoom.class));
        }
    }

    @Nested
    @DisplayName("deleteMeetingRoom method")
    class DeleteMeetingRoom {
        @Test
        @DisplayName("should throw MeetingRoomNotFoundException if id does not exist")
        void deleteMeetingRoom_nonExistingMeetingRoom_throwsMeetingRoomNotFoundException() {
            when(meetingRoomRepository.findById(nonExistingMeetingRoomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingRoomService.deleteMeetingRoom(nonExistingMeetingRoomId))
                    .isInstanceOf(MeetingRoomNotFoundException.class)
                    .extracting("meetingRoomId")
                    .isEqualTo(nonExistingMeetingRoomId);

            verify(meetingRoomRepository, times(1)).findById(nonExistingMeetingRoomId);
            verify(meetingRoomRepository, never()).delete(any(MeetingRoom.class));
        }

        @Test
        @DisplayName("should not throw if id exists")
        void deleteMeetingRoom_existingMeetingRoom_doesNotThrowException() {
            when(meetingRoomRepository.findById(meetingRoomFirst.getId())).thenReturn(Optional.of(meetingRoomFirst));

            assertThatNoException().isThrownBy(() -> meetingRoomService.deleteMeetingRoom(meetingRoomFirst.getId()));

            verify(meetingRoomRepository, times(1)).findById(meetingRoomFirst.getId());
            verify(meetingRoomRepository, times(1)).deleteById(meetingRoomFirst.getId());
        }
    }
}
