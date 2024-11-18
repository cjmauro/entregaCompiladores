.386
.model flat, stdcall
option casemap :none
includelib \masm32\lib\kernel32.lib
includelib \masm32\lib\user32.lib
INCLUDE \masm32\include\masm32rt.inc
.data
	a REAL8 ?
	i_ulongint DWORD ?
	p2_ulongint DWORD ?
	p1_ulongintprimero DWORD ?
	format db "%d", 0
	p1_ulongint DWORD ?
	errorResta db "Error: Resultado negativo detectado en la resta.", 0
	p1_ulongintsegundo DWORD ?
	result REAL8 ?
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


gsuma:
	INC recursionFlaggsuma
	CMP recursionFlaggsuma, 1
	JG recursion_error
	MOV EAX, 5
	MOV i_ulongint, EAX
	CALL gsuma
	FLD result
	FSTP a_single
	FILD i_ulongint
	FMUL a_single
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
	MOV EDX, EBX
	MOV AUX_ulongint, EDX
	FILD AUX_ulongint
	FSTP a_single
	printf("%f\n", a_single)
	MOV EDX, 5
	MOV AUX_ulongint, EDX
	FILD AUX_ulongint
	FSTP b_single
	MOV EAX, 1
	CMP EAX, p1_ulongintprimero
	JG Label37
	MOV EAX, 3
	MOV i_ulongint, EAX
	CALL gsuma
	FILD p1_ulongintsegundo
	FADD result
	FSTP a_single
	printf("%f\n", a_single)
	JMP Label41
Label37:
	printf("funciono\n")
Label41:
	printf("%f\n", a_single)
	INVOKE ExitProcess, 0 
END start
