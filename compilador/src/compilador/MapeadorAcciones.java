package compilador;

import java.util.HashMap;
import java.util.Map;

public class MapeadorAcciones {
    private Map<Integer, accionSemantica> mapaAcciones;

    public MapeadorAcciones() {
        mapaAcciones = new HashMap<>();
        

        mapaAcciones.put(1, new AccionSemantica1());
        mapaAcciones.put(2, new AccionSemantica2()); 
        mapaAcciones.put(3, new AccionSemantica3()); 
        mapaAcciones.put(4, new AccionSemantica4()); 
        mapaAcciones.put(5, new AccionSemantica5()); 
        mapaAcciones.put(6, new AccionSemantica6()); 
        mapaAcciones.put(7, new AccionSemantica7()); 
        mapaAcciones.put(8, new AccionSemantica8(Parser.OCTAL, "octal"));//octales
        mapaAcciones.put(9, new AccionSemantica8(Parser.SINGLE, "single"));//numeros con .

    }

    public accionSemantica getAccion(int valor) {
        return mapaAcciones.getOrDefault(valor, null);  // Retorna null si no existe una acci�n asociada
    }
}
