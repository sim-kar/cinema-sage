package com.dt199g.project;

import java.io.IOException;
import java.io.InputStream;

/**
 * ...
 *
 * @author ...
 */
public class Project {

    /**
     * Main point of program entry.
     * @param args application arguments
     */
    static public void main(String... args) {
        /*
            Dummy implementation. Replace with your own solution.
         */
        try (InputStream inputStream = Project.class.getResourceAsStream("/tmp.txt")) {
            assert inputStream != null;
            System.out.println(new String(inputStream.readAllBytes()));
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }
}
