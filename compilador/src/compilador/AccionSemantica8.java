package compilador;

public class AccionSemantica8 extends accionSemantica {
    //accion semantica para retornar tokens resueltos en la matriz de transicion de estados, como octales,y numeros con .
    int token;
    String tipo;

    public AccionSemantica8(int token, String tipo) {
        this.token = token;
        this.tipo = tipo;
    }

    @Override
    public token ejecutar(String palabra, analizadorLexico al) {
    		
            return new token(palabra, tipo, token);
        }
}