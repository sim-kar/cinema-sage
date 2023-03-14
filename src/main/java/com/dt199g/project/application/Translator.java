package com.dt199g.project.application;

/**
 * Used to make translate requests and responses to and from a chatbot.
 *
 * @author Simon Karlsson
 */
public interface Translator {

    /**
     * Sends a request to be serviced.
     *
     * @param request the request to service
     */
    void send(String request);
}
