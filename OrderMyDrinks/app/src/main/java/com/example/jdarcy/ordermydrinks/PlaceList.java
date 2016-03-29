package com.example.jdarcy.ordermydrinks;

/**
 * Class to manage list of places returned from google places
 */

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

/**
 * Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 */
public class PlaceList implements Serializable {

    @Key
    public String status;

    @Key
    public List<Place> results;

}

