package compilador;

public class token {
	private int idToken;
    private String nombre;
    private String tipo; 
    private String uso; // CONSULTAR
    

    public token(String nombre, String tipo, int idToken) {
    	this.idToken = idToken;
        this.nombre = nombre;
        this.tipo = tipo;

    }

    public token(String nombre, int idToken) {
    	this.idToken = idToken;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void SetNombre(String n) {
        this.nombre = n;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
    	this.tipo = tipo;
    }



    @Override
    public String toString() {
        return "Token{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +

                '}';
    }

	public int getIdToken() {
		return idToken;
	}

	public int getValue() {
		return idToken;
	}
		
	public String getKey() {
		return nombre;
	}
	
	public void setIdToken(int idToken) {
		this.idToken = idToken;
	}

	public String getUso() {
		return uso;
	}

	public void setUso(String uso) {
		this.uso = uso;
	}
	
    public token getCopia() {
        token copia = new token(this.nombre, this.tipo, this.idToken);
        copia.uso = this.uso;
        return copia;
    }
}
