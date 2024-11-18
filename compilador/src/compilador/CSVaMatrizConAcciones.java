package compilador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVaMatrizConAcciones {
	 private List<Integer>[][] matrix;
	 private MapeadorAcciones mapeador;
	 
	 
	 
	 
	    // Constructor que carga el CSV
	    @SuppressWarnings("unchecked")
	    public CSVaMatrizConAcciones(String csvFile) throws IOException {
	    	mapeador = new MapeadorAcciones();
	        List<List<Integer>[]> rows = new ArrayList<>();
	        String line;
	        int numCols = -1;

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	            // Leer la primera l�nea y descartarla (cabeceras)
	            br.readLine();

	            // Leer el resto de las filas
	            while ((line = br.readLine()) != null) {
	                String[] values = line.split(",");
	                if (numCols == -1) {
	                    numCols = values.length - 1; // Excluir la primera columna
	                }
	                if (values.length > 1) {
	                    // Crear una fila de listas de enteros
	                    List<Integer>[] row = new List[numCols];
	                    for (int i = 1; i < values.length; i++) { // Empezar desde el �ndice 1
	                        row[i - 1] = parseListValue(values[i].trim());
	                    }
	                    rows.add(row);
	                }
	            }

	            // Convertir la lista a una matriz
	            matrix = new List[rows.size()][numCols];
				
	            for (int i = 0; i < rows.size(); i++) {
					
	                matrix[i] = rows.get(i);
					
	            }

				
	        }
	    }

	    // M�todo para convertir una cadena a una lista de enteros
	    private List<Integer> parseListValue(String value) {
	        List<Integer> result = new ArrayList<>();
	        String[] parts = value.split("_");
	        for (String part : parts) {
	            try {
	                result.add(Integer.parseInt(part));
	            } catch (NumberFormatException e) {
	                // Manejar valores inv�lidos, por ejemplo, ignorarlos o agregar un valor por defecto
	                result.add(-1); // O cualquier otro valor por defecto
	            }
	        }
	        return result;
	    }

	    public List<accionSemantica> get(int row, int col) {
	        List<accionSemantica> a = new ArrayList<>();
	        if (row >= 0 && row < matrix.length && col >= 0 && col < matrix[row].length) {
	            for (int i : matrix[row][col]) {
	                accionSemantica accion = mapeador.getAccion(i);
	                if (accion != null) {
					
	                    a.add(accion);
	                }
	            }
	            return a;
	        } else {
	            return null; // Retornar null si est� fuera de los l�mites
	        }
	    }

	    // M�todo para establecer una lista de enteros en una posici�n espec�fica
	    public void set(int row, int col, List<Integer> value) {
	        if (row >= 0 && row < matrix.length && col >= 0 && col < matrix[row].length) {
	            matrix[row][col] = value;
	        } else {
	            throw new IndexOutOfBoundsException("�ndice fuera de los l�mites de la matriz.");
	        }
	    }

	    // M�todo para obtener el n�mero de filas
	    public int getRowCount() {
	        return matrix.length;
	    }

	    // M�todo para obtener el n�mero de columnas
	    public int getColumnCount() {
	        return matrix.length > 0 ? matrix[0].length : 0;
	    }

	    // M�todo para imprimir la matriz (opcional)
	    public void printMatrix() {
	        for (List<Integer>[] row : matrix) {
	            for (List<Integer> cell : row) {
	                System.out.print(cell + " ");
	            }
	            System.out.println();
	        }
	    }

	    public List<Integer>[][] getMatrix() {
	        return matrix;
	    }
	}