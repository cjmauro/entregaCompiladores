package compilador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.crypto.Data;

public class GeneradorDeCodigo {
    private static Parser parser;
    private static final Set<String> ARITHMETIC_OPERATORS = Set.of("+", "-", "*", "/");
    private static final Set<String> COMPARISON_OPERATORS = Set.of(">=", ">", "<=", "<", "==");
    private static final Set<String> JUMP_OPERATORS = Set.of("bf","bi");
    private static Stack<String> registroPila = new Stack<>();
    private static Map<String, String> saltosPila = new HashMap<>();
    private static int i = 0;
    private static Map<String, DataObject> data = new HashMap<>();
    private static Map<String, String> float_values = new HashMap<>();
    private static List<String> instrucciones = new ArrayList<String>();
    private static List<String> declaraciones = new ArrayList<String>();
    private static List<String> errores = new ArrayList<>();
    private static int registroActual = 0;
    private static int num_polaca = 0;
    private static String salto;
    private static String asmString = "";
    private static String comparadorTemporal = "";

    public GeneradorDeCodigo(File file){
        try {
			parser = new Parser(file);
	        parser.yydebug = false;

	        parser.yyparse();
            
            declaraciones.add(".386");
            declaraciones.add(".model flat, stdcall");
            declaraciones.add("option casemap :none");
            declaraciones.add("includelib \\masm32\\lib\\kernel32.lib");
            declaraciones.add("includelib \\masm32\\lib\\user32.lib");
            declaraciones.add("INCLUDE \\masm32\\include\\masm32rt.inc");
            instrucciones.add(".code");

            instrucciones.add( """
                OverflowDetected:
                    invoke MessageBox, 0, addr errorMultiplicacion, addr errorMultiplicacion, MB_ICONERROR
                    INVOKE ExitProcess, 1 
    
                UnderflowDetected:
                    invoke MessageBox, 0, addr errorResta, addr errorResta, MB_ICONERROR
                    INVOKE ExitProcess, 1  

                recursion_error:
                    invoke MessageBox, 0, addr errorRecursion, addr errorRecursion, MB_ICONERROR
                    INVOKE ExitProcess, 1

                """);
    

            data.put("errorMultiplicacion", new DataObject("errorMultiplicacion", "db", "\"Error: Overflow detectado en la multiplicacion.\", 0"));
            data.put("errorRecursion", new DataObject("errorRecursion", "db", "\"Error: Recursion no soportada detectada.\", 0"));
            data.put("errorResta", new DataObject("errorResta", "db", "\"Error: Resultado negativo detectado en la resta.\", 0"));
            data.put("format", new DataObject("format", "db", "\"%d\", 0"));
            data.put("buffer", new DataObject("buffer", "db", "256 dup(?)"));
            data.put("result", new DataObject("result", "REAL8", "?"));
            data.put("AUX_ulongint", new DataObject("AUX_ulongint", "DWORD", "?"));
        
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        generarCodigo();
        System.out.println("///////////////////////Polaca inversa///////////////////////");
        parser.imprimirPolaca();
        System.out.println("///////////////////////Tabla de simbolos///////////////////////");
        parser.imprimirTablaSimbolos();
        if(parser.getErrores().size() > 0){
            System.out.println("///////////////////////Errores///////////////////////");
        }
        imprimirErrores();
    }

    public String getAsmString(){
        if(parser.getErrores().size()> 0){
            return "Errores en el codigo, no fue posible generar el archivo assembler";
        }else{
            imprimirInstrucciones();
            return asmString;
        }
    }

    public static void generarCodigo() {
    	Map<String, List<String>> map = parser.getPolaca();
    	for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<Integer> saltos = new ArrayList<Integer>();
            saltos = parser.getSaltos(entry.getKey());
    		if (!entry.getKey().equals(parser.get_nombre_start())) {         
                instrucciones.add(entry.getKey() + ":");

                instrucciones.add("\tINC recursionFlag"+ entry.getKey());
                instrucciones.add("\tCMP recursionFlag"+ entry.getKey() +", 1");
                data.put("recursionFlag"+ entry.getKey(), new DataObject("recursionFlag"+ entry.getKey(), "DWORD", "0"));
                instrucciones.add("\tJG recursion_error");

    			if (entry.getValue().size() > 1) {
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        String token = entry.getValue().get(i);

                        if(token.equals("=" ) || token.equals("ASSING")){
                            generarCodigoAsignacion();
                        }else if(token.equals("CALL")){
                            token_function tf = (token_function) recuperar_funcion(entry.getValue().get(i+1));
                            if(tf != null){
                                if(tf.getRetorno().equals("single")){
                                    push_registro_pila("result");
                                }else{
                                    push_registro_pila("ECX");
                                }
                            }else{
                                error(token + " no es una funcion");
                            }
                            agregar_instruccion("CALL " + entry.getValue().get(i+1)); 
                            i++;
                        }else if (token.equals("retorno")) {
                            generarRetorno(token, entry.getKey());

                        }else if(esOperador(token)){
                            generarCodigoOperacion(token);
                        }else if(escomparador(token)){
                            generarCodigoComparacion(token);
                        } else if (esSalto(token, entry.getValue())) {
                            generarCodigoSaltos(token);
                        }else if (token.equals("OUTF")) {
                            generarCodigoImpresion(token);
                        } else {
                           push_registro_pila(token);
                        }
                        if(saltos.contains(i)){
                            instrucciones.add("Label" + Integer.toString(i) + ":");
                        }

                    }
                }
                num_polaca = 0;
            }
        }

