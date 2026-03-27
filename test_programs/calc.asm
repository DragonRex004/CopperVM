; Easy Calculator
start:
        CALL read_console    ; Erste Zahl einlesen
        CALL read_console    ; Zweite Zahl einlesen
        ADD                  ; Addieren
        POP AX               ; Ergebnis holen
        SYSCALL 1            ; Print AX
        JMP start            ; Loop