package com.aluracursos.screenmatch.service;

// Importo las librerias
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;  //importo los servicios para enviar solicitudes HTTP y recibir respuestas HTTP de un recurso identificado por un URI.
import java.net.http.HttpRequest;  //importo los servicios para que ASP.NET lea los valores HTTP enviados por un cliente durante una solicitud web.
import java.net.http.HttpResponse;  //importo los servicios para encapsular la información de la respuesta HTTP de una operación ASP.NET


public class ConsumoAPI {
    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        String json = response.body();
        return json;
    }
}
