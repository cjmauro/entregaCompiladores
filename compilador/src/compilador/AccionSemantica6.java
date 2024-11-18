package compilador;


public class AccionSemantica6 extends accionSemantica {
	String resultado;
    @Override
    public token ejecutar(String palabra, analizadorLexico al) {
    	resultado = palabra;
    	
        if (palabra.startsWith("##")) {

            resultado = palabra.substring(2);

        }
        return null;
    }
    
    @Override
    public String get_palabra(token t) {
    	return resultado;
    }
  
}
