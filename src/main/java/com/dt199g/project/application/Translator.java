package com.dt199g.project.application;

import io.reactivex.rxjava3.core.Observable;

/**
 * Used to make translate natural language requests into parameters that can be used to service
 * the request, and then translates the response back into natural language.
 *
 * @author Simon Karlsson
 */
public interface Translator {

    /**
     * Sends a natural language request to be serviced, and returns a natural language response.
     *
     * @param request the natural language request to service
     * @return the response in natural language
     */
    Observable<String> makeRequest(String request);
}
