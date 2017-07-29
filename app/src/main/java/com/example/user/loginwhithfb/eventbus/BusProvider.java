package com.example.user.loginwhithfb.eventbus;


import com.squareup.otto.Bus;

/**
 * Created by POSTER on 29.07.2017.
 */

public class BusProvider {
    private static Bus bus;

    public static Bus getInstance() {
        if (bus == null){
            bus = new Bus();
        }
        return bus;
    }
}