        List<String> polaca = parser.getPolaca().get(parser.get_nombre_start());
        instrucciones.add("start:");
        List<Integer> saltos = parser.getSaltos(parser.get_nombre_start());
        for (int i = 0; i < polaca.size(); i++) { 
            String token = polaca.get(i);   
            if(token.equals("=" ) || token.equals("ASSING")){
                generarCodigoAsignacion();
            }else if(token.equals("CALL")){
                token_function tf = (token_function) recuperar_funcion(polaca.get(i+1));
                if(tf != null){
                    if(tf.getRetorno().equals("single")){
                        push_registro_pila("result");
                    }else{
                        push_registro_pila("ECX");
                    }
                }else{
                    error(token + " no es una funcion");
                }
                agregar_instruccion("CALL " + polaca.get(i+1)); 
                i++;
            }else if(esOperador(token)){
                generarCodigoOperacion(token);
            }else if(escomparador(token)){
                generarCodigoComparacion(token);
            }else if(esSalto(token, polaca)){
                generarCodigoSaltos(token);
            }else if (token.equals("OUTF")) {

                generarCodigoImpresion(token);

            }else{
               push_registro_pila(token);
            }

            if(saltos.contains(i)){
                if (polaca.get(i+1).equals("bi")) {
                    saltos.add(i+1);
                }else{
                    if(polaca.get(i).equals("bi")){
                        instrucciones.add("Label" + Integer.toString(i-1) + ":");
                    }else{

                        instrucciones.add("Label" + Integer.toString(i) + ":");
                    }
                }
            }
            num_polaca++;
        }

        for (Map.Entry<String, String> entry : saltosPila.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (Integer.parseInt(key) >= num_polaca) {
                instrucciones.add(value + ":");
            }
        }

