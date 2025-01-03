%{
package compilador;
import java.io.IOException;
import compilador.analizadorLexico;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.io.File;
%}


%token MULTILINE
%token SINGLE
%token FUN
%token ID
%token BEGIN
%token END
%token END_IF
%token IF
%token THEN
%token ELSE
%token RET
%token ASIGN
%token PAIR
%token NEQ
%token GE
%token LE
%token OUTF
%token NUM, UP , DOWN, FOR, TYPEDEF, NUM_PAIR
%token OCTAL_TYPE, PAIR_TYPE, OCTAL, ULONGINT_TYPE, SINGLE_TYPE
%token TIPO_FUN

%%
start:
    ID BEGIN { 
                actual = $1.sval;
                polaca_map.put(actual, new ArrayList<String>());
                token palabra = (token) $1.obj; 
                start = palabra.getKey();
                alcance.push(palabra.getKey());} body END {alcance.pop();}
    | ID error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta el delimitador BEGIN o END."); }
    | error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta el nombre del programa (ID)."); }
;
    
body:
    retorno body
    | expresion body {}
    | /* vacio */
;

retorno:
    tipo FUN ID '(' parametro ')'{
                                        actual = $3.sval; 
                                        token palabra = (token) $3.obj;

                                        if(polaca_map.containsKey(palabra.getKey())){
                                            lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Función " + palabra.getKey() + " ya declarada.");
                                        }
                                        actualizar_tipo($1.sval, palabra.getKey());
                                        lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey())).setUso("funcion");
                                        token t =  lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey()));

                                        token_function functionpalabra = new token_function(t.getNombre(), t.getTipo(), t.getValue());
                                        functionpalabra.setRetorno($1.sval);
                                        
                                        functionpalabra.setParametro((token) $5.obj);
                                        lexer.getTablaSimbolos().put(generarNombreConStack(t.getKey()), functionpalabra);

                                        alcance.push($3.sval);
                                        polaca_map.put(actual, new ArrayList<String>()); 
                                        
                                        polaca_map.get(actual).add(generarNombreConStack($5.sval));
                                        lexer.getTablaSimbolos().put(generarNombreConStack($5.sval), (token) $5.obj);
    
                                        } BEGIN function END {alcance.pop(); actual = alcance.peek();}
    | tipo FUN error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el nombre de la funcion");}
    | FUN error
    | tipo ID '('')' {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta definir el tipo de la funcion");}
;
function:
    RET '(' operacion ')' ';'{
                                List<String> lista = (List<String>) $3.obj; 
                                for(String op : lista){
                                    agregar_a_polaca(op);
                                }
                                agregar_a_polaca("retorno");}
    | declaracion function 
    | asignacion function 
    | for function 
    | print function 
    | llamado_function ';' function 
    | if_en_function
    | error ';'{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el retorno en la funcion"); }
;

if_en_function:    
    IF '('condicion ')' {alcance.push("if"+ String.valueOf(polaca_map.get(actual).size()));} THEN   cuerpo_if   { alcance.pop(); $$.ival = polaca_map.get(actual).size();
                                                polaca_map.get(actual).set($3.ival, String.valueOf(polaca_map.get(actual).size()));} body_if_function 

    | IF error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Faltan parentesis o existe un error de syntaxis ");}
    | IF '(' palabras_reservadas error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Condición del if no puede ser una palabra reservada."); }
    ;

body_if_function:
    return ELSE {agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));} else {alcance.pop(); polaca_map.get(actual).set($2.ival, String.valueOf(polaca_map.get(actual).size()));} return end_if 
    | return end_if function
    | end_if function
    | ELSE {agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));} else { alcance.pop(); polaca_map.get(actual).set($2.ival, String.valueOf(polaca_map.get(actual).size()));}end_if function
    ;

return:
    RET '(' operacion ')' ';' {List<String> lista = (List<String>) $3.obj; 
                                for(String op : lista){
                                    agregar_a_polaca(op);
                                }
                                agregar_a_polaca("retorno");}
    ;



bloque_compuesto:
    expresion { $$ = $1; }
    | expresion bloque_compuesto {}
;

cuerpo_if:
    expresion {$$ = $1;
                $$.ival = polaca_map.get(actual).size()+1;}//esto es para el salto condicional
    | BEGIN bloque_compuesto END { $$ = $2; $$.ival = polaca_map.get(actual).size()+1;}
