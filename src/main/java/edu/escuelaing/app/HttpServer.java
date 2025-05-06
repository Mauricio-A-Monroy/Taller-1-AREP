package edu.escuelaing.app;

import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpServer{
    public static void main(String[]args) throws IOException, URISyntaxException {
        ServerSocket serverSocket =null;
        try{
            serverSocket = new ServerSocket(35000);
        }catch(IOException e){
            System.err.println("Couldnotlistenon port: 35000.");
            System.exit(1);
            }
        boolean running = true;

        int idCounter = 0;
        Map<Integer, String> dataStore = new HashMap<>();

        while (running) {

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));

            BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream());

            String inputLine, outputLine;

            boolean isFirstLine = true;
            String filePath = "";
            String HTTPRequest = "";
            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine){
                    HTTPRequest = inputLine.split(" ")[0];
                    filePath = inputLine.split(" ")[1];
                    isFirstLine = false;
                }

                if (!in.ready()) {
                    break;
                }
            }

            if (filePath.equals("/")){
                filePath = "/index.html";
            }

            System.out.println("Request: " + HTTPRequest);
            System.out.println("FilePath: " + filePath);

            File file = new File("public" + filePath);

            URI resourceURI = new URI(filePath);

            System.out.println("URI: "+ resourceURI.getQuery());

            System.out.println("Counter: " + idCounter);

            if (filePath.startsWith("/app/rest-service")) {
                handleRestRequest(HTTPRequest, filePath, resourceURI.getQuery(),out, dataStore, idCounter);
                if (HTTPRequest.equals("POST")) {
                    idCounter += 1;
                }
            } else {


                if (file.exists() && !file.isDirectory()) {
                    String contentType = getContentType(filePath);

                    // Leer el archivo
                    byte[] fileData = readFileData(file);

                    // Enviar respuesta HTTP
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: " + contentType);
                    out.println("Content-Length: " + fileData.length);
                    out.println();
                    out.flush();

                    dataOut.write(fileData, 0, fileData.length);
                    dataOut.flush();
                } else {
                    // Archivo no encontrado
                    String errorMessage = "HTTP/1.1 404 Not Found\r\n"
                            + "Content-Type: text/html\r\n"
                            + "\r\n"
                            + "<h1>404 File Not Found</h1>";
                    out.println(errorMessage);
                }
            }

            System.out.println("--------------------------------------------");

            out.close();
            in.close();
            dataOut.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream"; // Tipo genérico
        }
    }

    private static byte[] readFileData(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileData = new byte[(int) file.length()];
        fileInputStream.read(fileData);
        fileInputStream.close();
        return fileData;
    }

    private static void handleRestRequest(String method, String filePath, String requestBody, PrintWriter out, Map<Integer, String> dataStore, int idCounter) {
        String response = "";
        String idParam = filePath.replace("/app/rest-service/", "").trim();

        if (method.equals("GET")) {
            // Convertir los valores a una lista de strings con comillas
            StringBuilder namesJson = new StringBuilder("[ \n ");
            for (int i = 0 ; i < dataStore.size() ; i++) {
                namesJson.append("\n {\"id\":").
                        append(String.valueOf(i+1) + ", ").
                        append("\"name\": \"").
                        append(dataStore.get(i) + "\" },");
            }

            // Eliminar la última coma y cerrar el arreglo
            if (namesJson.length() > 1) {
                namesJson.deleteCharAt(namesJson.length() - 1);
            }
            namesJson.append("]");

            // Crear la respuesta JSON
            response = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{ \"names\": " + namesJson.toString() + " }";
        } else if (method.equals("POST")) {
            String newName = requestBody.replace("name=", "").trim();
            dataStore.put(idCounter, newName);
            response = "HTTP/1.1 201 Created\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"id\":" + idCounter + ", \"name\":\"" + newName + "\"}";
        } else {
            response = "HTTP/1.1 405 Method Not Allowed\r\n"
                    + "Content-Type: application/json\r\n"
                    + "\r\n"
                    + "{\"error\":\"Method not allowed\"}";
        }

        out.println(response);
    }


}