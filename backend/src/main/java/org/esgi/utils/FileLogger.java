package org.esgi.utils;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@ApplicationScoped
public class FileLogger {

    private static final Path LOG_FILE = Path.of("reservations.log");

    public void log(String line) {
        try {
            Files.writeString(
                    LOG_FILE,
                    LocalDateTime.now() + " " + line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
