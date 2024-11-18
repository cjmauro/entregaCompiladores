package compilador;
import java.util.Map;

public class AccionSemantica2 extends accionSemantica {
	// Verifica las palabras reservadas ulongint, for, up, down, pair, IF, THEN, ELSE, BEGIN, END, END_IF, OUTF, TYPEDEF, FUN, RET
    public token ejecutar(String palabra, analizadorLexico al) {
    	
    	token t = new token(palabra, Parser.NUM);
    	
    	char letra = Character.toLowerCase(palabra.charAt(0));
    		
    	if (letra == 'x' || letra == 'y' || letra == 'z') {    	
    		t.setTipo("ulongint");
    	}
    	if(letra == 's') {
    		t.setTipo("single");
    	}
    	
        Map<String, Short> palabrasReservadas = al.getPalabrasReservadas();

        Short tokenValue = palabrasReservadas.get(palabra.toLowerCase());
        if (tokenValue != null) {
        	t.setIdToken(tokenValue);
        } else {
            if (palabra.length() > 15) {
                String palabraTrunc = palabra;
                palabra = palabra.substring(0, 15);
                System.out.println("Linea " + al.getLineaActual() + ": " +
                        "Warning: El identificador " + palabraTrunc + " fue truncado a: " + palabra);
            }
            t.setIdToken(Parser.ID);
        }
        return t;
    }

    @Override
    public String get_palabra(token t) {
    	String palabra = t.getNombre();
        if (palabra.length() > 15) {
            return palabra.substring(0, 15);
        }
        return palabra;
    }
}