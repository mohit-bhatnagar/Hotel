package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.service.CityService;
import com.booking.recruitment.hotel.service.HotelService;
import com.booking.recruitment.hotel.util.DistanceComparator;
import org.apache.catalina.connector.Response;
import com.booking.recruitment.hotel.util.Haversine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;


@RestController
@RequestMapping("/hotel")
public class HotelController {
  private final HotelService hotelService;
  @Autowired
  public HotelController(HotelService hotelService) {
    this.hotelService = hotelService;
  }



  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Hotel> getAllHotels() {
    return hotelService.getAllHotels();
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{hotelId}")
  public Hotel getHotelById(@PathVariable Long hotelId) {
    return hotelService.getHotelById(hotelId);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{cityId}")
  public Hotel getHotelByCity(@PathVariable Long cityId, @RequestParam String sortBy) {


    return hotelService.getTopHotelsByCity(cityId,sortBy);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Hotel createHotel(@RequestBody Hotel hotel) {
    return hotelService.createNewHotel(hotel);
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping(value = "/{hotelId}")
  public void deleteHotel(@PathVariable Long hotelId){
    boolean status = hotelService.deleteHotelById(hotelId);
    if(!status){
      throw new ElementNotFoundException(hotelId + " not found");
    }
  }

}
