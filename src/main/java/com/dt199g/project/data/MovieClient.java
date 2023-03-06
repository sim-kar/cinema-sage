package com.dt199g.project.data;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A client that makes requests to a REST API.
 *
 * @author Simon Karlsson
 */
public class MovieClient implements Client {
    private final String apiUrl;
    private final Flowable<HttpClient> client;
    private final String apiKey;

    /**
     * Initialize a new MovieClient. If using an API key, make sure to include it as a query
     * parameter, e.g. '&api_key=secret_key' and not just 'secret_key'.
     *
     * @param client the HTTP client to use
     * @param apiUrl the URL to the API to use (do not include a '/' at the end)
     * @param apiKey the API key as a query parameter
     */
    public MovieClient(HttpClient client, String apiUrl, String apiKey) {
        // Flowable for backpressure; same client can be used to service many requests; scalable
        this.client = Flowable.just(client);
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * {@inheritDoc}
     * Returns an empty string is the request is unsuccessful for any reason.
     *
     * @param query using the format '/endpoint?paramter=value&anotherparameter=value'
     * @return the body of the HTTP response as a single string;
     *         or an empty string if the request is unsuccessful
     */
    @Override
    public Flowable<String> sendRequest(String query) {
        return client
                .subscribeOn(Schedulers.io()) // getResponse is blocking
                .map(client -> getResponse(client, buildRequest(query)))
                .filter(response -> response.statusCode() == 200)
                .map(HttpResponse::body);
    }

    /**
     * Builds an HTTP request to the API with the query and API key.
     *
     * @param query using the format '/endpoint?paramter=value&anotherparameter=value'
     * @return the HTTP request
     */
    private HttpRequest buildRequest(String query) {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + query + apiKey))
                .GET()
                .build();
    }

    /**
     * Sends an HTTP request and returns the HTTP response. Blocks while waiting for the response.
     *
     * @param client the HTTP client to use
     * @param request the HTTP request to send
     * @return the HTTP response
     * @throws IOException an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    private HttpResponse<String> getResponse(HttpClient client, HttpRequest request)
            throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
