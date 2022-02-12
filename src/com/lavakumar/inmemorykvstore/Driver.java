package com.lavakumar.inmemorykvstore;

import java.util.HashMap;
import java.util.Map;

public class Driver {


    public static void main(String[] args) {

        try {
            InMemoryDB<String, Map<String, Object>> inMemoryDB = new InMemoryDB<>();

            // put sde_bootcamp title SDE-Bootcamp price 30000.00 enrolled false estimated_time 30
            Map<String, Object> value = new HashMap<>();
            value.put("title", "SDE-BootCamp");
            value.put("price", 30000.00);
            value.put("enrolled", false);
            value.put("estimated_time", 30);
            inMemoryDB.put("sde_bootcamp", value);

            //get sde_bootcamp
            System.out.println(inMemoryDB.get("sde_bootcamp"));

            //put sde_kickstart title SDE-Kickstart price 4000 enrolled true estimated_time 8
            value = new HashMap<>();
            value.put("title", "SDE-Kickstart");
            value.put("price", 4000);
            value.put("enrolled", true);
            value.put("estimated_time", 8);
            inMemoryDB.put("sde_kickstart", value);
            //get sde_kickstart
            // Data Type Error
            System.out.println(inMemoryDB.get("sde_kickstart"));

            //put sde_kickstart title SDE-Kickstart price 4000.00 enrolled true estimated_time 8
            value = new HashMap<>();
            value.put("title", "SDE-Kickstart");
            value.put("price", 4000.00);
            value.put("enrolled", true);
            value.put("estimated_time", 8);
            inMemoryDB.put("sde_kickstart", value);

            //get sde_kickstart
            System.out.println(inMemoryDB.get("sde_kickstart"));






        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}


