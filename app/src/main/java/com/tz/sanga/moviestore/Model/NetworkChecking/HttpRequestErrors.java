package com.tz.sanga.moviestore.Model.NetworkChecking;

public class HttpRequestErrors {

    public static final int network_connect_timeout = 599;
    public static final int authenticate_to_gain_network_access = 511;
    public static final int  network_read_timeout = 598;

    //Description for errors codes
    public static final String connection_required = "Please check your network connection";
    public static final String connection_time_out = "Connection time out";
    public static final String connection_read_time_out = "Network read time out";

}
