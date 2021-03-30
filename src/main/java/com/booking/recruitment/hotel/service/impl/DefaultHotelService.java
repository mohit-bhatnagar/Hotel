package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.CityService;
import com.booking.recruitment.hotel.service.HotelService;
import com.booking.recruitment.hotel.util.DistanceComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

@Service
class DefaultHotelService implements HotelService {
  private static final int TOP_LIMIT = 3;
  private final HotelRepository hotelRepository;
//  private Map<Long,List<Hotel>> distanceCache = new HashMap<>();

  @Autowired
  CityService cityService;

  @Autowired
  DefaultHotelService(HotelRepository hotelRepository) {
    this.hotelRepository = hotelRepository;
  }

  @Override
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll().stream().filter(hotel -> !hotel.isDeleted()).collect(Collectors.toList());
  }

  @Override
  public Hotel getHotelById(Long id) {
    Optional<Hotel> byId = hotelRepository.findById(id);
    if(!byId.isPresent()) {
      throw new ElementNotFoundException(id + " is not found");
    }
    Hotel hotel = byId.get();
    if(hotel.isDeleted()){
      throw new ElementNotFoundException(id + " is not found");
    }
    return hotel;
  }

  @Override
  public List<Hotel> getHotelsByCity(Long cityId) {
    return hotelRepository.findAll().stream()
        .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
        .collect(Collectors.toList());
  }

  public List<Hotel> getTopHotelsByCity(Long cityId, String sortBy) {
    List<Hotel> allHotels = getHotelsByCity(cityId);
//    if (distanceCache.containsKey(cityId)) {
//      return distanceCache.get(cityId);
//    }

    Comparator<Hotel> distanceComparator =null;
    if(sortBy.equalsIgnoreCase("distance")){
      distanceComparator= new DistanceComparator(cityService.getCityById(cityId));
    }

    PriorityQueue<Hotel> queue = new PriorityQueue(distanceComparator);

    addToQueue(queue,allHotels);

    List<Hotel> hotels = new ArrayList<>();
    while (!queue.isEmpty()) {
      hotels.add(queue.poll());
    }

//    distanceCache.put(cityId,hotels);
    return hotels;
  }

  private void addToQueue(PriorityQueue<Hotel> queue, List<Hotel> allHotels) {

    allHotels.stream().forEach(hotel -> {
      queue.add(hotel);
      if (queue.size() > TOP_LIMIT) {
          queue.poll();
        }
    });

  }

  @Override
  public Hotel createNewHotel(Hotel hotel) {
    if (hotel.getId() != null) {
      throw new BadRequestException("The ID must not be provided when creating a new Hotel");
    }

    return hotelRepository.save(hotel);
  }

  @Override
  public boolean deleteHotelById(Long id) {
    Optional<Hotel> byId = hotelRepository.findById(id);
    if(!byId.isPresent() || byId.get().isDeleted()) {
      return false;
    }
    Hotel hotel = byId.get();
    hotel.setDeleted(true);
    hotelRepository.save(hotel);
    return true;
  }
}
