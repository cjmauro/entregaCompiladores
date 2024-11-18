package compilador;

public class AccionSemantica3 extends accionSemantica {
    // detecta si la palabra a analizar es un salto de línea, de ser así incrementa el contador de líneas de código
    public token ejecutar(String palabra, analizadorLexico al) {    
       
        if (palabra.equals("\n")) {
            al.setLineaActual();
        }
        return null; // no es un token así que devolvemos 0
    }
}

