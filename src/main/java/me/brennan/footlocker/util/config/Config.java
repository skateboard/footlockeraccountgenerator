package me.brennan.footlocker.util.config;

import com.google.gson.annotations.SerializedName;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class Config {

    @SerializedName("catch_all")
    private String catchAllEmail;

    public String getCatchAllEmail() {
        return catchAllEmail;
    }
}
