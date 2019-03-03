package com.example.administrator.hotelservice.search_data;

/**
 * Created by Juned on 2/1/2017.
 */

public class Module {

    String title = null;
    String province = null;
    String image;


    public Module(String title, String province, String image) {
        this.title = title;
        this.province = province;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {

        return title + " " + province;

    }

}
