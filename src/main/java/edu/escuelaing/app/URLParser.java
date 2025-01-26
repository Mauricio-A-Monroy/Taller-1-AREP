package edu.escuelaing.app;

import java.net.MalformedURLException;
import java.net.URL;

public class URLParser {
    public static void main(String[] arg) throws MalformedURLException {
        URL site = new URL("https://co.pinterest.com:80/pin/12033123998216999/");
        System.out.println("Protocol: " + site.getProtocol());
        System.out.println("Authority: " + site.getAuthority());
        System.out.println("Host: " + site.getHost());
        System.out.println("Port: " + site.getPort());
        System.out.println("Path: " + site.getPath());
        System.out.println("Query: " + site.getQuery());
        System.out.println("File: " + site.getFile());
        System.out.println("Ref: " + site.getRef());


    }
}
