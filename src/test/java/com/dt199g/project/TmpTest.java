package com.dt199g.project;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TmpTest {
    @Test
    public void testLoadingResource() {
        try (InputStream inputStream = Project.class.getResourceAsStream("/tmp.txt")) {
            assert inputStream != null;
            String text = new String(inputStream.readAllBytes());
            assertEquals(text, "Data loaded from test resources!");
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }
}
