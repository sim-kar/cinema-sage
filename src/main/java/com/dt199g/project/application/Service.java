package com.dt199g.project.application;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Used to service requests.
 *
 * @author Simon Karlsson
 */
public interface Service {

    /**
     * Try to find a movie with the given parameters.
     *
     * @param genre the genre of the movie
     * @param name the name of a person in the movie
     * @param year the year the movie was released
     */
    Flowable<String> findMovie(String genre, String name, String year);
}
