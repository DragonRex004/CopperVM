package de.dragonrex;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopperAssembler {

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

    public static void main(String[] args) {
        int[] simple_calculator_program = {
                0x20, 0x03E8,
                0x80, 0x01,
                0x80, 0x03,
                0x24,
                0x80, 0x03,
                0x24,
                0x40,
                0x28,
                0x80, 0x01,
                0x60, 0x04
        };

        try {
            saveProgramToFile(simple_calculator_program, "test_programs/simple_calculator.cux");
        } catch (IOException e) {
            System.err.println("Error saving program: " + e.getMessage());
        }
    }
}
