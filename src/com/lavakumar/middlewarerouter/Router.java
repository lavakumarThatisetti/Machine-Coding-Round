package com.lavakumar.middlewarerouter;

public interface Router {

    void withRoute(String path, String result);
    String route(String path);
}
