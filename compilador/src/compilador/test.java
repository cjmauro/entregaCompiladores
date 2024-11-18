package compilador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class test {
    public static void main(String[] args) throws IOException {
        System.out.println("Por favor, ingrese la ruta del archivo: ");
        Scanner scanner = new Scanner(System.in);
        String fileP = scanner.nextLine();
        //String fileP = "C:/Users/mauro/eclipse-workspace/compilador/src/CodigosDePrueba/1.txt";

        File file = new File(fileP);
        GeneradorDeCodigo gdc = new GeneradorDeCodigo(file);
        //parser.yydebug = false;
        
        System.out.println(gdc.getAsmString());
        String filePath = new File("").getAbsolutePath() + File.separator + "hello.asm";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(gdc.getAsmString());
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
        
    }
}

