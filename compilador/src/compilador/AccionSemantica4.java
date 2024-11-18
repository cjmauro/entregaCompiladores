package compilador;

public class AccionSemantica4  extends accionSemantica {
	//informa el error
    public token ejecutar(String palabra, analizadorLexico al) {	
    	al.agregarError(palabra);
    	
    	return null;
    }

}
