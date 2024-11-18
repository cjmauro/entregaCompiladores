package compilador;

public class token_function extends token {
	private String retorno;
	private token parametro;
	

    public token_function(String nombre, String tipo, int idToken) {
    	super(nombre,tipo,idToken);
    }

    public token_function(String nombre, int idToken) {
    	super(nombre, idToken);
    }

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public token getParametro() {
		return parametro;
	}

	public void setParametro(token parametro) {
		this.parametro = parametro;
	}
    
}
