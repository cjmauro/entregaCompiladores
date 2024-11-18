package compilador;

public class asignarColumna {

    public int detectar_columna(char ch) {
        // Manejo especial para caracteres no alfab�ticos
    	
        if (Character.isLetter(ch)) {
            if (ch != 's') {
                ch = 'l'; // Agrupamos todas las letras excepto 's' como 'l'
            }
        } else if (Character.isDigit(ch)) {
            int digit = Character.getNumericValue(ch);
            if (digit > 0 && digit <= 7) {
                ch = '1'; // D�gitos del 0 al 7
            } else if (digit == 8 || digit == 9) {
                ch = '8'; // D�gitos 8 y 9
            } else if (digit == 0) {
                ch = '0';
            }
            } else {
                return Recuperar_columna(ch);
           }

        return Recuperar_columna(ch); // Retornar la columna para caracteres v�lidos
    }

    public int Recuperar_columna(char c) {
        switch (c) {
            case 'l': // Letras menos la S
            case '¡':
                return 0;
            case 's': // Letra S
                return 1;
            case '8': // D�gitos del 8 al 9
                return 2;
            case '0': // D�gito 0
                return 3;
            case '1': // D�gitos del 1 al 7, para casos octales
                return 4;
            case '<':
                return 5;
            case '>':
                return 6;
            case '!':
                return 7;
            case ':':
                return 8;
            case '=':
                return 9;
            case '(':
                return 10;
            case ')':
                return 11;
            case ';':
                return 12;
            case '+':
                return 13;
            case '-':
                return 14;
            case '*':
                return 15;
            case '/':
                return 16;
            case ',':
                return 17;
            case '.':
                return 18;
            case '[':
                return 19;
            case ']':
                return 20;
            case '#':
                return 24;
            case '_':
                return 25;
            case '\n':
            case '\r': 
                return 21;
            case '\t':
                return 22; 
            case ' ': 
                return 23;
            case '{':
                return 26; 
            case '}': 
                return 27;
            default:
                return -1; // Valor por defecto para caracteres no especificados
        }
    }
}