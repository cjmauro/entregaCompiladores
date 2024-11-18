package compilador;

public abstract class accionSemantica {
    public abstract token ejecutar(String palabra, analizadorLexico al);
    
    public String get_palabra(token t) {
    	return t.getNombre();
    }
}
