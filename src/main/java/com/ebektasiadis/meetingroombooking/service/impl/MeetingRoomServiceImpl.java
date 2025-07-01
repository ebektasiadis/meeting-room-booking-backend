package com.ebektasiadis.meetingroombooking.service.impl;

import com.ebektasiadis.meetingroombooking.dto.MeetingRoomRequest;
import com.ebektasiadis.meetingroombooking.dto.MeetingRoomResponse;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNameExistsException;
import com.ebektasiadis.meetingroombooking.exception.meetingroom.MeetingRoomNotFoundException;
import com.ebektasiadis.meetingroombooking.mapper.MeetingRoomMapper;
import com.ebektasiadis.meetingroombooking.model.MeetingRoom;
import com.ebektasiadis.meetingroombooking.repository.MeetingRoomRepository;
import com.ebektasiadis.meetingroombooking.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {
    final private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    public MeetingRoomServiceImpl(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public List<MeetingRoomResponse> getAllMeetingRooms() {
        return meetingRoomRepository.findAll().stream().map(MeetingRoomMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public MeetingRoomResponse getMeetingRoomById(Long id) throws MeetingRoomNotFoundException {
        MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));

        return MeetingRoomMapper.toResponse(meetingRoom);
    }

    @Override
    public MeetingRoomResponse createMeetingRoom(MeetingRoomRequest meetingRoomRequest) throws MeetingRoomNameExistsException {
        MeetingRoom meetingRoom = MeetingRoomMapper.toEntity(meetingRoomRequest);

        if (meetingRoomRepository.findByName(meetingRoom.getName()).isPresent()) {
            throw new MeetingRoomNameExistsException(meetingRoom.getName());
        }

        meetingRoom = meetingRoomRepository.save(meetingRoom);

        return MeetingRoomMapper.toResponse(meetingRoom);
    }

    @Override
    public MeetingRoomResponse updateMeetingRoom(Long id, MeetingRoomRequest meetingRoomRequest) throws MeetingRoomNotFoundException, MeetingRoomNameExistsException {
        MeetingRoom meetingRoom = MeetingRoomMapper.toEntity(meetingRoomRequest);
        MeetingRoom existingMeetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));
        
        if (!existingMeetingRoom.getName().equals(meetingRoom.getName())) {
            if (meetingRoomRepository.findByName(meetingRoom.getName()).isPresent()) {
                throw new MeetingRoomNameExistsException(meetingRoom.getName());
            }
        }

        existingMeetingRoom.setName(meetingRoom.getName());
        existingMeetingRoom.setCapacity(meetingRoom.getCapacity());
        existingMeetingRoom.setLocation(meetingRoom.getLocation());
        existingMeetingRoom.setHasProjector(meetingRoom.getHasProjector());
        existingMeetingRoom.setHasWhiteboard(meetingRoom.getHasWhiteboard());

        existingMeetingRoom = meetingRoomRepository.save(existingMeetingRoom);

        return MeetingRoomMapper.toResponse(existingMeetingRoom);
    }

    @Override
    public void deleteMeetingRoom(Long id) throws MeetingRoomNotFoundException {
        meetingRoomRepository.findById(id)
                .orElseThrow(() -> new MeetingRoomNotFoundException(id));
        meetingRoomRepository.deleteById(id);
    }
}
