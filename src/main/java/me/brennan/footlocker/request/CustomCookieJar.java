package me.brennan.footlocker.request;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class CustomCookieJar implements CookieJar {
    private final List<Cookie> cookies = new LinkedList<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookies;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookies.addAll(list);
    }

    public void setCookie(String name, String value, String domain) {
        addCookies(new Cookie.Builder()
                .name(name)
                .value(value)
                .domain(domain)
                .build());
    }

    public void addCookies(Cookie cookie) {
        cookies.add(cookie);
    }

    public void clearCookies() {
        cookies.clear();
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
}