;



if:    
    IF '('condicion ')' {alcance.push("if"+ String.valueOf(polaca_map.get(actual).size()));} 
                        THEN   cuerpo_if   
                        { alcance.pop(); $$.ival = polaca_map.get(actual).size();
                        polaca_map.get(actual).set($3.ival, String.valueOf(polaca_map.get(actual).size()));} 
                        body_if 

    | IF error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Faltan parentesis o existe un error de syntaxis ");}
    | IF '(' palabras_reservadas error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Condición del if no puede ser una palabra reservada."); }
    ;

body_if:
    ELSE {agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));} else 
         {polaca_map.get(actual).set($2.ival, String.valueOf(polaca_map.get(actual).size()));} end_if 
    | end_if
    ;

end_if:
    END_IF ';' 
    | error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta end_if");}
    ;

else:
    cuerpo_if {$$ = $1;}
    | error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Falta el contenido del else");}
    ;

expresion:
    declaracion { $$ = $1; }
    | asignacion { $$ = $1; } 
    | if { $$ = $1; }
    | for { $$ = $1; }
    | print { $$ = $1; }
    | llamado_function ';' { $$ = $1; }
    | error ';' {   lexer.getErrores().add("Error en línea " + 
                    lexer.getLineaActual() + ": ; en expresion"); }
;

print:
    OUTF '(' MULTILINE ')' ';' {agregar_a_polaca($3.sval);
                                agregar_a_polaca("OUTF");}
    | OUTF '(' operacion ')' ';' {List<String> lista = (List<String>) $3.obj;  
                                    for(String op : lista){
                                        agregar_a_polaca(op);
                                    }
                                   agregar_a_polaca("OUTF");}
    | OUTF '(' palabras_reservadas ')' { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": parámetro incorrecto en OUTF, palabra reservada no permitida."); }
    | OUTF '(' ')' { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ":parametro inexistente o incorrecto en OUTF"); }
    | OUTF '(' error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Falta parentesis de cierre en la expresion de OUTF"); }
    | OUTF ')' error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Falta parentesis de apertura en la expresion de OUTF"); }
    | OUTF ';' error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Faltan los parametros y parentesis en la expresion de OUTF"); }
;

declaracion:
    tipo lista_variables ';'     {List<String> lista = (List<String>) $2.obj;

                                    for(String item : lista){
                                        String[] partes = item.split(":");
                                        if(validar_alcance(item)){
                                            System.out.println("Warning: " + lexer.getLineaActual() + ": Variable " + item + " ya declarada en el alcance actual.");
                                        }
                                        String p1 = partes.length > 0 ? partes[0] : ""; 
                                        lexer.getTablaSimbolos().get(p1).setTipo($1.sval.toLowerCase());
                                        if(lexer.getTablaSimbolos().get(item) == null){
                                            token palabra = new token(item, $1.sval.toLowerCase(), ID);
                                            lexer.getTablaSimbolos().put(item, palabra);
                                        }
                                        actualizar_tipo($1.sval, item);
                                        
                                        agregar_a_polaca(item);
                                    }
                                   }
                                   
                                   
    | tipo lista_variables ASIGN lista_operaciones ';'  {

                        List<String> lista = (List<String>) $2.obj;
                        List<List<String>> valores = (List<List<String>>) $4.obj;
                        
                        for(String item : lista){
                            String[] partes = item.split(":");
                            String p1 = partes.length > 0 ? partes[0] : ""; 
                            token palabra = (token) lexer.getTablaSimbolos().get(p1);
                            palabra.setTipo($1.sval.toLowerCase());
                            actualizar_tipo($1.sval, palabra.getKey());
                            }
                        cargar_asignacion_en_polaca(lista, valores);
    
                        }
    | TYPEDEF PAIR '<' tipo '>' ID ';'  {token palabra = (token) $6.obj;
                                        palabra.setUso("tipo");      
                                        String resultado = palabra.getKey();
                                        actualizar_tipo($4.sval,resultado);
                                        agregar_a_polaca(resultado);
                                        }

    | TYPEDEF  '<' tipo '>' error { lexer.getErrores().add("Error en línea " 
                                    + lexer.getLineaActual() + ": Falta definir el tipo pair "); } 
    | TYPEDEF PAIR tipo ID ';'  error { lexer.getErrores().add("Error en línea " 
                                + lexer.getLineaActual() + ": Falta encerrar el tipo entre <> "); }
    | TYPEDEF PAIR '<' tipo '>' ';'  error { lexer.getErrores().add("Error en línea " 
                                + lexer.getLineaActual() + ": Falta definir el nombre del pair "); }
