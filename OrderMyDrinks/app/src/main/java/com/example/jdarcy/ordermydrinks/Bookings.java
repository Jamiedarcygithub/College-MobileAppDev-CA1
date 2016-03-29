package com.example.jdarcy.ordermydrinks;

/**
 * Represents an item in a Bookings table
 */
public class Bookings {

    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    /**
     * Item Email
     */
    @com.google.gson.annotations.SerializedName("email")
    private String mEmail;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item Number of guests
     */
    @com.google.gson.annotations.SerializedName("numberofguests")
    private int mGuests;

    /**
     * Item Drinks order
     */
    @com.google.gson.annotations.SerializedName("order")
    private String mOrder;

    /**
     * Item Arrival time
     */
    @com.google.gson.annotations.SerializedName("time")
    private int mTime;

    /**
     * Item Bar Name
     */
    @com.google.gson.annotations.SerializedName("barname")
    private String mBarName;

    /**
     * Item Bar Name
     */
    @com.google.gson.annotations.SerializedName("baraddress")
    private String mBarAddress;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * ToDoItem constructor
     */
    public Bookings() {

    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Initializes a new bookings item
     *
     * @param name           The item name
     * @param email          The item email
     * @param id             The item id
     * @param numberOfGuests The item guests
     * @param order          The item id
     * @param time           The item time
     * @param barName        The item Bar Name
     * @param barAddress     The item bar address
     */
    public Bookings(String name, String id, int numberOfGuests, String order, int time, String barName, String barAddress, String email) {
        this.setName(name);
        this.setId(id);
        this.setGuests(numberOfGuests);
        this.setOrder(order);
        this.setBarName(barName);
        this.setBarAddress(barAddress);
        this.setEmail(email);
        this.setTime(time);
    }

    /**
     * Returns the item name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the item name
     *
     * @param email email to set
     */
    public final void setEmail(String email) {
        mEmail = email;
    }


    /**
     * Returns the item email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Sets the item name
     *
     * @param name name to set
     */
    public final void setName(String name) {
        mName = name;
    }


    /**
     * Returns the item guests
     */
    public int getGuests() {
        return mGuests;
    }

    /**
     * Sets the item text
     *
     * @param numberOfGuests text to set
     */
    public final void setGuests(int numberOfGuests) {
        mGuests = numberOfGuests;
    }

    /**
     * Returns the item order
     */
    public String getOrder() {
        return mOrder;
    }

    /**
     * Sets the item name
     *
     * @param order name to set
     */
    public final void setOrder(String order) {
        mOrder = order;
    }

    /**
     * Returns the item time
     */
    public int getTime() {
        return mTime;
    }

    /**
     * Sets the item time
     *
     * @param time time to set
     */
    public final void setTime(int time) {
        mTime = time;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id id to set
     */
    public final void setId(String id) {
        mId = id;
    }

    /**
     * Returns the Bar Name
     */
    public String getBarName() {
        return mBarName;
    }

    /**
     * Sets the item Bar Name
     *
     * @param barName name to set
     */
    public final void setBarName(String barName) {
        mBarName = barName;
    }

    /**
     * Returns the Bar Address
     */
    public String getBarAddress() {
        return mBarAddress;
    }

    /**
     * Sets the item Bar Address
     *
     * @param barAddress address to set
     */
    public final void setBarAddress(String barAddress) {
        mBarAddress = barAddress;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Bookings && ((Bookings) o).mId.equals(mId);
    }
}
