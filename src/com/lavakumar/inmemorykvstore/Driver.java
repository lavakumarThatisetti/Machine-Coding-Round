package com.lavakumar.inmemorykvstore;

import java.util.ArrayList;
import java.util.List;

public class Driver {
    public static void main(String[] args) {

        KeyValueStore keyValueStore = new KeyValueStore();

        List<Pair<String, String>> dsaBootCamps = new ArrayList<>();
        dsaBootCamps.add(new Pair<>("title", "DSA-Bootcamp"));
        dsaBootCamps.add(new Pair<>("price", "10000.00"));
        dsaBootCamps.add(new Pair<>("enrolled", "true"));
        dsaBootCamps.add(new Pair<>("estimated_time", "30"));

        keyValueStore.put("dsa_bootcamp", dsaBootCamps);


        System.out.println(keyValueStore.get("dsa_bootcamp"));

        System.out.println(keyValueStore.keys());

        System.out.println(keyValueStore.search("estimated_time", "30"));

        List<Pair<String, String>> sdeBootCamps = new ArrayList<>();
        sdeBootCamps.add(new Pair<>("title", "SDE-Bootcamp"));
        sdeBootCamps.add(new Pair<>("price", "10000.00"));
        sdeBootCamps.add(new Pair<>("enrolled", "false"));
        sdeBootCamps.add(new Pair<>("estimated_time", "30"));

        keyValueStore.put("sde_bootcamp", sdeBootCamps);


        System.out.println(keyValueStore.keys());

        System.out.println(keyValueStore.search("estimated_time", "30"));
    }
}
