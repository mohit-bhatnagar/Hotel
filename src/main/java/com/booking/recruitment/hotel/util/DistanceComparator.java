package com.booking.recruitment.hotel.util;

import com.booking.recruitment.hotel.model.City;
import com.booking.recruitment.hotel.model.Hotel;

import java.util.Comparator;

public class DistanceComparator  implements Comparator<Hotel> {

    City cityId;

    public DistanceComparator(City cityId) {
        this.cityId = cityId;
    }


    @Override
    public int compare(Hotel id1, Hotel id2) {
        return distance(id2) - distance(id1);
    }


    public int distance(Hotel a) {
        return (int) Haversine.distance(cityId.getCityCentreLatitude(), cityId.getCityCentreLongitude(), a.getLatitude(), a.getLongitude());

    }

}
