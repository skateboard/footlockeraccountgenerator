package me.brennan.footlocker.task;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.brennan.footlocker.FootLocker;
import me.brennan.footlocker.model.Account;
import me.brennan.footlocker.proxy.Proxy;
import me.brennan.footlocker.request.CustomCookieJar;
import me.brennan.footlocker.request.JsonBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class CreateAccountTask implements Runnable {

    private final OkHttpClient httpClient;

    private final Pattern EMAIL_PATTERN = Pattern.compile("activationToken=(.*)");

    public CreateAccountTask() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new CustomCookieJar())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);

        final Proxy proxy = FootLocker.INSTANCE.getProxyManager().randomProxy();
        if (proxy != null) {
            builder.proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())));
            if (proxy.getAuthenticator() != null) {
                builder.proxyAuthenticator(proxy.getAuthenticator());
            }
        }

        this.httpClient = builder.build();
    }

    @Override
    public void run() {
        try {
            String csrfToken = generateSession();

            if (csrfToken != null) {
                final Account createdAccount = createAccount(csrfToken);

                if (createdAccount != null) {
                    final String activationToken = getConformationEmail(createdAccount.getEmail());

                    //regen session for activation
                    csrfToken = generateSession();

                    if (activateAccount(csrfToken, activationToken)) {
                        System.out.println("Created account: " + createdAccount.getEmail());
                        createdAccount.write();
                    } else {
                        System.out.println("Failed to activate account: " + createdAccount);
                    }
                } else {
                    System.out.println("Failed to create account");
                }
            } else {
                System.out.println("Failed to generate session.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean activateAccount(String csrfToken, String activationToken) throws Exception {
        final JsonObject body = new JsonObject();
        body.addProperty("activationToken", URLDecoder.decode(activationToken, StandardCharsets.UTF_8));

        final Request request = new Request.Builder()
                .url("https://www.footlocker.com/api/v3/activation?timestamp=" + System.currentTimeMillis())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0")
                .post(new JsonBody(body))
                .header("x-csrf-token", csrfToken)
                .header("Accept", "*/*")
                .header("Connection", "keep-alive")
                .build();

        try(Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                final JsonObject jsonObject = parse(response.body().string());

                return jsonObject.get("activationStatus").getAsString().equals("Success");
            }
        }

        return false;
    }

    private Account createAccount(String csrfToken) throws Exception {
        final String firstName = FootLocker.INSTANCE.getFirstName();
        final String lastName = FootLocker.INSTANCE.getLastName();
        final String phoneNumber = FootLocker.INSTANCE.getPhoneNumber();
        final String email = firstName + "_" + lastName + "@" + FootLocker.INSTANCE.getConfig().getCatchAllEmail();
        final String password = firstName + "1969!";

        System.out.println(password);

        final JsonObject body = new JsonObject();
        body.addProperty("bannerEmailOptIn", false);
        body.addProperty("firstName", firstName);
        body.addProperty("lastName", lastName);
        body.addProperty("birthday", "07/02/2001");
        body.addProperty("uid", email);
        body.addProperty("password", password);
        body.addProperty("phoneNumber", phoneNumber);
        body.addProperty("loyaltyStatus", true);
        body.addProperty("wantToBeVip", false);
        body.addProperty("flxTcVersion", "2.0");
        body.addProperty("loyaltyFlxEmailOptIn", false);

        final Request request = new Request.Builder()
                .url("https://www.footlocker.com/api/v3/users?timestamp=" + System.currentTimeMillis())
                .post(new JsonBody(body))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0")
                .header("x-csrf-token", csrfToken)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .build();

        try(Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 201) {
                System.out.println(response.code());
                System.out.println(response.body().string());
                return null;
            }
        }

        return new Account(email, firstName, lastName, phoneNumber, password);
    }

    private String generateSession() throws Exception {
        final Request request = new Request.Builder()
                .url("https://www.footlocker.com/api/session")
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0")
                .build();

        try(Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200)
                return parse(response.body().string()).getAsJsonObject("data").get("csrfToken").getAsString();
        }

        return null;
    }

    private String getConformationEmail(String email) {
        try {
            String activationEmail = FootLocker.INSTANCE.getGmailService().getEmail(email);

            while (activationEmail == null) {
                activationEmail = FootLocker.INSTANCE.getGmailService().getEmail(email);
                TimeUnit.SECONDS.sleep(1);
            }
            final Matcher matcher = EMAIL_PATTERN.matcher(activationEmail);

            if (matcher.find()) {
                final String group = matcher.group();

                return group.split("&")[0].split("=")[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private JsonObject parse(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
