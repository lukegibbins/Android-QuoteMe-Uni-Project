package com.example.quoteme.Models;

import com.google.android.gms.maps.model.LatLng;

    //personal map object to hold data to plot on maps
public class MapObject {
    LatLng latLng;
    String city;
    String country;
    String quoteTitle;
    String vendor;

    // Constructor for mapObject. Tells me
    public MapObject(LatLng latLng, String city, String country, String quoteTitle, String vendor) {
        this.city = city;
        this.country = country;
        this.quoteTitle = quoteTitle;
        this.vendor = vendor;
        this.latLng = latLng;
    }

    //getters to get object data
    public LatLng getLatLng(){
        return this.latLng;
    }

    public String getCity(){
        return this.city;
    }

    public String getCountry(){
        return this.country;
    }

    public String getVendor(){
        return this.vendor;
    }

    public String getQuoteTitle(){
        return this.quoteTitle;
    }

}
