.386
.MODEL flat, stdcall
option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\user32.inc
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\user32.lib
includelib \masm32\lib\masm32.lib
dll_dllcrt0 PROTO C
printf PROTO C : VARARG
.data
	FLOAT1 REAL8 10.0
	FLOAT0 REAL8 1.8
	recursionFlaggsum DWORD 0
	i_ulongint DWORD ?
	c_ulongint DWORD ?
	p2_ulongint DWORD ?
	__new_line__ db 13, 10, 0
	result REAL8 ?
	a_ulongint DWORD ?
	recursionFlaggsuma DWORD 0
	buffer db 256 dup(?)
	a_single REAL8 ?
	b_single REAL8 ?
	b REAL8 ?
	p1_ulongintprimero DWORD ?
	e DWORD ?
	format db "%d", 0
	p1_ulongint DWORD ?
	texto3 db "funciono", 0
	formatFloat db "%f", 0
	texto2 db "hola3", 0
	e_ulongint DWORD ?
	n_ulongint DWORD ?
	errorResta db "Error: Resultado negativo detectado en la resta.", 0
	p1_ulongintsegundo DWORD ?
	texto1 db "hola2", 0
	texto0 db "hola1", 0
	AUX_ulongint DWORD ?
	errorMultiplicacion db "Error: Overflow detectado en la multiplicacion.", 0
	errorRecursion db "Error: Recursion no soportada detectada.", 0
.code
OverflowDetected:
    invoke MessageBox, 0, addr errorMultiplicacion, addr errorMultiplicacion, MB_ICONERROR
    INVOKE ExitProcess, 1

UnderflowDetected:
    invoke MessageBox, 0, addr errorResta, addr errorResta, MB_ICONERROR
    INVOKE ExitProcess, 1

recursion_error:
    invoke MessageBox, 0, addr errorRecursion, addr errorRecursion, MB_ICONERROR
    INVOKE ExitProcess, 1


gsum:
	INC recursionFlaggsum
	CMP recursionFlaggsum, 1
	JG recursion_error
	MOV EAX, 2
	MOV a_ulongint, EAX
	MOV EBX, i_ulongint
	IMUL EBX, a_ulongint
	JO OverflowDetected
	MOV ECX, EBX
	DEC recursionFlaggsum
	ret
gsuma:
	INC recursionFlaggsuma
	CMP recursionFlaggsuma, 1
	JG recursion_error
	MOV EDX, 5
	MOV AUX_ulongint, EDX
	FILD AUX_ulongint
	FSTP a_single
	FiLD i_ulongint
	FLD a_single
	FMUL
	FSTP result
	DEC recursionFlaggsuma
	ret
start:
	invoke printf, ADDR  texto0
	invoke printf, addr __new_line__
	invoke printf, ADDR  texto1
	invoke printf, addr __new_line__
	invoke printf, ADDR  texto2
	invoke printf, addr __new_line__
	MOV EAX, 2
	MOV p1_ulongintprimero, EAX
	MOV EAX, 8
	MOV p1_ulongintsegundo, EAX
	MOV EBX, 7
	IMUL EBX, 2
	JO OverflowDetected
	MOV EAX, EBX
	MOV a_ulongint, EAX
	MOV EDX, 5
	MOV AUX_ulongint, EDX
	FILD AUX_ulongint
	FSTP b_single
	FINIT
	MOV AUX_ulongint, 1
	FLD AUX_ulongint
	FiLD FLOAT0
	FCOMP st(1)
	FSTSW AX
	SAHF
	JB Label91
	MOV EAX, 1
	MOV e_ulongint, EAX
	MOV n_ulongint, 1
Label35:
	MOV EAX, 6
	MOV i_ulongint, EAX
	CALL gsuma
	FINIT
	FiLD n_ulongint
	FiLD result
	FCOMP st(1)
	FSTSW AX
	SAHF
	JB Label63
	MOV EAX, 25
	CMP EAX, n_ulongint
	JL Label63
	invoke printf, addr format, n_ulongint
	invoke printf, addr __new_line__
	MOV EBX, e_ulongint
	ADD EBX, 1
	MOV EAX, EBX
	MOV e_ulongint, EAX
	MOV EBX, n_ulongint
	ADD EBX, 1
	MOV EAX, EBX
	MOV n_ulongint, EAX
	JMP Label35
Label63:
	MOV EAX, 55
	MOV c_ulongint, EAX
	MOV EAX, 3
	MOV i_ulongint, EAX
	CALL gsuma
	FLD result
	FiLD e_ulongint
	FMUL
	fstp result
	invoke printf, addr formatFloat, result
	invoke printf, addr __new_line__
	MOV EAX, 3
	MOV i_ulongint, EAX
	CALL gsuma
	FLD result
	FiLD e_ulongint
	FMUL
	MOV AUX_ulongint, 55
	FILD AUX_ulongint
	FLD FLOAT1
	FMUL
	FADD
	FSTP b_single
	invoke printf, addr formatFloat, b_single
	invoke printf, addr __new_line__
	invoke printf, addr format, e_ulongint
	invoke printf, addr __new_line__
	JMP Label95
Label91:
	invoke printf, ADDR  texto3
	invoke printf, addr __new_line__
Label95:
	MOV EAX, 5
	MOV c_ulongint, EAX
	invoke printf, addr format, c_ulongint
	invoke printf, addr __new_line__
	INVOKE ExitProcess, 0 
END start