;


asignacion:
    lista_variables ASIGN lista_operaciones ';' 
    {
        boolean error = false;
        for (String op : (List<String>) $1.obj) { 

            if (op.contains("|POSITION")) {
                op = op.substring(0, op.indexOf("|POSITION"));
            }
            if(!validar_alcance(op)){
                error = true;
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Variable " + op + " no declarada en el alcance actual."); }
        }
        if (!error){
            List<String> lista = (List<String>) $1.obj;
            List<List<String>> valores = (List<List<String>>) $3.obj;
            cargar_asignacion_en_polaca(lista, valores);
            $$ = new ParserVal(lista);
        }
    }
;

lista_variables:
    variable 
    {
        List<String> lista = new ArrayList<>();  
        lista.add($1.sval);  
        $$ = new ParserVal(lista);  
    }
    | lista_variables ',' variable 
    {
        List<String> lista = (List<String>) $1.obj;  
        lista.add($3.sval);  
        $$ = new ParserVal(lista); 
    }
    | lista_variables ',' error 
    { 
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta una variable después de la coma"); 
        $$ = $1;  
    }
;


variable:
    ID  
    {
        token palabra = (token) $1.obj;
        $$ = new ParserVal(generarNombreConStack(palabra.getKey()));
    }
    | ID '{' NUM '}' {token palabra = (token) $1.obj;
                        $$ = new ParserVal(generarNombreConStack(palabra.getKey())+"|POSITION"+$3.sval+"|");}
    | ID '{'error  
    {
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": falta pasar valor entre llaves o valor mal definido ");
    }
;
operacion:
    operacion '+' multi_div {
        $$ = new ParserVal(combineListsWithOperator((List<String>) $1.obj, (List<String>) $3.obj, "+"));
    }
    | operacion '-' multi_div {
        $$ = new ParserVal(combineListsWithOperator((List<String>) $1.obj, (List<String>) $3.obj, "-"));
    }
    | multi_div {
        $$ = $1;  
    }
;

multi_div:
    multi_div '*' valor {
        List<String> lista = new ArrayList<String>();
        lista.addAll((List<String>) $1.obj);
        lista.addAll((List<String>) $3.obj);
        lista.add("*");

        $$ = new ParserVal(lista);
    }
    | multi_div '/' valor {
                List<String> lista = new ArrayList<String>();
                lista.addAll((List<String>) $1.obj);
                lista.addAll((List<String>) $3.obj);
                lista.add("/"); 
                $$ = new ParserVal(lista);}
    | valor {
        List<String> lista = (List<String>) $1.obj;
        for(String op : lista){
            if (op.contains("|POSITION")) {
                op = op.substring(0, op.indexOf("|POSITION"));
            }
            if(!validar_alcance(op)){
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Variable " + op + " no declarada en el alcance actual.");
            }  
        }
        $$ = new ParserVal(lista);
        
    }
    | error {
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Error en la operación.");
    }
;


valor:
    variable {List<String> lista = new ArrayList<String>();  
                lista.add($1.sval);
                $$ = new ParserVal(lista);}
    | OCTAL {token palabra = (token) $1.obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                $$ = new ParserVal(lista);}
    | SINGLE {token palabra = (token) $1.obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                $$ = new ParserVal(lista);}
    | NUM       {token palabra = (token) $1.obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                $$ = new ParserVal(lista);}
    | ID MULTILINE {token palabra = (token) $1.obj; 
                    token multilinea = (token) $2.obj;
                    List<String> lista = new ArrayList<String>();
                    lista.add(palabra.getKey());
                    lista.add(multilinea.getKey());
                    $$ = new ParserVal(lista);}
    | NUM_PAIR {token palabra = (token) $1.obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                $$ = new ParserVal(lista);}
    | llamado_function { $$ = $1; }
    | '-' valor {token palabra = (token) $2.obj; 
                 String resultado =  palabra.getKey(); 
                 agregarSimbolo(resultado, palabra);
                 List<String> lista = new ArrayList<String>();
                 lista.add("-"+ palabra.getKey()); }
