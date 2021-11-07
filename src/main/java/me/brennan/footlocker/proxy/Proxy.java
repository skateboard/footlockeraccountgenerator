package me.brennan.footlocker.proxy;

import okhttp3.Authenticator;
import okhttp3.Credentials;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class Proxy {
    private String ip, username, password;
    private int port;

    public Proxy(String ip, int port, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public Proxy(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Authenticator getAuthenticator() {
        if (getUsername() == null || getPassword() == null)
            return null;

        return (route, response) -> {
            final String credential = Credentials.basic(getUsername(), getPassword());

            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
    }
}