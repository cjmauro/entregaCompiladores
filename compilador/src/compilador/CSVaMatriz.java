package compilador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVaMatriz {
    private int[][] matrix;

    
    public CSVaMatriz(String csvFile) throws IOException {
        List<int[]> rows = new ArrayList<>();
        String line;
        int numCols = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (numCols == -1) {
                    numCols = values.length - 1;
                }
                if (values.length > 1) {
                    int[] row = new int[numCols];
                    for (int i = 1; i < values.length; i++) { 
                        row[i - 1] = parseIntValue(values[i].trim());
                    }
                    rows.add(row);
                }
            }

            matrix = new int[rows.size()][numCols];
            for (int i = 0; i < rows.size(); i++) {
                matrix[i] = rows.get(i);
            }
        }
    }

    private int parseIntValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; 
        }
    }

    
    public int get(int row, int col) {
        if (row >= 0 && row < matrix.length && col >= 0 && col < matrix[row].length) {
            return matrix[row][col];
        } else {
            return -2;
        }
    }
    public void set(int row, int col, int value) {
        if (row >= 0 && row < matrix.length && col >= 0 && col < matrix[row].length) {
            matrix[row][col] = value;
        } else {
            throw new IndexOutOfBoundsException("Indice fuera de los limites de la matriz.");
        }
    }

    public int getRowCount() {
        return matrix.length;
    }

    public int getColumnCount() {
        return matrix.length > 0 ? matrix[0].length : 0;
    }

    public void printMatrix() {
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
        }
    }
    
    public int[][] getMatrix(){
    	return matrix;
    }
    
}
