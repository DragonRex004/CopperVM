package de.dragonrex.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CliParser {

    public static CliConfig parse(String[] args) {
        if (args.length != 2) {
            usageAndExit();
        }

        Path inputFolder = Path.of(args[0]);
        Path outputFile = Path.of(args[1]);

        // Prüfen ob der Input-Ordner existiert und ein Verzeichnis ist
        if (!Files.exists(inputFolder) || !Files.isDirectory(inputFolder)) {
            throw new IllegalArgumentException("Input folder does not exist or is not a directory: " + inputFolder);
        }

        // Alle .asm Dateien im Ordner sammeln
        List<Path> inputFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(inputFolder)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".asm"))
                 .forEach(inputFiles::add);
        } catch (IOException e) {
            throw new RuntimeException("Error reading input folder: " + inputFolder, e);
        }

        if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("No .asm files found in folder: " + inputFolder);
        }

        return new CliConfig(Command.ASSEMBLE, outputFile, inputFiles);
    }

    private static void usageAndExit() {
        System.err.println("""
            Usage:
              assemble <input-folder> <output.cux>

            Example:
              assemble test_programs output.cux
            """);
        System.exit(1);
    }
}