        agregar_instruccion("INVOKE ExitProcess, 0 ");
        instrucciones.add("END start");

       
    }


    public void imprimirInstrucciones(){

        
        StringBuilder asmContent = new StringBuilder();


        for (String declaracion : declaraciones) {
            asmContent.append(declaracion).append("\n");
        }

        asmContent.append(".data\n");
        for (Map.Entry<String, DataObject> entry : data.entrySet()) {

            if( entry.getValue().getSubdata().size() > 0) {
                asmContent.append(entry.getKey()).append(" STRUCT\n");
                for (DataObject subdata : entry.getValue().getSubdata()) {
                    asmContent.append("\t").append(subdata.getName())
                        .append(" ").append(subdata.getType())
                        .append(" ").append(subdata.getValue())
                        .append("\n");
                }
                asmContent.append(entry.getKey()).append(" ENDS\n");
            }
        }

        for(Map.Entry<String, String> entry : float_values.entrySet()){
            if(entry.getValue().contains("FLOAT")){
                asmContent.append("\t").append(entry.getValue()).append(" REAL8 " + entry.getKey() +"\n");
            }else{
                asmContent.append("\t").append(entry.getValue()).append(" DWORD ").append(entry.getKey().replace("ulongint", "") +  "\n");
            }
        }

        for (Map.Entry<String, DataObject> entry : data.entrySet()) {

            if( entry.getValue().getSubdata().size() <= 0) {
                asmContent.append("\t").append(entry.getKey())
                    .append(" ").append(entry.getValue().getType())
                    .append(" ").append(entry.getValue().getValue())
                    .append("\n");
            }
        }

        for (String instruccion : instrucciones) {
            asmContent.append(instruccion).append("\n");
        }

        asmString = asmContent.toString();
    }

    public static void generarCodigoImpresion(String token){
        String reg = registroPila.peek();
        String registro = recuperar_registro();
        Map<String, token> tablaSimbolos = parser.getTablaSimbolos();

        if(tablaSimbolos.get(reg) != null){
            if(tablaSimbolos.get(reg).getTipo().equals("single")){
                reg = reg + "_single";
                registro = registro + "_single";
            }else if(tablaSimbolos.get(reg).getTipo().equals("ulongint")){
                reg = reg + "_ulongint";
                registro = registro + "_ulongint";
            }
        }

        if(tablaSimbolos.get(reg) != null){
            if(tablaSimbolos.get(reg).getTipo().equals("multiline")){
                agregar_instruccion("printf(\"" + registro + "\\n\")");
                return;
            }
        }

        if(data.get(registro) != null || float_values.get(reg) != null){
            if(float_values.get(reg) != null || data.get(registro).getType().equals("REAL8") ){
                agregar_instruccion("printf(\"%f\\n\", " + registro + ")"); 
            }else{
                agregar_instruccion("printf(\"%u\\n\", " + registro + ")");
            }
        }
    }

    public static void generarCodigoAsignacion(){
        String reg2 = registroPila.peek();
        String registro2 = recuperar_registro();
        String reg1 = registroPila.peek();
        String registro1 = recuperar_registro();

        Map<String, token> tablaSimbolos = parser.getTablaSimbolos();

        if(!reg1.contains("_ulongint") && !reg1.contains("_single")) {
            if(tablaSimbolos.get(reg1) != null){
                if(tablaSimbolos.get(reg1).getTipo().equals("single")){
                    reg1 = reg1 + "_single";
                    registro1 = registro1 + "_single";
                }else if(tablaSimbolos.get(reg1).getTipo().equals("ulongint")){
                    reg1 = reg1 + "_ulongint";
                    registro1 = registro1 + "_ulongint";
                }
            }
        }

        if(registro2.equals("FLAG_fstp")){
            if (data.get(registro1).getType().equals("REAL8")) {
                agregar_instruccion("FSTP " + registro1);
                return;
            }else{
                error("Incompatibilidad de tipos entre " + registro1);
                return;
            }
        }

        String reg = registro2;
        if(tablaSimbolos.get(reg2) != null)
            reg = reg2;

        Boolean checkregistro = false;
        if(!reg2.equals("ECX") && !reg2.equals("EAX") && !reg2.equals("EBX") && !reg2.equals("EDX") && !reg2.matches("\\d+")){
            checkregistro = true;
        }
        if(devolverTipo(reg1).equals("single") || registro1.contains("_single")){
            if(devolverTipo(reg2).equals("ulongint") || registro2.contains("_ulongint")){
                if(float_values.get(registro2) != null){
                    registro2 = float_values.get(registro2);
                }else{
                    float_values.put(registro2, "ulongint" + registro2);
                    registro2 = "ulongint" + registro2;
                }
            }

            if (data.get(registro1).getType().equals("REAL8")) {

                if (!checkregistro){
                    agregar_instruccion("MOV EDX, " + registro2);
                    agregar_instruccion("MOV AUX_ulongint, EDX");
                    agregar_instruccion("FILD AUX_ulongint");
                }else{
                    agregar_instruccion("FLD " + registro2);
                }
                agregar_instruccion("FSTP " + registro1);

                return;
            }else{
                error("Incompatibilidad de tipos entre " + registro1 + " y " + registro2);
                return;
            }
        }
        if(data.containsKey(registro1)){
            if(!registro1.equals(registro2)){
                agregar_instruccion("MOV EAX, " + registro2);
            }
            agregar_instruccion("MOV " + registro1 + ", EAX");
        }else{                            
            agregar_instruccion("MOV " + registro1 + ", " + registro2);
        }
    }

    public static void agregar_instruccion(String s){
        instrucciones.add("\t" + s);
    }

    public static String devolverTipo(String reg){
        Map<String, token> tablaSimbolos = parser.getTablaSimbolos();
        

        String cadena = reg;
        while (cadena.contains(":")) { 
            if(tablaSimbolos.get(cadena) != null){
                return tablaSimbolos.get(cadena).getTipo();
            }
            cadena = cadena.substring(0, cadena.lastIndexOf(":")); 
        }
        
        if (reg.endsWith("primero") || reg.endsWith("segundo")) {
            String nuevoReg = reg.replaceAll("(_ulongint|_single)(?=primero|segundo$)", "");
            
            String tiporaiz = tablaSimbolos.get(nuevoReg.substring(0, nuevoReg.length() - 7)).getTipo();
            return tablaSimbolos.get(tiporaiz).getTipo();
        }


        if(data.get(reg) != null){
            return data.get(reg).getType();
        }

        
        return "";
    }

    public static void push_registro_pila(String variable){
        try{
            String adicional = "";
            if(variable.contains("|POSITION")){
                adicional = variable.split("\\|")[1];
                    switch (adicional) {
                        case "POSITION1":
                            adicional = "primero";
                            break;
                        case "POSITION2":
                            adicional = "segundo";
                            break;
                        default:
                            adicional = "";
                            break;
                    }
            }
            
            if (variable.contains(":")) {
                
                String token = variable.split(":")[0];
                Map<String, token> tablaSimbolos = parser.getTablaSimbolos();

                if (data.get(variable) == null) {
                    String value = token;
                    if(tablaSimbolos.get(token).getTipo() == null){
                        value = variable;
                    }
                    if(devolverTipo(variable).equals("ulongint")){ 
                        cargar_data(token, new DataObject(token, "DWORD", "?"), variable);
                    }else if(devolverTipo(variable).equals("single")){
                        cargar_data(token, new DataObject(token, "REAL8", "?"), variable);
                    }else if(devolverTipo(variable).equals("octal")) {
                        cargar_data(token, new DataObject(token, "DWORD", "?"), variable);
                    }
                    else{
                        String tipo = tablaSimbolos.get(tablaSimbolos.get(token).getTipo()).getTipo();
                        if(tipo.equals("ulongint")){
                            tipo = "DWORD";
                            token = token + "_ulongint";
                        }else if(tipo.equals("single")){
                            tipo = "REAL8";
                            token = token + "_single";
                        }
                        cargar_data(token + adicional, new DataObject(token + adicional, tipo , "?"), variable);
                    }
                    
                }


                String tipo = "";
                if(tablaSimbolos.get(variable) != null){
                    if(tablaSimbolos.get(variable).getTipo().equals("single")){
                        tipo =  "_single";
                    }else if(tablaSimbolos.get(variable).getTipo().equals("ulongint")){
                        tipo = "_ulongint";
                    }
                }


                registroPila.push(token+ tipo + adicional);
            }else{
                registroPila.push(variable + adicional);

            }
        } catch (Exception  e) {
            errores.add("Error: Ocurrio un error inesperado, revise la sintaxis del codigo");
        }
    }

    public static void generarRetorno(String token, String nombreFuncion){

        token_function tf = (token_function) recuperar_funcion(nombreFuncion);

        if(tf != null){
            if(tf.getRetorno().equals("single")){
                String registro = recuperar_registro();

                if(!registro.equals("FLAG_fstp")){

                    if(tf.getParametro().getTipo().equals("single")){
                        agregar_instruccion("FLD " + registro);
                    }else{
                        agregar_instruccion("MOV EDX, " + registro);
                        agregar_instruccion("MOV AUX_ulongint, EDX");
                        agregar_instruccion("FILD AUX_ulongint");
                    }
                }
                    agregar_instruccion("FSTP result");
                
            }else{
                String registro = recuperar_registro();

                if(registro.equals("FLAG_fstp")){
                    agregar_instruccion("FSTP result");
                }else{
                    agregar_instruccion("MOV ECX, " + registro);
                }
            }
        }else{
            error(token + " no es una funcion");
        }
        agregar_instruccion("DEC recursionFlag" + nombreFuncion);
        agregar_instruccion("ret");
    }

    public static boolean escomparador(String token){
        return COMPARISON_OPERATORS.contains(token);
    }

    public static boolean esSalto(String token, List<String> polaca){
        return JUMP_OPERATORS.contains(token);
    }

    public static token_function recuperar_funcion(String nombre) {
        for (Map.Entry<String, token> entry : parser.getTablaSimbolos().entrySet()) {
            if (entry.getValue().getNombre().equals(nombre) && entry.getValue() instanceof token_function) {
                token value = entry.getValue();
                return (token_function) value;
       
            }
        }
        return null;
    }
    
    public static void generarCodigoSaltos(String token){
        String operando1 = registroPila.pop();

        Integer i = Integer.parseInt(operando1);
        if (i > num_polaca) {
            if (token.equals("bf") ) {
                agregar_instruccion(comparadorTemporal + " Label" + operando1);
                saltosPila.put(operando1,"Label" + operando1); 
            }else if(token.equals("bi")){
                agregar_instruccion("JMP " + "Label" + operando1);
                saltosPila.put(operando1, "Label" + operando1);
            }
        }else{ // for

            if (token.equals("bf") ) {
                agregar_instruccion(comparadorTemporal + " Label" + operando1);
                saltosPila.put(operando1,"Label" + operando1); 
            }else if(token.equals("bi")){
                agregar_instruccion("JMP " + "Label" + operando1);
                saltosPila.put(operando1, "Label" + operando1);
            }
        }
    }

    private static void agregarEtiquetaInicio(Integer i) {
        for (int j = instrucciones.size() - 1; j >= 0; j--) {
            if (instrucciones.get(j).trim().startsWith("MOV i, EAX") &&
                j + 1 < instrucciones.size() &&
                instrucciones.get(j + 1).trim().startsWith("CMP i,")) {

                System.out.println("Encontrado inicio de for, agregando etiqueta");
                String etiqueta = "LabelInicio" + i;
                instrucciones.add(j + 1, etiqueta + ":");
                break;
            }
        }
    }
    
    private static void generarSaltoIncondicional() {
        for (int j = instrucciones.size() - 1; j >= 0; j--) {
            if (instrucciones.get(j).trim().startsWith("LabelInicio")) {
                String etiqueta = instrucciones.get(j).trim().replace(":", "");
                agregar_instruccion("JMP " + etiqueta);
                break;
            }
        }
    }

    public static String recuperar_registro(){
        String reg = registroPila.pop();
        if(reg.contains(".")){
            if(float_values.get(reg) != null){
                reg = float_values.get(reg);
            }else{
                float_values.put(reg, "FLOAT" + float_values.size());
                reg = "FLOAT" + (float_values.size() - 1);
            }
        }
        return reg;
    }

    public static void insertar_label(int i, String token, List<String> polaca){
       String a = polaca.get(i);
    }

    public static void generarCodigoComparacion(String token){
        String reg2 = registroPila.peek();
        String registro2 = recuperar_registro();
        String reg1 = registroPila.peek();
        String registro1 = recuperar_registro();

        Map<String, token> tablaSimbolos = parser.getTablaSimbolos();

        if(devolverTipo(reg1).equals("single") || devolverTipo(reg2).equals("single") || registro1.contains("_single") || registro2.contains("_single")){

            agregar_instruccion("FINIT");
            if(devolverTipo(reg1).equals("ulongint") || reg1.matches("\\d+")){
                agregar_instruccion("MOV EDX, " + registro1);
                agregar_instruccion("MOV AUX_ulongint, EDX");
                agregar_instruccion("FILD AUX_ulongint");
            }else{
                agregar_instruccion("FLD " + registro1);

            }
            if(devolverTipo(reg2).equals("ulongint") || reg2.matches("\\d+")){
                agregar_instruccion("MOV EDX, " + registro2);
                agregar_instruccion("MOV AUX_ulongint, EDX");
                agregar_instruccion("FILD AUX_ulongint");
            }else{
                agregar_instruccion("FLD " + registro2);
            }
            agregar_instruccion("FCOMP st(1)");
            agregar_instruccion("FSTSW AX");
            agregar_instruccion("SAHF");

        }else{
            agregar_instruccion("MOV EAX, " + registro1);
            agregar_instruccion("CMP EAX, " + registro2);
        }
        switch (token) {//quedaron al reves por como se carga el mov
            case ">=":
                comparadorTemporal =  "JLE";
                break;
            case ">":
                comparadorTemporal = "JL";
                break;
            case "<=":
                comparadorTemporal = "JGE";
                break;
            case "<":
                comparadorTemporal = "JG"; 
                break;
            case "==":
                comparadorTemporal = "JE";
                break;
            default:
                throw new IllegalArgumentException("Operador de comparación no soportado: " + token);
        }
        
    }

    private static boolean esOperador(String token) {
        return ARITHMETIC_OPERATORS.contains(token);
    }

    public static String validar_tipo(String registro, String reg){
        String tipo = "";
        if(parser.getTablaSimbolos().get(registro) != null){
            tipo= parser.getTablaSimbolos().get(registro).getTipo();
        }
        if(data.get(registro) != null){
            tipo= data.get(registro).getType();
        }
        if(float_values.get(reg) != null){
            tipo= "REAL8";
        }

        if(reg.equals("EBX") || reg.equals("EAX") || reg.equals("ECX") || reg.equals("EDX")){
            tipo = "DWORD";
        }

        if (tipo.equals(""))
            error("No se encontro el tipo de " + reg);
        else{

            if (tipo.equals("ulongint")) {
                tipo = "DWORD";
            } else if (tipo.equals("single")) {
                tipo = "REAL8";
            }
        }
        return tipo;
    }

    private static void generarCodigoOperacion(String operador) {
        String reg2 = registroPila.peek();
        String registro2 = recuperar_registro();
        String reg1 = registroPila.peek();
        String registro1 = recuperar_registro();
    
        if (operador.equals("/")) {
            agregar_instruccion("CMP " + registro2 + ", 0");
            agregar_instruccion("JE DIVIDE_BY_ZERO");
        }
            
        String tipo1 = "";
        String tipo2 = "";
        if (data.get(registro1) == null || data.get(registro2) == null) {
            for(Map.Entry<String, DataObject> entry : data.entrySet()) {
                if (entry.getValue().getSubdata().size() > 0) {
                    for (DataObject subdata : entry.getValue().getSubdata()) {
                        String[] partes1 = registro1.split("\\.");
                        String[] partes2 = registro2.split("\\.");

                        if (partes1.length > 1 && subdata.getName().equals(partes1[1])) {
                            tipo1 = subdata.getType();
                            break;
                        }else if (partes2.length > 1 && entry.getKey().equals(partes2[1])) {
                            tipo2 = subdata.getType();
                            break;
                        }
                    }
                }
            }
        }

        if (tipo2.equals("")){
            tipo2 = validar_tipo(registro2, reg2);
        }

        if (tipo1.equals("")){
            tipo1 = validar_tipo(registro1, reg1);
        }


        //conversiones implicitas
        if(tipo2.equals("REAL8") || tipo1.equals("REAL8")){

            if (registro1.matches("\\d+")) {
                if(float_values.get(reg1) != null){
                    registro1 = float_values.get(reg1);
                }else{
                    float_values.put(reg1, "ulongint" + float_values.size());
                    registro1 = "ulongint" + (float_values.size() - 1);
                }
            }      

            if (registro2.matches("\\d+")) {
                if(float_values.get(reg2) != null){
                    registro2 = float_values.get(reg2);
                }else{
                    float_values.put(reg2, "ulongint" + float_values.size());
                    registro2 = "ulongint" + (float_values.size() - 1);
                }
            }

            
            if(tipo2.equals("ulongint")){
                String tempRegistro = registro1;
                registro1 = registro2;
                registro2 = tempRegistro;
            
                String tempReg = reg1;
                reg1 = reg2;
                reg2 = tempReg;
            }

            agregar_instruccion("FILD " + registro1);

            switch (operador) {
                case "+":
                    agregar_instruccion("FADD " + registro2);
                    break;
                case "-":
                    agregar_instruccion("FSUB " + registro2);
                    break;
                case "*":
                    agregar_instruccion("FMUL " + registro2);
                    break;
                case "/":
                    agregar_instruccion("FDIV " + registro2);
                    
                    break;
                default:
                    throw new IllegalArgumentException("Operador aritmético no soportado: " + operador);
            }
               registroPila.push("FLAG_fstp");

        }else{
            agregar_instruccion("MOV EBX, " + registro1);
         
            switch (operador) {
                case "+":
                    agregar_instruccion("ADD EBX, " + registro2);
                    registroPila.push("EBX");
                    break;
                case "-":
                    agregar_instruccion("SUB EBX, " + registro2);
                    registroPila.push("EBX");
                    agregar_instruccion("JC UnderflowDetected");
                    break;
                case "*":
                    agregar_instruccion("IMUL EBX, " + registro2);
                    registroPila.push("EBX");
                    agregar_instruccion("JO OverflowDetected");
                    break;
                case "/":
                    agregar_instruccion("IDIV " + registro2);
                    registroPila.push("EBX");

                    break;
                default:
                    throw new IllegalArgumentException("Operador aritmético no soportado: " + operador);
            }
        }
    
    }

    public static void cargar_data(String registro, DataObject object, String polaca){

        Map<String, token> tablaSimbolos = parser.getTablaSimbolos();
        String aux = registro;
        if(tablaSimbolos.get(polaca) != null){
            if(tablaSimbolos.get(polaca).getTipo().equals("single")){
                aux = registro + "_single";
            }else if(tablaSimbolos.get(polaca).getTipo().equals("ulongint")){
                aux = registro + "_ulongint";
            }
        }


        if(data.get(aux) == null){
            data.put(aux, object);
        }
    }
    
    public static void error(String razon){
        parser.getErrores().add("Error " + razon);
    }

    public void imprimirErrores(){
        for (String error : parser.getErrores()) {
            System.out.println(error);
        }
    }
}