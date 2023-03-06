package com.dt199g.project.application;

/**
 * Used to make requests.
 *
 * @author Simon Karlsson
 */
public interface Request {

    /**
     * Sends a request to be serviced.
     *
     * @param request the request to service
     */
    void send(String request);
}
