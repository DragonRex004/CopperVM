package de.dragonrex.cli;

import java.nio.file.Path;
import java.util.List;

public class CliConfig {

    private final Command command;
    private final Path outputFile;
    private final List<Path> inputFiles;

    public CliConfig(Command command, Path outputFile, List<Path> inputFiles) {
        this.command = command;
        this.outputFile = outputFile;
        this.inputFiles = inputFiles;
    }

    public Command getCommand() {
        return command;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public List<Path> getInputFiles() {
        return inputFiles;
    }
}

