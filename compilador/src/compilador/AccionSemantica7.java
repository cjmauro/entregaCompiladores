package compilador;

public class AccionSemantica7  extends accionSemantica { // esto mitiga los saltos de lineas tabs y espacios en blanco en palabras
	String resultado;
    @Override
    public token ejecutar(String palabra, analizadorLexico al) {
        	return null; //error
    }
    
    @Override
    public String get_palabra(token t) {
    	return "";
    }
}