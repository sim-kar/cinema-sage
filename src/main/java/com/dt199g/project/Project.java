package com.dt199g.project;

import com.dt199g.project.application.MovieTranslator;
import com.dt199g.project.application.MovieService;
import com.dt199g.project.application.Translator;
import com.dt199g.project.application.Service;
import com.dt199g.project.data.Client;
import com.dt199g.project.data.MovieClient;
import com.dt199g.project.data.MovieRepository;
import com.dt199g.project.data.Repository;
import com.dt199g.project.presentation.Chatbot;
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
public class Project {
    private static final String API_URL = "https://api.themoviedb.org/3";
    private static String apiKey = "";

    static {
        try (InputStream inputStream = Project.class.getResourceAsStream("/apiKey")) {
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
