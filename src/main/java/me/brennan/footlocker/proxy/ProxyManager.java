package me.brennan.footlocker.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class ProxyManager {
    private final List<Proxy> proxies = new LinkedList<>();
    private final Random random = new Random();

    public ProxyManager() {
        final File file = new File("proxies.txt");

        if (file.exists())
        {
            try {
                final FileReader fileReader = new FileReader(file);

                final BufferedReader bufferedReader = new BufferedReader(fileReader);

                String string;
                while ((string = bufferedReader.readLine()) != null) {
                    final String[] split = string.split(":");

                    if(split.length > 3) {
                        proxies.add(new Proxy(split[0], Integer.parseInt(split[1]), split[2], split[3]));
                    } else {
                        proxies.add(new Proxy(split[0], Integer.parseInt(split[1])));
                    }
                }

                bufferedReader.close();
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int size() {
        return proxies.size();
    }

    public Proxy randomProxy() {
        if(proxies.isEmpty())
            return null;
        final Proxy proxy = proxies.get(random.nextInt(proxies.size()));
        proxies.remove(proxy);

        return proxy;
    }
}
