package edu.escuelaing.app;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class URLReader {
    public static void main(String[] args) throws Exception {
        String site = "https://i.pinimg.com/736x/d6/4a/d3/d64ad34f0661e8a5a000ff8a5ff78346.jpg";

        // Crea el objeto que representa una URL
        URL siteURL = new URL(site);

        // Crea el objeto que URLConnection
        URLConnection urlConnection = siteURL.openConnection();

        // Obtiene los campos del encabezado y los almacena en un estructura Map
        Map<String, List<String>> headers = urlConnection.getHeaderFields();

        // Obtiene una vista del mapa como conjunto de pares <K,V>
        // para poder navegarlo
        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();

        // Recorre la lista de campos e imprime los valores
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String headerName = entry.getKey();
            //Si el nombre es nulo, significa que es la linea de estado
            if(headerName !=null){
                System.out.print(headerName + ":");
            }
            List<String> headerValues = entry.getValue();
            for (String value : headerValues) {
                System.out.print(value);
            }
            System.out.println("");
            //System.out.println("");
        }

        System.out.println("-------message-body------");

        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        }catch (IOException x) {
            System.err.println(x);
        }
    }
 }