;
          
lista_operaciones:
    operacion 
    {   List<List<String>> lista = new ArrayList<List<String>>();
        List<String> lista2 = (List<String>) $1.obj;
        lista.add(lista2);
        
        $$ = new ParserVal(lista);
    }
    | lista_operaciones ',' operacion  
    {
        List<List<String>> lista = (List<List<String>>) $1.obj;
        List<String> lista2 = (List<String>) $3.obj;
        lista.add(lista2);
        $$ = new ParserVal(lista);
    }
;

llamado_function:
    ID '('operacion ')' 
            {
            token palabra = (token) $1.obj; 
            token_function tf = (token_function) lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey()));
            List<String> lista = new ArrayList<String>();

            if(!polaca_map.containsKey($1.sval)){
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Función " + $1.sval + " no declarada.");
            }else{

                if (tf == null) {
                    String input = generarNombreConStack(palabra.getKey());
                    if (input.endsWith(palabra.getKey())) {          
                        input = input.substring(0, input.length() - palabra.getKey().length() -1 );
                        tf = (token_function) lexer.getTablaSimbolos().get(input);
                    }
                    else{
                        String[] partes = input.split(":");

                        StringBuilder incremental = new StringBuilder();
                        incremental.append(partes[0]);
                        for (int i = 1; i < partes.length; i++) {
                            if (i > 0) {
                                incremental.append(":");
                            }
                            incremental.append(partes[i]);

                            if(lexer.getTablaSimbolos().get(incremental.toString()) instanceof token_function){
                                tf = (token_function) lexer.getTablaSimbolos().get(incremental.toString());
                                break;
                            }
                         
                        }
    


                    }
                }

                lista.add(tf.getParametro().getKey());
                lista.addAll((List<String>) $3.obj);  
                lista.add("=");
                lista.add("CALL");
                lista.add($1.sval);
            }
  

            $$ = new ParserVal(lista); }   
    | ID '(' ')' error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta argumento en llamado de función."); 
                        $$ = new ParserVal(new ArrayList<String>());}
                        
;

