package com.dt199g.project.data;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Used to get data from a Client.
 *
 * @author Simon Karlsson
 */
public interface Repository {
    /**
     * Get the given person's data.
     *
     * @param name the name of the person
     * @return the person's data
     */
    Flowable<String> getPerson(String name);

    /**
     * Get all genres.
     *
     * @return all genres
     */
    Flowable<String> getGenres();

    /**
     * Get a movie based on the supplied filter.
     *
     * @param filter the filter to use to find a movie
     * @return a movie that the filter applies to
     */
    Flowable<String> getMovie(String filter);

    /**
     * Get a movie based on the supplied filter and sorting method. For example, if sorted by
     * popularity the most popular movie will be returned.
     *
     * @param filter the filter to use to find a movie
     * @param sortBy the criteria to sort movies by
     * @return the top ranking movie according to the sorting method that the filter applies to
     */
    Flowable<String> getMovie(String filter, String sortBy);
}
