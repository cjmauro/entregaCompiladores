package compilador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVaMatrizConValoresMultiples extends CSVaMatriz {
    private Map<Integer, Map<Integer, List<Integer>>> valoresMultiples;

    public CSVaMatrizConValoresMultiples(String csvFile) throws IOException {
        super(csvFile); 
        valoresMultiples = new HashMap<>();
        cargarValoresMultiples(csvFile);
    }

    private void cargarValoresMultiples(String csvFile) throws IOException {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); 

            int rowIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 1) {
                    for (int colIndex = 1; colIndex < values.length; colIndex++) {
                        String value = values[colIndex].trim();
                        List<Integer> parsedValues = new ArrayList<>();
                        
                        if (value.contains("_")) {
                            String[] multipleValues = value.split("-");
                            for (String val : multipleValues) {
                                try {
                                    parsedValues.add(Integer.parseInt(val));
                                } catch (NumberFormatException e) {
                                }
                            }
                        } else {
                            try {
                                parsedValues.add(Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                            }
                        }

                        valoresMultiples.computeIfAbsent(rowIndex, k -> new HashMap<>())
                                        .put(colIndex - 1, parsedValues);
                    }
                    rowIndex++;
                }
            }
        }
    }

    
    public List<Integer> getValoresMultiples(int row, int col) {
        return valoresMultiples.getOrDefault(row, new HashMap<>()).getOrDefault(col, new ArrayList<>());
    }
}
