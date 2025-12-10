# CopperVM Befehlssatz

## Übersicht

Der CopperVM Befehlssatz ist in 8 Kategorien unterteilt, die jeweils einen Bereich von 16 Opcodes (0x00-0xFF) umfassen.

---

## 1. Systemsteuerung (0x00-0x0F)

| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x00   | HALT     | -         | VM stoppen und Ausführung beenden |
| 0x01   | NOP      | -         | Keine Operation (No Operation) |

---

## 2. Stack-Operationen (0x10-0x1F)

| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x10   | PUSH     | <wert>    | Wert auf den Stack legen |
| 0x11   | POP      | -         | Obersten Wert vom Stack entfernen |
| 0x12   | DUP      | -         | Obersten Wert duplizieren |
| 0x13   | SWAP     | -         | Oberste zwei Werte auf dem Stack tauschen |

---

## 3. Register-Operationen (0x20-0x3F)

### Wert in Register laden (MOV)
| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x20   | MOV_AX   | <wert>    | Wert in Register AX laden |
| 0x21   | MOV_BX   | <wert>    | Wert in Register BX laden |
| 0x22   | MOV_CX   | <wert>    | Wert in Register CX laden |
| 0x23   | MOV_DX   | <wert>    | Wert in Register DX laden |

### Register auf Stack (PUSH_REG)
| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x24   | PUSH_AX  | -         | Wert von AX auf Stack legen |
| 0x25   | PUSH_BX  | -         | Wert von BX auf Stack legen |
| 0x26   | PUSH_CX  | -         | Wert von CX auf Stack legen |
| 0x27   | PUSH_DX  | -         | Wert von DX auf Stack legen |

### Stack in Register (POP_REG)
| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x28   | POP_AX   | -         | Obersten Stack-Wert nach AX kopieren |
| 0x29   | POP_BX   | -         | Obersten Stack-Wert nach BX kopieren |
| 0x2A   | POP_CX   | -         | Obersten Stack-Wert nach CX kopieren |
| 0x2B   | POP_DX   | -         | Obersten Stack-Wert nach DX kopieren |

---

## 4. Arithmetik (0x40-0x4F)

| Opcode | Mnemonic | Operanden | Beschreibung | Flags |
|--------|----------|-----------|--------------|-------|
| 0x40   | ADD      | -         | Addition (pop b, pop a, push a+b) | ZF, NF, CF, OF |
| 0x41   | SUB      | -         | Subtraktion (pop b, pop a, push a-b) | ZF, NF, CF |
| 0x42   | MUL      | -         | Multiplikation (pop b, pop a, push a*b) | ZF, NF, OF |
| 0x43   | DIV      | -         | Division (pop divisor, pop dividend, push quotient) | ZF, NF, CF, OF |
| 0x44   | MOD      | -         | Modulo (pop divisor, pop dividend, push rest) | ZF, NF, OF |
| 0x45   | INC_AX   | -         | Register AX um 1 erhöhen | ZF, NF, OF |
| 0x46   | DEC_AX   | -         | Register AX um 1 verringern | ZF, NF |

### Flag-Bedeutungen
- **ZF (Zero Flag)**: Ergebnis ist 0
- **NF (Negative Flag)**: Ergebnis ist negativ
- **CF (Carry Flag)**: Übertrag/Unterlauf oder Rest vorhanden
- **OF (Overflow Flag)**: Arithmetischer Überlauf oder Division durch 0

---

## 5. Logik & Vergleich (0x50-0x5F)

| Opcode | Mnemonic | Operanden | Beschreibung | Flags |
|--------|----------|-----------|--------------|-------|
| 0x50   | AND      | -         | Bitweises UND (pop b, pop a, push a&b) | ZF, NF |
| 0x51   | OR       | -         | Bitweises ODER (pop b, pop a, push a\|b) | ZF, NF |
| 0x52   | XOR      | -         | Bitweises XOR (pop b, pop a, push a^b) | ZF, NF |
| 0x53   | NOT      | -         | Bitweises NICHT (pop a, push ~a) | ZF, NF |
| 0x54   | CMP      | -         | Vergleich (pop b, pop a, berechne a-b, setze Flags) | ZF, NF, CF |

**Hinweis:** CMP funktioniert wie SUB, legt aber kein Ergebnis auf den Stack - nur Flags werden gesetzt.

---

## 6. Sprungbefehle (0x60-0x6F)

