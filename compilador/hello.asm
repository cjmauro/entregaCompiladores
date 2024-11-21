.386
.model flat, stdcall
option casemap :none
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\user32.lib
INCLUDE \masm32\include\masm32rt.inc
.data
	FLOAT0 REAL8 10.0
	b REAL8 ?
	recursionFlaggsum DWORD 0
	i_ulongint DWORD ?
	c_ulongint DWORD ?
	p2_ulongint DWORD ?
	p1_ulongintprimero DWORD ?
	e DWORD ?
	format db "%d", 0
	p1_ulongint DWORD ?
	e_ulongint DWORD ?
	n_ulongint DWORD ?
	errorResta db "Error: Resultado negativo detectado en la resta.", 0
	p1_ulongintsegundo DWORD ?
	result REAL8 ?
	a_ulongint DWORD ?
	AUX_ulongint DWORD ?
	recursionFlaggsuma DWORD 0
	errorMultiplicacion db "Error: Overflow detectado en la multiplicacion.", 0
	errorRecursion db "Error: Recursion no soportada detectada.", 0
	buffer db 256 dup(?)
	a_single REAL8 ?
	b_single REAL8 ?
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
	MOV EAX, 5
	MOV i_ulongint, EAX
	CALL gsum
	MOV EAX, ECX
	CMP EAX, 1
	JL Label89
	MOV EAX, 1
	MOV e_ulongint, EAX
	MOV n_ulongint, 1
Label33:
	MOV EAX, 6
	MOV i_ulongint, EAX
	CALL gsuma
	FINIT
	FLD n_ulongint
	FLD result
	FCOMP st(1)
	FSTSW AX
	SAHF
	JL Label61
	MOV EAX, 25
	CMP EAX, n_ulongint
	JL Label61
	printf("%u\n", n_ulongint)
	MOV EBX, e_ulongint
	ADD EBX, 1
	MOV EAX, EBX
	MOV e_ulongint, EAX
	MOV EBX, n_ulongint
	ADD EBX, 1
	MOV EAX, EBX
	MOV n_ulongint, EAX
	JMP Label33
Label61:
	MOV EAX, 55
	MOV c_ulongint, EAX
	MOV EAX, 3
	MOV i_ulongint, EAX
	CALL gsuma
	FLD result
	FiLD e_ulongint
	FMUL
	fstp result
	printf("%f\n", result)
	MOV EAX, 3
	MOV i_ulongint, EAX
	CALL gsuma
	FLD result
	FiLD e_ulongint
	FMUL
	MOV AUX_ulongint, 55
	FILD AUX_ulongint
	FLD FLOAT0
	FMUL
	FADD
	FSTP b_single
	printf("%f\n", b_single)
	printf("%u\n", e_ulongint)
	JMP Label93
Label89:
	printf("funciono\n")
Label93:
	INVOKE ExitProcess, 0 
END start
