package com.lavakumar.middlewarerouter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RouterImpl implements Router {

    static class TrieNode {
        Map<String, TrieNode> children;
        String value;

        TrieNode(){
            children = new HashMap<>();
        }
    }

    TrieNode root;
    RouterImpl(){
        root = new TrieNode();
    }

    @Override
    public void withRoute(String path, String result) {
       // if(path.length()<=1) return;

        String[] paths = path.split("/");
        insert(paths, result);
    }

    public void insert(String[] paths, String value){
        TrieNode current = root;
        for(String path: paths){
            TrieNode trieNode = current.children.get(path);
            if(trieNode == null){
                trieNode = new TrieNode();
                current.children.put(path, trieNode);
            }
            current = trieNode;
        }
        current.value = value;
    }


    @Override
    public String route(String path) {
        String[] paths = path.split("/");
        TrieNode node = search(paths);
        return node == null ? null : node.value;
    }


    public TrieNode search(String[] paths){
        return searchHelper(paths, root,0);
    }

    public TrieNode searchHelper(String[] paths, TrieNode root, int index){
        TrieNode current = root;
        for(int i=index;i<paths.length;i++){
            if(Objects.equals(paths[i], "*")){
                for(Map.Entry<String,TrieNode> entry: current.children.entrySet()){
                    if(entry.getKey()!=null){
                        return searchHelper(paths,entry.getValue(), i+1);
                    }
                }
            }
            TrieNode trieNode = current.children.get(paths[i]);
            if(trieNode == null){
                return null;
            }
            current = trieNode;
        }
        return current;
    }



}
