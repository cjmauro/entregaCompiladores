package compilador;

public class AccionSemantica1 extends accionSemantica { 
    // Verifica que el int no sobrepase 2^32 - 1
	 @Override
	    public token ejecutar(String palabra, analizadorLexico al) {
	        try {
	            long numero = Long.parseLong(palabra);
	            
	            long minValor = (long) -Math.pow(2, 31);
	            long maxValor = (long) Math.pow(2, 31) - 1;

	            if (numero >= minValor && numero <= maxValor) {
	            	token t = new token(palabra, Parser.NUM);
	            	t.setTipo("ULONGINT");
	            	return t;
	            } else {
	                return null; 
	            }
	        } catch (NumberFormatException e) {
	            return null;
	        }
	    }
}
