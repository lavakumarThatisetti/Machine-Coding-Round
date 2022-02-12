package com.lavakumar.inmemorykvstore;

import com.lavakumar.inmemorykvstore.objects.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class testMAp {
    public static void main(String[] args) {
        HashMap<Object,Object> map =new HashMap<>();
        List<Pair> pairList = new ArrayList<>();
        pairList.add(new Pair("Lava","Kumar"));
        map.put(1,2);
        map.put("lava",pairList);
        map.put("kumar",new Pair("Hello","Bye"));

        for(Map.Entry<Object,Object> entry: map.entrySet()){
            System.out.println(entry.getKey()+"   "+entry.getValue());
        }
    }
}
