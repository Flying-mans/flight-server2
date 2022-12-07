package com.jejuro.server2.repository;

import com.jejuro.server2.entity.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlightRepository extends MongoRepository<Flight, String> {

}
