package compilador;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap;
import java.util.ArrayList;

public class analizadorLexico {
    int LineaActual;
    Map<String, Short> palabrasTokens;
    List<String> errores = new ArrayList<>();
    int filaActual = 0;
     Map<String, token> tablaSimbolos = new HashMap<>();
    int nroDeConstante = 0 ;
    asignarColumna ac;
    PushbackReader reader;
    CSVaMatriz estados;
    CSVaMatrizConAcciones csvAcciones;

    public analizadorLexico(Map<String, Short> p_tokens, File file) throws IOException {
    	LineaActual = 0;
        reader = new PushbackReader(new FileReader(file), 2);
        ac = new asignarColumna();
        estados = new CSVaMatriz("matriz.csv");
        csvAcciones = new CSVaMatrizConAcciones("acciones.csv");
        palabrasTokens = p_tokens;
    }

	public ParserVal leerToken() throws IOException {
        StringBuffer sb = new StringBuffer("");
        int caracter;
        boolean finArchivo = false;

        while (!finArchivo) {
            caracter = reader.read();
            if (caracter == -1) {
                finArchivo = true;
                if (sb.length() > 0) {
                    return procesarUltimoToken(sb, ac, estados, csvAcciones);
                }
                break;
            }

            char ch = (char) caracter;

            int columna = ac.detectar_columna(ch);
            List<accionSemantica> acciones = csvAcciones.get(filaActual, columna);
            token token = null;
            String palabra = "";
 
            if (acciones != null) {
                for (accionSemantica accion : acciones) {
                    if (accion instanceof AccionSemantica3) {
                        token = accion.ejecutar(String.valueOf(ch), this);
                    } else {
                        token = accion.ejecutar(sb.toString(), this);
                    }
                    if(token != null)
                    	palabra = accion.get_palabra(token);
                }
            }

            filaActual = estados.get(filaActual, columna);
            if (filaActual != 0) {
                sb.append(ch);
                if (filaActual == -1) {

                    sb.deleteCharAt(sb.length() - 1);
                    String resultado;
                    if (token != null) {
                    	resultado = palabra; //token.getIdToken() + " " + palabra; iba antes asi
                    }else {
                    	resultado = palabra;
                    }
                    filaActual = 0;  
                    reader.unread(caracter);
                    if(token != null) {
               
                    	ParserVal token_parser = new ParserVal(token);
                    	boolean existe = false;
                        for (token t : tablaSimbolos.values()) {
                            if (t.getKey().equals(resultado)) {
                            	existe = true;
                                break; 
                            }
                        }
                        if (!existe) {
                            tablaSimbolos.put(resultado, token);
                        }
                    	return token_parser; 
                    }
                    sb = new StringBuffer("");
                }
                // Estado de error (-2)
                if (filaActual == -2) {
                    agregarError("Secuencia inválida: " + sb.toString());
                    sb = new StringBuffer(""); 
                    filaActual = 0;  
                    continue;  
                }
            }
        }
        return null;
    }

    private ParserVal procesarUltimoToken(StringBuffer sb, asignarColumna ac, CSVaMatriz estados, CSVaMatrizConAcciones csvAcciones) {
        if (sb.length() > 0) {
            int columna = 21;
            List<accionSemantica> acciones = csvAcciones.get(filaActual, columna);
            token token = null;
            String palabra = "";

            if (acciones != null) {
                for (accionSemantica accion : acciones) {
                    token = accion.ejecutar(sb.toString(), this);
                    if(token != null) {
	                    palabra = accion.get_palabra(token);
	                    token.SetNombre(palabra);
                    }
                }
            }
            if(token != null) {
	            String resultado = token + " " + palabra;
	            tablaSimbolos.put(resultado, token);
	        	ParserVal token_parser = new ParserVal(token);
	        	return token_parser; 
            }
        }
        return null;
    }

    public int getLineaActual() {
        return LineaActual+1;
    }

    public void setLineaActual() {
        LineaActual += 1;
    }

    public Map<String, Short> getPalabrasReservadas() {
        return palabrasTokens;
    }

    public void agregarError(String error) {
        System.out.println("Error: Linea " + filaActual + " " + error);
        errores.add("Error: Linea " + LineaActual + " " + error);
    }

    public void setFilaActual(int valor) {
        filaActual = valor;
    }
    
    public Map<String, token> getTablaSimbolos(){
    	return tablaSimbolos;
    }
    
    
    public void removerTablaSimbolos(String a){
    	tablaSimbolos.remove(a);
    }
    
    public boolean existsSimbolo(String key){
        return tablaSimbolos.containsKey(key);
    }


    public List<String> getErrores() {
        return errores;
    }

    public void addSimbolo(String key, token atributos){
        tablaSimbolos.put(key, atributos);
    }

    public void deleteSimbolo(String key){
        tablaSimbolos.remove(key);
    }
}