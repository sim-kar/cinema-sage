package com.sim_kar.cinema_sage.data;

import io.reactivex.rxjava3.core.Observable;

/**
 * A client for making HTTP requests.
 *
 * @author Simon Karlsson
 */
public interface Client {
    /**
     * Send a GET request to the client's server. The query should not be the entire URL, bur rather
     * just the endpoint (beginning with '/') and query parameters. For example:
     * '/employees?lastName=Smith&age=30'.
     *
     * @param query using the format '/endpoint?paramter=value&anotherparameter=value'
     * @return the body of the HTTP response as a single string
     */
    Observable<String> sendRequest(String query);
}
