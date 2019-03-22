package com.oocode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, VegPrice> prices = new HashMap<>();

        while (true) {
            String l;
            Request request = new Request.Builder()
                    .url("https://dry-fjord-40481.herokuapp.com/latest")
                    .build();

            try (Response e = new OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .build().newCall(request).execute()) {
                try (ResponseBody x = e.body()) {
                    assert x != null;
                    l = x.string();
                }
            }
            for (String le : l.split("\n")) {
                String[] s = le.split(",");
                String[] cv = s[1].trim().split(" ");
                int v = parseInt(cv[0]);
                String c = cv[1];
                if (!prices.containsKey(c) || v <= prices.get(c).price) {
                    prices.put(c, new VegPrice(s[0], c, v));
                }
            }
            StringBuilder m = new StringBuilder();
            for (VegPrice value : prices.values().stream()
                    .sorted(comparing(o -> o.vegName))
                    .collect(toList())) {
                m.append(String.format("%s, %s, %s",
                        value.vegName, value.price, value.shopName));
                m.append("\n");
            }
            System.out.println(m.toString());
            Thread.sleep(1000 * 60 * 5);
        }
    }
}
