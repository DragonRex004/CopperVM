package de.dragonrex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import de.dragonrex.cli.CliConfig;
import de.dragonrex.cli.CliParser;

public class CopperAssembler {
    private Map<String, Integer> labels = new HashMap<>();
    private List<Integer> machineCode = new ArrayList<>();
    private List<ParsedLine> parsedLines = new ArrayList<>();
    private int[] program = new int[0];

    static class ParsedLine {
        String instruction;
        String operand;
        int address;

        ParsedLine(String instruction, String operand, int address) {
            this.instruction = instruction;
            this.operand = operand;
            this.address = address;
        }
    }

    public int[] assemble(String sourceCode) throws AssemblerException {
        // Pass 1: Labels sammeln und Zeilen parsen
        firstPass(sourceCode);

        // Pass 2: Code generieren
        secondPass();

        return machineCode.stream().mapToInt(i -> i).toArray();
    }

    private void firstPass(String sourceCode) throws AssemblerException {
        String[] lines = sourceCode.split("\n");
        int address = 0;

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum].trim();

            // Kommentare entfernen
            int commentPos = line.indexOf(';');
            if (commentPos >= 0) {
                line = line.substring(0, commentPos).trim();
            }

            if (line.isEmpty()) continue;

            // Label erkennen
            if (line.endsWith(":")) {
                String label = line.substring(0, line.length() - 1);
                labels.put(label, address);
                continue;
            }

            // Instruction parsen
            String[] parts = line.split("[,\\s]+", 2);
            String instruction = parts[0].toUpperCase();
            String operand = parts.length > 1 ? parts[1].trim() : null;

            // MOV mit Register behandeln
            if (instruction.equals("MOV") && operand != null) {
                String[] movParts = operand.split(",");
                if (movParts.length == 2) {
                    String reg = movParts[0].trim().toUpperCase();
                    instruction = "MOV_" + reg;
                    operand = movParts[1].trim();
                }
            }

            // PUSH mit Register behandeln
            if (instruction.equals("PUSH") && operand != null && Registers.REGISTERS.containsKey(operand.toUpperCase())) {
                instruction = "PUSH_" + operand.toUpperCase();
                operand = null;
            }

            // POP mit Register behandeln
            if (instruction.equals("POP") && operand != null && Registers.REGISTERS.containsKey(operand.toUpperCase())) {
                instruction = "POP_" + operand.toUpperCase();
                operand = null;
            }

            // INC/DEC mit Register
            if ((instruction.equals("INC") || instruction.equals("DEC")) && operand != null) {
                instruction = instruction + "_" + operand.toUpperCase();
                operand = null;
            }

            if (!OpCodes.OPCODES.containsKey(instruction)) {
                throw new AssemblerException("Unknown instruction: " + instruction + " at line " + (lineNum + 1));
            }

            parsedLines.add(new ParsedLine(instruction, operand, address));
            address++; // Opcode

            // Operand braucht zusätzlichen Speicherplatz
            if (operand != null) {
                address++;
            }
        }
    }

    private void secondPass() throws AssemblerException {
        for (ParsedLine line : parsedLines) {
            int opcode = OpCodes.OPCODES.get(line.instruction);
            machineCode.add(opcode);

            if (line.operand != null) {
                int value;

                // Label auflösen
                if (labels.containsKey(line.operand)) {
                    value = labels.get(line.operand);
                }
                // Hexadezimal
                else if (line.operand.startsWith("0x") || line.operand.startsWith("0X")) {
                    value = Integer.parseInt(line.operand.substring(2), 16);
                }
                // Dezimal
                else {
                    try {
                        value = Integer.parseInt(line.operand);
                    } catch (NumberFormatException e) {
                        throw new AssemblerException("Invalid operand: " + line.operand);
                    }
                }

                machineCode.add(value);
            }
        }
    }

    public void assembleFiles(List<String> inputPaths) throws IOException, AssemblerException {
        // Alle Dateien zu einem großen Source-Code zusammenfügen
        StringBuilder combinedSource = new StringBuilder();

        for (String inputPath : inputPaths) {
            String sourceCode = new String(Files.readAllBytes(Paths.get(inputPath)));
            combinedSource.append(sourceCode);
            combinedSource.append("\n");
        }

        // Einmal assemblieren mit allen Labels
        this.program = assemble(combinedSource.toString());
    }

    public void saveProgramToFile(String filePath) {
        try {
            if (!filePath.endsWith(".cux")) {
                filePath += ".cux";
            }

            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
                for (int instruction : this.program) {
                    dos.writeInt(instruction);
                }
            }

            System.out.println("Program saved to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class AssemblerException extends Exception {
        public AssemblerException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        try {
            CliConfig config = CliParser.parse(args);
            CopperAssembler assembler = new CopperAssembler();

            switch (config.getCommand()) {
                case ASSEMBLE -> {
                    System.out.println("Output: " + config.getOutputFile());
                    System.out.println("Inputs:");
                    List<String> inputPaths = new ArrayList<>();
                    config.getInputFiles().forEach(p -> {
                        System.out.println("  " + p);
                        inputPaths.add(p.toString());
                    });
                    assembler.assembleFiles(inputPaths);
                    assembler.saveProgramToFile(config.getOutputFile().toString());
                }
                default -> System.out.println("Invalid command: " + config.getCommand());
            }
        } catch (IOException | AssemblerException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