parametro:
    tipo ID {token palabra = (token) $2.obj;
            palabra.setUso("parametro");
            palabra.setTipo($1.sval);
            $$ = new ParserVal(palabra);
            $$.sval = palabra.getKey(); }
    | tipo error { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el nombre de parametro formal en la declaracion de la funcion"); }
    | error ID { lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el tipo de parametro formal en la declaracion de la funcion"); }
    | error { lexer.getErrores().add("PARAMETRO NO VALIDO"); }
;

tipo:
    ID {$$ = new ParserVal($1.sval); 
        if (lexer.getTablaSimbolos().get($1.sval).getUso() != "tipo"){
            lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Tipo " + $1.sval + " no valido");
        }
        }
    | PAIR_TYPE { $$ = new ParserVal($1.sval); }
    | ULONGINT_TYPE {$$ = new ParserVal($1.sval); }
    | OCTAL_TYPE {$$ = new ParserVal($1.sval); }
    | SINGLE_TYPE {$$ = new ParserVal($1.sval); }
;

palabras_reservadas:
    FOR
    | BEGIN
    | IF
    | END
    | END_IF
    | ELSE
;

comp:
    '=' {$$.sval = "=";}
    | NEQ {$$.sval = "!=";}
    | '>' {$$.sval = ">";}
    | '<' {$$.sval = "<";}
    | GE{$$.sval = ">=";}
    | LE{$$.sval = "<=";}
    | error { lexer.getErrores().add("Error en línea " 
                + lexer.getLineaActual() + ":falta comparador"); }
;

condicion:
    '(' condicion ')' { $$ = $2; }
    | valor comp valor {List<String> lista = new ArrayList<String>();
                        lista.addAll((List<String>) $1.obj);  
                        for(String op : (List<String>) $1.obj){
                            agregar_a_polaca(op);
                        }
                        for(String op : (List<String>) $3.obj){
                            agregar_a_polaca(op);
                        }
                        agregar_a_polaca($2.sval);
                        $$.ival = polaca_map.get(actual).size();
                        agregar_a_polaca("");
                        agregar_a_polaca("bf");
                        }
    | {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Error en la condicion");}
    | valor  valor {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Error en la condicion, falta comparador");}
;

for:
    FOR '(' declaracion_For ';' condicion  ';' incremento  ')'
                    {$1.ival = i; alcance.push("for"+ String.valueOf(polaca_map.get(actual).size()));
                    lexer.getTablaSimbolos().put(generarNombreConStack($3.sval), new token($3.sval, "ulongint", ID));
                    } cuerpo_for {
                    alcance.pop();
                    List<String> lista = (List<String>) $7.obj;
                    agregar_a_polaca(generarNombreConStack($3.sval));
                    agregar_a_polaca(generarNombreConStack($3.sval));
                    for(String op : lista){
                        agregar_a_polaca(op);
                    }
                    agregar_a_polaca("=");
                    agregar_a_polaca(String.valueOf($3.ival));
                    agregar_a_polaca("bi");
                    polaca_map.get(actual).set($5.ival, String.valueOf(polaca_map.get(actual).size()));
                    }

    | FOR '('declaracion_For ';' condicion ';' incremento ';' condicion ')' { alcance.push("for"+ String.valueOf(polaca_map.get(actual).size())); $1.ival = i;
                                            lexer.getTablaSimbolos().put(generarNombreConStack($3.sval), new token($3.sval, "ulongint", ID));
                                            } cuerpo_for{
                                            alcance.pop();
                                            List<String> lista = (List<String>) $7.obj;
                                            agregar_a_polaca(generarNombreConStack($3.sval));                                           
                                            agregar_a_polaca(generarNombreConStack($3.sval));
                                            for(String op : lista){
                                                agregar_a_polaca(op);
                                            }
                                            agregar_a_polaca("=");
                                            agregar_a_polaca(String.valueOf($3.ival));
                                            agregar_a_polaca("bi");
                                            polaca_map.get(actual).set($9.ival, String.valueOf(polaca_map.get(actual).size()));
                                            polaca_map.get(actual).set($5.ival, String.valueOf(polaca_map.get(actual).size()));
                                           }
    | FOR error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + 
                                                ":Falta parentesis en la condicion del for");}
    | FOR '(' error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual()
                                                + ": condicion del for mal definida");}
    | FOR '('declaracion_For condicion error {lexer.getErrores().add("Error en línea "
                                                + lexer.getLineaActual() + ":Falta ; en for");}
    | FOR '('declaracion_For ';' condicion  incremento error {lexer.getErrores().add("Error en línea "
                                                + lexer.getLineaActual() + ":Falta ; en for");}
;

cuerpo_for:
    BEGIN expresion_multiple END ';' {$$ = $2;}
    | expresion {$$ = $1;}
    | error {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Falta el cuerpo del for");}
    ;


declaracion_For:
    ID ASIGN NUM   {agregar_a_polaca(generarNombreConStack($1.sval));
                    agregar_a_polaca(generarNombreConStack($3.sval));
                    agregar_a_polaca("ASSING");
                    lexer.getTablaSimbolos().put($1.sval, new token($1.sval, "ulongint", ID));
                    ParserVal n = new ParserVal($1.sval);
                    n.ival = polaca_map.get(actual).size();
                    $$ = n;}   
    |ID ASIGN ID   {
                    String id = generarNombreConStack($3.sval);
                    if (id.contains("|POSITION")) {
                        id = id.substring(0, id.indexOf("|POSITION"));
                    }

                    agregar_a_polaca(generarNombreConStack($1.sval));
                    agregar_a_polaca(generarNombreConStack($3.sval));
                    agregar_a_polaca("ASSING");
                    lexer.getTablaSimbolos().put($1.sval, new token($1.sval, "ulongint", ID));
                    ParserVal n = new ParserVal($1.sval);
                    n.ival = polaca_map.get(actual).size();
                    $$ = n;}   
;

incremento:
    UP NUM  {List<String> lista = new ArrayList<String>();
            lista.add($2.sval);
            lista.add("+");
            $$= new ParserVal(lista);}

    | DOWN NUM  {
                List<String> lista = new ArrayList<String>();
                lista.add($2.sval);
                lista.add("-");
                $$= new ParserVal(lista);}
    | error ';' {lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                        + ":incremento/decremento del bucle mal definido");}