| Opcode | Mnemonic | Operanden | Beschreibung | Bedingung |
|--------|----------|-----------|--------------|-----------|
| 0x60   | JMP      | <addr>    | Unbedingter Sprung zu Adresse | - |
| 0x61   | JZ       | <addr>    | Sprung wenn Zero Flag gesetzt | ZF = 1 |
| 0x62   | JNZ      | <addr>    | Sprung wenn Zero Flag nicht gesetzt | ZF = 0 |
| 0x63   | JG       | <addr>    | Sprung wenn größer | ZF = 0 && NF = 0 |
| 0x64   | JL       | <addr>    | Sprung wenn kleiner | NF = 1 |
| 0x65   | CALL     | <addr>    | Funktionsaufruf (PC auf Stack, springe zu addr) | - |
| 0x66   | RET      | -         | Rücksprung (pop Adresse vom Stack, springe dorthin) | - |

---

## 7. Speicher (0x70-0x7F)

| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x70   | LOAD     | <addr>    | Wert aus Speicheradresse laden und auf Stack legen |
| 0x71   | STORE    | <addr>    | Obersten Stack-Wert in Speicheradresse schreiben |
| 0x72   | LOAD_IND | -         | Indirekte Adressierung: AX enthält Adresse, Wert auf Stack |
| 0x73   | STORE_IND| -         | Indirekt speichern: AX enthält Adresse, Stack-Wert speichern |

---

## 8. I/O (0x80-0x8F)

| Opcode | Mnemonic | Operanden | Beschreibung |
|--------|----------|-----------|--------------|
| 0x80   | PRINT_AX | -         | Inhalt von Register AX ausgeben |
| 0x81   | PRINT_STACK | -      | Obersten Stack-Wert ausgeben (ohne pop) |
| 0x82   | READ     | -         | Benutzereingabe einlesen und in AX speichern |

---

## Beispielprogramme

### Beispiel 1: Addition zweier Zahlen
```
PUSH 10        ; 0x10, 0x0A
PUSH 20        ; 0x10, 0x14
ADD            ; 0x40
POP_AX         ; 0x28
PRINT_AX       ; 0x80
HALT           ; 0x00
```

**Bytecode:** `0x10 0x0A 0x10 0x14 0x40 0x28 0x80 0x00`

### Beispiel 2: Schleife (Countdown)
```
MOV_CX 5       ; 0x22, 0x05 - Counter auf 5 setzen
PUSH_CX        ; 0x26       - CX auf Stack
PRINT_STACK    ; 0x81       - Aktuellen Wert ausgeben
DEC_CX         ; Hypothetisch: CX dekrementieren
PUSH_CX        ; 0x26
PUSH 0         ; 0x10, 0x00
CMP            ; 0x54       - Vergleiche CX mit 0
JNZ -10        ; 0x62, addr - Springe zurück wenn CX != 0
HALT           ; 0x00
```

### Beispiel 3: Funktionsaufruf
```
MOV_BX 10      ; 0x21, 0x0A - Funktionsadresse
CALL BX        ; 0x65       - Springe zu Funktion
HALT           ; 0x00

; Funktion bei Adresse 10
PUSH 42        ; 0x10, 0x2A
POP_AX         ; 0x28
PRINT_AX       ; 0x80
RET            ; 0x66       - Zurück zum Aufrufer
```

---

## Erweiterungsmöglichkeiten

Reservierte Bereiche für zukünftige Erweiterungen:
- **0x07-0x0F**: Erweiterte Systemsteuerung
- **0x14-0x1F**: Erweiterte Stack-Operationen
- **0x2C-0x3F**: Weitere Register-Operationen
- **0x47-0x4F**: Erweiterte Arithmetik (Shift, Rotate)
- **0x55-0x5F**: Erweiterte Logik
- **0x67-0x6F**: Weitere Sprungbefehle
- **0x74-0x7F**: Erweiterte Speicheroperationen
- **0x83-0x8F**: Erweiterte I/O
- **0x90-0xFF**: Frei für spezielle Operationen

---

## Architektur

- **Register**: AX, BX, CX, DX (General Purpose)
- **Flag Register**: ZF, CF, NF, OF
- **Program Counter (PC)**: Zeigt auf aktuelle Instruktion
- **Stack**: LIFO-Datenstruktur
- **Memory**: 1024 Worte (konfigurierbar)

---

## Lizenz

CopperVM © 2025