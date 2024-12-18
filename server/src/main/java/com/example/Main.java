package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static <Static> void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(3000);
        while (true) {
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            // leggo la prima linea che mi manda il client cioè: Metodo /risorsa protocollo
            // + versione
            String firstLine = in.readLine();
            System.out.println(firstLine);
            // divido la prima linea che mi ha inviato il client e lo metto nelle string
            String[] request = firstLine.split(" ");
            String method = request[0];
            String resource = request[1];
            String versione = request[2];

            // leggo tutto finchè non mi arriva qualcossa di vuoto
            String header;
            do {
                header = in.readLine();
                System.out.println(header);
            } while (!header.isEmpty());
            // quando entra nella pagina gli compare subito l'index

            if (resource.endsWith("/")) {
                resource = resource + "index.html";
            }

            File file = new File("htdocs" + resource);

            if (file.isDirectory()) {
                out.writeBytes("HTTP/1.1 301 Moved Permanently\n");
                out.writeBytes("Content-Length: 0\n");
                out.writeBytes("Location: " + resource + "/\n");
                out.writeBytes("\n");
            }
            // creo il percorso del file
            // controllo se esiste quel file o no e poi lo mando
            if (file.exists()) {
                out.writeBytes("http/1.1 200 Ok" + "\n");
                out.writeBytes("Content-Lenght " + file.length() + "\n");
                out.writeBytes("Content-Type:" + getContentType(file) + "\n");
                out.writeBytes("\n");

                InputStream input = new FileInputStream(file);
                byte[] buf = new byte[8192];
                int n;
                while ((n = input.read(buf)) != -1) {
                    out.write(buf, 0, n);

                }
                input.close();
            } else {
                String msg = "Non trovato";
                out.writeBytes("http/1.1 404 NOT FOUND" + "\n");
                out.writeBytes("Content-Lenght " + msg.length() + "\n");
                out.writeBytes("Content-Type: text/index" + "\n");
                out.writeBytes("\n");
                out.writeBytes(msg);
            }
            s.close();
        }
    }

    public static String getContentType(File file) {
        String[] s = file.getName().split("\\.");
        String exit = s[s.length - 1];
        switch (exit) {
            case "png":
                return "image/png";
            case "js":
                return "text/js";
            case "css":
                return "text/css";
            case "html":
                return "text/html";
        }
        return exit;

    }
}