;

expresion_multiple:
    expresion expresion_multiple
    |expresion
;

%%

Map<String, Short> palabrasTokens = new HashMap<>();

private static Integer i = 0;

private static Map<String, List<String>> polaca_map = new HashMap<>(); 
private static String actual = ""; 
private String aux = ""; 

private Stack<Integer> pila = new Stack<>();
private String start = "";
private static Stack<String> alcance = new Stack<>();

private static analizadorLexico lexer;
static Map<String, token> TablaSimbolos= new HashMap<>();

private ParserVal ParserVal;
private int asignacion_de_token = 1;

@SuppressWarnings("unchecked")
private int yylex() {
    token t;
    try {
    	ParserVal a = lexer.leerToken();
    	if(a != null){
	    	t = (token) a.obj;
	    	ParserVal = new ParserVal(t);
	    	yylval = ParserVal;
            ParserVal.sval = t.getKey();
	    	return t.getValue(); 
    	}else {
    		return -1;
    	}
    } catch (IOException e) {
        lexer.agregarError("Error de entrada/salida: " + e.getMessage());
        return -1; 
    }
}

private void actualizar_tipo(String param, String p1){
    String cadena = p1;
    while (cadena.contains(":")) { 
        if(lexer.getTablaSimbolos().containsKey(cadena)){
            if (lexer.getTablaSimbolos().get(cadena).getTipo() != null && !(lexer.getTablaSimbolos().get(cadena).getTipo().equals(param.toLowerCase()))) {
                System.out.println("REDECLARACION DE VARIABLE: " + p1 + " desde el tipo " + lexer.getTablaSimbolos().get(cadena).getTipo() + " a " + 
                param.toLowerCase() + " en la linea: " + lexer.getLineaActual());
                break;
            }
        }
        cadena = cadena.substring(0, cadena.lastIndexOf(":"));
    }

    token simbolo = lexer.getTablaSimbolos().get(p1);
    if ("funcion".equals(simbolo.getUso())) { 
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": La variable " + p1 + " es una función, no se puede declarar.");
    } else {
        simbolo.setTipo(param.toLowerCase()); 
    }
    lexer.getTablaSimbolos().put(generarNombreConStack(p1) , simbolo);  
}

private void yyerror(String string2) {
    lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": " + string2);
}

public Parser(File file) throws IOException {
    lexer = new analizadorLexico(palabrasTokens, file);
    cargarPalabrasTokens();
}

public void agregarSimbolo(String nombre, token t) {
    if (!lexer.getTablaSimbolos().containsKey(nombre)) {
        lexer.getTablaSimbolos().put(nombre, t);
    } else {
        lexer.getErrores().add("Error: El símbolo '" + nombre + "' ya existe en la Tabla de Símbolos.");
    }

}


public void cargarPalabrasTokens() {
        palabrasTokens.put("for", Parser.FOR);
        palabrasTokens.put("up", Parser.UP);
        palabrasTokens.put("down", Parser.DOWN);
        palabrasTokens.put("pair", Parser.PAIR);
        palabrasTokens.put("if", Parser.IF);
        palabrasTokens.put("then", Parser.THEN);
        palabrasTokens.put("else", Parser.ELSE);
        palabrasTokens.put("begin", Parser.BEGIN);
        palabrasTokens.put("end", Parser.END);
        palabrasTokens.put("end_if", Parser.END_IF);
        palabrasTokens.put("outf", Parser.OUTF);
        palabrasTokens.put("typedef", Parser.TYPEDEF);
        palabrasTokens.put("fun", Parser.FUN);
        palabrasTokens.put("ret", Parser.RET);
        palabrasTokens.put("octal", Parser.OCTAL_TYPE);
        palabrasTokens.put("(", (short) 40);
        palabrasTokens.put(")", (short) 41);
        palabrasTokens.put(",", (short) 44);
        palabrasTokens.put(":", (short) 58); 
        palabrasTokens.put(":=", Parser.ASIGN);
        palabrasTokens.put("=", (short) 61);
        palabrasTokens.put(">", (short) 62);
        palabrasTokens.put("<", (short) 60);
        palabrasTokens.put("+", (short) 43);
        palabrasTokens.put("-", (short) 45);
        palabrasTokens.put("*", (short) 42);
        palabrasTokens.put("/", (short) 47);
        palabrasTokens.put("<=", Parser.LE);
        palabrasTokens.put(">=", Parser.GE);
        palabrasTokens.put("!=", Parser.NEQ);
        palabrasTokens.put("single", Parser.SINGLE_TYPE);
        palabrasTokens.put("ulongint", Parser.ULONGINT_TYPE);
        palabrasTokens.put("{", (short) 123);
        palabrasTokens.put("}", (short) 125);
        palabrasTokens.put(";", (short) 59);
   
}

