package me.brennan.footlocker;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import me.brennan.footlocker.gmail.GmailService;
import me.brennan.footlocker.proxy.ProxyManager;
import me.brennan.footlocker.task.CreateAccountTask;
import me.brennan.footlocker.util.CSVWriter;
import me.brennan.footlocker.util.config.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public enum FootLocker {
    INSTANCE;

    private final Faker faker = new Faker();

    private Config config;

    private GmailService gmailService;

    private CSVWriter writer;

    private ProxyManager proxyManager;

    public void start() throws Exception {
        loadConfig();
        this.gmailService = new GmailService();

        this.proxyManager = new ProxyManager();
        System.out.println("loaded " + proxyManager.size() + " proxies!");

        this.writer = new CSVWriter("created_accounts.csv");
        this.writer.write(new String[]{"Email", "First", "Last", "Phone", "Password"});

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter Threads: ");
        int threads = 1;
        try {
            threads = Integer.parseInt(bufferedReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++)
            executorService.submit(new CreateAccountTask());
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public Config getConfig() {
        return config;
    }

    public CSVWriter getWriter() {
        return writer;
    }

    public GmailService getGmailService() {
        return gmailService;
    }

    public String getFirstName() {
        return faker.name().firstName();
    }

    public String getLastName() {
        return faker.name().lastName();
    }

    public String getPhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    private void loadConfig() {
        try {
            final File file = new File("config.json");

            if (!file.exists()) {
                System.out.println("Please create a config.json");
                System.exit(-1);
                return;
            }

            final FileReader fileReader = new FileReader(file);

            final BufferedReader bufferedReader = new BufferedReader(fileReader);

            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();

            this.config = new Gson().fromJson(stringBuilder.toString(), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
