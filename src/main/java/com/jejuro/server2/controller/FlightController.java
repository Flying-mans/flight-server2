package com.jejuro.server2.controller;

import com.jejuro.server2.entity.Flight;
import com.jejuro.server2.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FlightController {

    private final FlightRepository flightRepository;

    /**
     * 데이터가 잘 들어가있는지 확인용
     */
    @GetMapping("/flight/{id}")
    public Flight findById(@PathVariable String id) {
        return flightRepository.findById(id).get();
    }

    @GetMapping("/flight")
    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

}
