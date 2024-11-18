package compilador;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccionSemantica5   extends accionSemantica {
	String resultado;
    @Override
    public token ejecutar(String palabra, analizadorLexico al) {
    	resultado = palabra; 
       
        String palabraSinSaltos = palabra.replaceAll("\\r?\\n", " ");

        String patron = "\\[([^\\]]*)\\]";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(palabraSinSaltos);

        if (matcher.matches()) {
            resultado = matcher.group(1);
            
            token t  = new token(resultado,Parser.MULTILINE);
            t.setTipo("multiline");
            return  t;
        } else {
            return null; 
        }
    }
    
    @Override
    public String get_palabra(token t) {
    	return resultado;
    }
}