public void imprimirPolaca() {
        if(!polaca_map.isEmpty()){
            for(String key : polaca_map.keySet()){
                System.out.println("Polaca de: " + key);
                List<String> polaca = polaca_map.get(key);
                for (int i = 0; i < polaca.size(); ++i) {
                    System.out.println(i + " " + polaca.get(i));
                }
            }
        }
}

public void imprimirAlcance() {
        if (!alcance.isEmpty()) {
                System.out.println();
                System.out.println("alcance:");

                for (int i = 0; i < alcance.size(); ++i) {
                        System.out.println(i + " " + alcance.get(i));
                }
        }
}

private List<String> combineListsWithOperator(List<String> lista1, List<String> lista2, String operator) {
    lista1.addAll(lista2);
    lista1.add(operator);
    return lista1;
}

public String generarNombreConStack(String nombre) {
    if (!nombre.contains(":")) {
        StringBuilder resultado = new StringBuilder(nombre);
        for (int i = 0; i < alcance.size(); i++) {
            String elemento = alcance.get(i);
            resultado.append(":").append(elemento);
        }
        return resultado.toString();
    }
    return "";
}

public static boolean validar_alcance(String token){
    if (token.split(":").length <=  1) {
        return true;
    }
    for (Map.Entry<String, token> entry : lexer.getTablaSimbolos().entrySet()) {

        if (entry.getKey().contains(":")){

            if(token.startsWith(entry.getKey())){
                return true;
            }

        }
    }
    return false;
}

public static void agregar_a_polaca(String token) { 
    polaca_map.get(actual).add(token);
    i++;
}



public void cargar_asignacion_en_polaca(List<String> variables, List<List<String>> operaciones) {
    if (variables.size() != operaciones.size()) {
        if (variables.size() > operaciones.size()) {
            System.out.println("Warning: Hay más variables que operaciones. Los elementos sobrantes serán asignados a 0.");
        } 
        else if (variables.size() < operaciones.size()) {
            System.out.println("Warning: Hay más operaciones que variables. Las expresiones sobrantes serán descartadas.");
        }
    }

    for (int i = 0; i < variables.size(); i++) {
        String variable = variables.get(i);
 
        agregar_a_polaca(variable);
            if (i < operaciones.size()) {
                List<String> operacion = operaciones.get(i);
                for (String op : operacion) {
                    
                    agregar_a_polaca(op);
                }

            } else {
                agregar_a_polaca("0");
            }
            agregar_a_polaca("ASSING");
        
    }


}

void imprimir_tabla() {
    for (Map.Entry<String, token> entry : lexer.getTablaSimbolos().entrySet()) {
        String key = entry.getKey();
        token t = entry.getValue();
        System.out.println("elemento: " + t.getTipo() + " " + key);
    }
}


public Map<String, List<String>> getPolaca() {
    return polaca_map;
}

public String get_nombre_start() {
    return start;
}

public Map<String, token> getTablaSimbolos() {
    return lexer.getTablaSimbolos();
}

public void imprimirTablaSimbolos() {
    for (Map.Entry<String, token> entry : lexer.getTablaSimbolos().entrySet()) {
        String key = entry.getKey();
        token t = entry.getValue();
        System.out.println("elemento: " + t.getTipo() + " " + key);
    }
}

public List<String> getErrores() {
    return lexer.getErrores();
}

public List<Integer> getSaltos(String polaca) {
    List<Integer> saltos = new ArrayList<>();
    List<String> lista = polaca_map.get(polaca);
    for (int i = 0; i < lista.size(); i++) {
        if (lista.get(i).equals("bi") || lista.get(i).equals("bf")) {
           saltos.add(Integer.parseInt(lista.get(i-1)));
        }
    }
    return saltos;
}