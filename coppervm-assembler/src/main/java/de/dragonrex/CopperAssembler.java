package de.dragonrex;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CopperAssembler {
    private Map<String, Integer> labels = new HashMap<>();
    private List<Integer> machineCode = new ArrayList<>();
    private List<ParsedLine> parsedLines = new ArrayList<>();

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

    public void assembleFile(String inputPath, String outputPath) throws IOException, AssemblerException {
        String sourceCode = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inputPath)));
        int[] program = assemble(sourceCode);
        saveProgramToFile(program, outputPath);
    }

    public static void saveProgramToFile(int[] program, String filePath) throws IOException {
        if (!filePath.endsWith(".cux")) {
            filePath += ".cux";
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            for (int instruction : program) {
                dos.writeInt(instruction);
            }
        }

        System.out.println("Program saved to: " + filePath);
    }

    static class AssemblerException extends Exception {
        public AssemblerException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: CopperAssembler <input.asm> <output.cux>");
            System.exit(1);
        }

        CopperAssembler assembler = new CopperAssembler();
        try {
            assembler.assembleFile(args[0], args[1]);
            System.out.println("Assembly successful!");
        } catch (IOException | AssemblerException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}

