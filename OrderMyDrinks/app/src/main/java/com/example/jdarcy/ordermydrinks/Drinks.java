package com.example.jdarcy.ordermydrinks;

public class Drinks {
    /**
     * Item text
     */
    @com.google.gson.annotations.SerializedName("text")
    private String mText;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    private int mDrinks = 0;

    /**
     * Drinks constructor
     */
    public Drinks() {

    }

    @Override
    public String toString() {
        return getText();
    }

    /**
     * Initializes a new DrinksItem
     *
     * @param text The item text
     * @param id   The item id
     * @param numDrinks   The item numDrinks
     */
    public Drinks(String text, String id, int numDrinks) {
        this.setText(text);
        this.setId(id);
        this.setNumDrinks(numDrinks);
    }

    /**
     * Returns the item text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the item text
     *
     * @param text text to set
     */
    public final void setText(String text) {
        mText = text;
    }


    /**
     * Sets the item text
     *
     * @param numDrinks drinks to set
     */
    public final void setNumDrinks(int numDrinks) {
        mDrinks = numDrinks;
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Drinks && ((Drinks) o).mId.equals(mId);
    }

}