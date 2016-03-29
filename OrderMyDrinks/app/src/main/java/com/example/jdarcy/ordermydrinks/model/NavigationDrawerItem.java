package com.example.jdarcy.ordermydrinks.model;

import com.example.jdarcy.ordermydrinks.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerItem {

    private String title;
    private int imageId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<NavigationDrawerItem> getData() {
        List<NavigationDrawerItem> dataList = new ArrayList<>();

        int[] imageIds = getImages();
        String[] titles = getTitles();

        for (int i = 0; i < titles.length; i++) {
            NavigationDrawerItem navItem = new NavigationDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setImageId(imageIds[i]);
            dataList.add(navItem);
        }
        return dataList;
    }

    private static int[] getImages() {

        return new int[]{
                R.drawable.ic_home_white_36dp, R.drawable.ic_beer,
                R.drawable.ic_share_white_36dp, R.drawable.ic_feedback_white_36dp,
                R.drawable.ic_settings_applications_white_36dp, R.drawable.ic_rate_review_white_36dp};
    }

    private static String[] getTitles() {

        return new String[]{
                "Home", "Test Alcohol Levels", "Share", "Feedback", "Settings", "Rate This App"
        };
    }
}
