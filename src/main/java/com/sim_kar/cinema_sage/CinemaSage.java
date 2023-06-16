package com.sim_kar.cinema_sage;

import com.sim_kar.cinema_sage.application.MovieTranslator;
import com.sim_kar.cinema_sage.application.MovieService;
import com.sim_kar.cinema_sage.application.Translator;
import com.sim_kar.cinema_sage.application.Service;
import com.sim_kar.cinema_sage.data.Client;
import com.sim_kar.cinema_sage.data.MovieClient;
import com.sim_kar.cinema_sage.data.MovieRepository;
import com.sim_kar.cinema_sage.data.Repository;
import com.sim_kar.cinema_sage.presentation.Chatbot;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;

/**
 * An application that lets the user chat to a chatbot that provides movie recommendations. It will
 * ask the user to provide a genre, year and/or the name of a cast or crew member, and will respond
 * with a movie that meet the criteria, provided that such a movie exists. The application
 * communicates with the TMDB API to get movie data. Note that the API requires a key, which must be
 * present as a file named "apiKey" in the resource folder.
 *
 * @author Simon Karlsson
 */
public class CinemaSage {
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static String apiKey = "";

    static {
        try (InputStream inputStream = CinemaSage.class.getResourceAsStream("/apiKey")) {
            assert inputStream != null;
            apiKey = "&api_key=" + new String(inputStream.readAllBytes());
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }

    /**
     * Main point of program entry.
     * @param args application arguments
     */
    static public void main(String... args) {
        // initialize all classes and inject dependencies here at the root of the project
        HttpClient httpClient = HttpClient.newHttpClient();
        Client client = new MovieClient(httpClient, API_URL, apiKey);
        Repository repository = new MovieRepository(client);
        Service service = new MovieService(repository);
        Translator translator = new MovieTranslator(service);
        Chatbot chatbot = new Chatbot(translator);

        chatbot.start();
    }
}
