package com.dormitory.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class RoomController {

    private final RoomRepository roomRepository;
    private final FacilityRepository facilityRepository;

    public RoomController(RoomRepository roomRepository, FacilityRepository facilityRepository) {
        this.roomRepository = roomRepository;
        this.facilityRepository = facilityRepository;
    }

    // 1. 빈 방 조회
    @GetMapping("/api/rooms/available")
    public List<String> getAvailableRooms() {
        return roomRepository.findAvailableRoomNumbers();
    }

    // 2. 호실 전체 목록
    @GetMapping("/api/rooms/all")
    public List<Map<String, Object>> getAllRooms() {
        return roomRepository.findAllRooms();
    }

    // 3. 특정 호실 거주 학생 조회
    @GetMapping("/api/rooms/{roomNo}/students")
    public List<Map<String, Object>> getStudentsByRoom(@PathVariable String roomNo) {
        return roomRepository.findStudentsByRoom(roomNo);
    }

    // 4. 특정 호실 시설물 조회
    @GetMapping("/api/rooms/{roomNo}/facilities")
    public List<Map<String, Object>> getFacilitiesByRoom(@PathVariable String roomNo) {
        return facilityRepository.findFacilitiesByRoom(roomNo);
    }
}