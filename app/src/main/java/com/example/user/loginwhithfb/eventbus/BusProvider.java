package com.example.user.loginwhithfb.eventbus;


import com.squareup.otto.Bus;

/**
 * Created by POSTER on 29.07.2017.
 */

public class BusProvider {
    private static Bus bus = new Bus();

    public static Bus getInstance() {
        return bus;
    }

    public BusProvider() {
    }
}
