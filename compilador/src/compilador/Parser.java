//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "test.y"
package compilador;
import java.io.IOException;
import compilador.analizadorLexico;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.io.File;
//#line 27 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short MULTILINE=257;
public final static short SINGLE=258;
public final static short FUN=259;
public final static short ID=260;
public final static short BEGIN=261;
public final static short END=262;
public final static short END_IF=263;
public final static short IF=264;
public final static short THEN=265;
public final static short ELSE=266;
public final static short RET=267;
public final static short ASIGN=268;
public final static short PAIR=269;
public final static short NEQ=270;
public final static short GE=271;
public final static short LE=272;
public final static short OUTF=273;
public final static short NUM=274;
public final static short UP=275;
public final static short DOWN=276;
public final static short FOR=277;
public final static short TYPEDEF=278;
public final static short NUM_PAIR=279;
public final static short OCTAL_TYPE=280;
public final static short PAIR_TYPE=281;
public final static short OCTAL=282;
public final static short ULONGINT_TYPE=283;
public final static short SINGLE_TYPE=284;
public final static short TIPO_FUN=285;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    2,    0,    0,    0,    1,    1,    1,    7,    3,    3,
    3,    3,    8,    8,    8,    8,    8,    8,    8,    8,
   17,   20,   15,   15,   15,   24,   25,   19,   19,   19,
   27,   28,   19,   22,   29,   29,   18,   18,   31,   33,
   30,   30,   30,   34,   35,   32,   32,   26,   26,   23,
   23,    4,    4,    4,    4,    4,    4,    4,   13,   13,
   13,   13,   13,   13,   13,   10,   10,   10,   10,   10,
   10,   11,   36,   36,   36,   38,   38,   38,    9,    9,
    9,   39,   39,   39,   39,   40,   40,   40,   40,   40,
   40,   40,   40,   37,   37,   14,   14,    6,    6,    6,
    6,    5,    5,    5,    5,    5,   21,   21,   21,   21,
   21,   21,   41,   41,   41,   41,   41,   41,   41,   16,
   16,   16,   16,   45,   12,   46,   12,   12,   12,   12,
   12,   44,   44,   44,   42,   42,   43,   43,   43,   47,
   47,
};
final static short yylen[] = {                            2,
    0,    5,    2,    1,    2,    2,    0,    0,   10,    3,
    2,    4,    5,    2,    2,    2,    2,    3,    1,    2,
    0,    0,    9,    2,    4,    0,    0,    7,    3,    2,
    0,    0,    6,    5,    1,    2,    1,    3,    0,    0,
    9,    2,    4,    0,    0,    5,    1,    2,    1,    1,
    1,    1,    1,    1,    1,    1,    2,    2,    5,    5,
    4,    3,    3,    3,    3,    3,    5,    7,    5,    6,
    7,    4,    1,    3,    3,    1,    4,    3,    3,    3,
    1,    3,    3,    1,    1,    1,    1,    1,    1,    2,
    1,    1,    2,    1,    3,    4,    4,    2,    2,    2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    3,    0,    2,    0,   10,    0,   12,    2,    3,    5,
    7,    4,    1,    1,    3,    3,    2,    2,    2,    2,
    1,
};
final static short yydefred[] = {                         0,
    4,    0,    0,    3,    1,    0,    0,    0,    0,    0,
    0,    0,    0,  105,  103,  104,  106,    0,    0,    0,
    0,   52,   53,   55,   56,    0,   54,    0,   73,   58,
   11,    0,    0,   42,    0,    0,    0,    0,  128,    0,
    0,    0,    2,    5,    6,    0,    0,    0,   57,    0,
    0,   85,   88,    0,   89,   91,   87,    0,    0,    0,
   92,   86,    0,   84,   78,    0,  108,  110,  111,  109,
  112,  107,    0,    0,    0,    0,    0,    0,   62,    0,
    0,   64,   65,  129,    0,    0,  102,    0,    0,    0,
   10,    0,    0,    0,   66,    0,    0,   75,    0,   74,
   90,   97,   93,   96,    0,    0,    0,    0,   77,    0,
   39,   43,  119,  114,  117,  118,  116,  115,  113,  123,
    0,    0,    0,   61,    0,    0,    0,    0,    0,    0,
    0,   12,    0,   72,    0,    0,    0,   82,   83,  120,
    0,  121,   59,   60,  136,  135,    0,  130,    0,    0,
   69,    0,    0,    0,   67,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   70,  100,   99,   98,    8,    0,
   37,    0,   40,  139,  137,  138,    0,  131,   68,   71,
    0,    0,    0,    0,  124,    0,    0,   36,   38,   49,
    0,   44,   47,   41,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   19,   48,    0,    0,    0,
  133,  125,  126,   20,   24,    0,    0,    9,   14,   15,
   16,   17,    0,    0,   50,   45,    0,    0,    0,    0,
    0,    0,   18,    0,  140,    0,  127,   21,   25,    0,
   46,  132,    0,   13,    0,   22,    0,   31,    0,   23,
    0,    0,    0,    0,   26,    0,   30,   32,    0,    0,
   29,    0,    0,   27,    0,   34,    0,   33,    0,   28,
};
final static short yydgoto[] = {                          3,
   18,    6,   19,  171,  172,  154,  181,  200,   96,   22,
   23,   24,   25,   61,  206,   74,  243,  225,  250,  247,
   75,  251,  226,  260,  267,  193,  253,  262,  183,   27,
  141,  194,  184,  208,  234,   28,   97,   29,   63,   64,
  121,   86,  162,  212,  195,  229,  228,
};
final static short yysindex[] = {                      -227,
    0, -145,    0,    0,    0,  224,   15, -174,  -21,  -37,
    3,  -16,  -56,    0,    0,    0,    0, -161,  224,  224,
 -135,    0,    0,    0,    0,   67,    0,  -34,    0,    0,
    0,   72, -202,    0,   16,   63, -120, -118,    0, -215,
  -23, -232,    0,    0,    0,  -90,  -13,  -39,    0,   78,
  -82,    0,    0,  -14,    0,    0,    0, -108,   89,  158,
    0,    0,  113,    0,    0,   58,    0,    0,    0,    0,
    0,    0,   23,  153,  -47,  -44,    0,  159,    0,  243,
  164,    0,    0,    0,  -58,  -38,    0, -232,  -18,  202,
    0,  225,  234,   78,    0,   69,   -4,    0,  176,    0,
    0,    0,    0,    0,   78,   78,   89,   89,    0,  259,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   89,  245,  250,    0, -153,   23,   57,  252,  263,   61,
 -138,    0,   25,    0,   78,  113,  113,    0,    0,    0,
   68,    0,    0,    0,    0,    0,  100,    0,  -53,   79,
    0,   84,  -81,  312,    0,   69,  264,  302,   91,   92,
 -217,  106,  308,  114,    0,    0,    0,    0,    0,  335,
    0,  118,    0,    0,    0,    0,   35,    0,    0,    0,
  119,  335,  107,  -76,    0,   23,  290,    0,    0,    0,
  322,    0,    0,    0,  305,  338,  326,    7,  350,  133,
  290,  290,  290,  290,  339,    0,    0,  320,   15,  335,
    0,    0,    0,    0,    0,   16,   78,    0,    0,    0,
    0,    0,  290,   15,    0,    0,  335,  135,  305,  359,
  145,  244,    0, -157,    0,  343,    0,    0,    0,  344,
    0,    0,  140,    0,  264,    0,  -94,    0,  366,    0,
  -48,  290,  320,   78,    0,  290,    0,    0,  298,  320,
    0, -157,  348,    0,  290,    0,  143,    0, -157,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,  149,    0,    0,  -35,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  149,  149,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  159,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -36,    0,    0,    0,
    0,    0,    0,   36,    0,    0,    0,    0,    0,    0,
    0,    0,   46,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  159,    0,    0,    0,  -11,    0,    0,    0,
    0,    0,    0,    0,    0,  156,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   29,    0,    0,  -36,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  108,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   88,  109,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  373,    0,    0,    0,   41,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  154,    0,    0,    0,  159,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  212,    0,
    0,    0,    0,    0,    0,  159,    0,    0,    0,    0,
    0,    0,    0,  -79,    0,    0,  160,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  137,    0,    0,  121,   51,    0,    0, -137,   14,  185,
  190,  217,  258,   -6,    0,  -51,    0, -142,    0,    0,
  -25,  148, -123,    0,    0, -209,    0,    0,  241,    0,
    0,    0,    0,    0,    0,   -9,  330,  323,   80,   85,
    0,    0,  265,  196,    0,    0,  200,
};
final static int YYTABLESIZE=619;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         26,
   59,   73,   35,   42,   51,  164,   59,   76,   76,   51,
   81,   48,   26,   26,  173,  117,  119,  118,   32,   95,
  126,  110,   76,   40,  241,   32,   93,   87,    1,   85,
   85,   85,    2,   85,  127,   85,   88,  252,  158,  135,
   84,  256,   36,   37,   85,   60,  216,   14,   15,   80,
   16,   17,  265,   65,  134,   73,   21,  159,  160,  270,
   59,   38,   73,  219,  220,  221,  222,   59,  135,   21,
   21,   66,   94,   30,  147,  185,   76,   76,   76,   76,
   76,   31,   76,  155,   95,  233,   81,   94,   81,   81,
   81,   89,   90,  186,   76,   76,   76,   76,  190,   95,
   43,   33,  246,   79,   81,  191,  145,   59,   33,   33,
    4,  105,   58,  106,  257,    5,   59,  152,  261,   76,
  146,   87,   59,   46,   47,   49,   20,  268,   79,  258,
   79,   79,   79,   59,  196,   82,  264,   83,  128,   20,
   20,   14,   15,  103,   16,   17,   79,  102,  156,   80,
   26,   80,   80,   80,  107,   44,   45,   76,  161,  108,
  120,  190,   48,   26,  230,   91,  122,   80,  191,   92,
   76,  248,  249,   98,  167,   26,   51,   99,  168,  190,
  205,  153,  109,   51,  136,  137,  191,   51,   26,  192,
  231,  138,  139,  111,  205,  205,  205,  205,  104,  122,
  105,   26,  106,   26,  124,  142,  163,  190,  112,  125,
   76,  113,   41,   53,  191,   54,  205,  255,   34,   53,
   26,   54,   26,  102,  102,  114,  115,  116,   94,   55,
  232,   76,   76,   50,   56,   55,   87,   57,   26,   39,
   56,  129,  101,   57,   63,  205,   26,   63,   63,  205,
   63,   63,   63,   26,   63,   63,   14,   15,  205,   16,
   17,   63,  215,  130,  131,   63,   63,  259,   63,   63,
   76,   63,   63,   53,  132,   54,   67,   68,   69,   70,
   53,   71,   54,  123,  240,  105,  105,  106,  106,   55,
  182,   76,   72,   76,   56,   76,   55,   57,   33,  140,
   76,   56,  182,  143,   57,   76,   76,   76,  144,   76,
   76,   76,  148,  149,   76,  211,  151,   76,   77,   78,
   53,  150,   54,   67,   68,   69,   70,   52,   71,   53,
  227,   54,  157,   52,  165,   53,   55,   54,  263,   72,
  105,   56,  106,  166,   57,   55,   53,  227,   54,  211,
   56,   55,  169,   57,   62,  158,   56,   62,   62,   57,
  174,  178,   55,  122,  175,  176,  179,   56,  189,  180,
   57,  201,   62,  100,  159,  160,  202,   99,  213,  187,
  207,   62,  122,  122,  214,  201,  201,  201,  201,  217,
  202,  202,  202,  202,  218,   62,  236,  223,   62,  238,
  239,  242,  244,  203,  245,  254,  266,  201,   62,  249,
    7,  122,  202,  101,  269,   35,   62,  203,  203,  203,
  203,  141,  188,  133,  237,  177,  235,   62,   62,   62,
   62,    0,    0,    0,    0,    0,  201,    0,    0,  203,
  201,  202,    0,   62,  204,  202,    0,    0,   62,  201,
    0,    0,    0,    0,  202,    0,    0,   62,  204,  204,
  204,  204,    0,    0,    0,    0,    0,  134,  203,    0,
  134,  134,  203,  134,  134,  134,    0,  134,  134,    7,
  204,  203,    8,    9,  134,    0,    0,   10,  134,  134,
    0,  134,  134,    0,  134,  134,   11,    0,    0,    0,
   12,   13,    0,   14,   15,    0,   16,   17,   62,  204,
    0,    0,    0,  204,    0,    0,    0,    0,    0,    7,
    0,    0,  204,    9,  170,    0,    0,   10,    0,    0,
    0,    0,    0,    0,    0,    0,   11,    0,   62,   62,
   12,   13,    0,   14,   15,  197,   16,   17,    0,    9,
    0,    0,    0,  198,    0,    0,  199,    0,    0,    0,
  209,    0,   11,    0,    9,  210,   12,   13,   10,   14,
   15,    0,   16,   17,    0,  224,   62,   11,    0,    9,
  170,   12,   13,   10,   14,   15,    0,   16,   17,    0,
    7,    0,   11,    0,    9,    0,   12,   13,   10,   14,
   15,    0,   16,   17,    0,    0,    0,   11,    0,    0,
    0,   12,   13,    0,   14,   15,    0,   16,   17,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          6,
   45,   40,   40,   60,   44,   59,   45,   44,   44,   44,
   36,   21,   19,   20,  157,   60,   61,   62,   40,   59,
   59,   73,   59,   40,  234,   40,   40,  260,  256,   41,
   42,   43,  260,   45,   86,   47,   60,  247,  256,   44,
  256,  251,   40,   41,  260,   32,   40,  280,  281,   36,
  283,  284,  262,  256,   59,   40,    6,  275,  276,  269,
   45,   59,   40,  201,  202,  203,  204,   45,   44,   19,
   20,  274,   44,   59,  126,   41,   41,   42,   43,   44,
   45,  256,   47,   59,   44,  223,   41,   59,   43,   44,
   45,   41,   42,   59,   59,   60,   61,   62,  256,   59,
  262,  123,  245,   41,   59,  263,  260,   45,  123,  123,
  256,   43,   41,   45,  252,  261,   45,  256,  256,   35,
  274,  260,   45,  259,  260,   59,    6,  265,   41,  253,
   43,   44,   45,   45,  186,  256,  260,  256,   88,   19,
   20,  280,  281,   59,  283,  284,   59,  256,  135,   41,
  157,   43,   44,   45,   42,   19,   20,   73,   59,   47,
   76,  256,  172,  170,  216,  256,   59,   59,  263,  260,
   86,  266,  267,  256,  256,  182,  256,  260,  260,  256,
  187,  131,  125,  263,  105,  106,  263,  267,  195,  266,
  216,  107,  108,   41,  201,  202,  203,  204,   41,   41,
   43,  208,   45,  210,   41,  121,  260,  256,  256,  268,
  126,  256,  269,  258,  263,  260,  223,  266,  256,  258,
  227,  260,  229,  259,  260,  270,  271,  272,  268,  274,
  217,  268,  268,  268,  279,  274,  260,  282,  245,  256,
  279,  260,  257,  282,  256,  252,  253,  259,  260,  256,
  262,  263,  264,  260,  266,  267,  280,  281,  265,  283,
  284,  273,  256,   62,   40,  277,  278,  254,  280,  281,
  186,  283,  284,  258,   41,  260,  261,  262,  263,  264,
  258,  266,  260,   41,   41,   43,   43,   45,   45,  274,
  170,  256,  277,  258,  279,  260,  274,  282,  123,   41,
  216,  279,  182,   59,  282,  270,  271,  272,   59,  274,
  275,  276,  256,   62,  279,  195,  256,  282,  256,  257,
  258,   59,  260,  261,  262,  263,  264,  256,  266,  258,
  210,  260,  265,  256,  256,  258,  274,  260,   41,  277,
   43,  279,   45,  260,  282,  274,  258,  227,  260,  229,
  279,  274,   41,  282,   32,  256,  279,   35,   36,  282,
   59,  256,  274,  256,  274,  274,   59,  279,  262,  256,
  282,  187,   50,   51,  275,  276,  187,  260,   41,  261,
   59,   59,  275,  276,   59,  201,  202,  203,  204,   40,
  201,  202,  203,  204,  262,   73,  262,   59,   76,   41,
  256,   59,   59,  187,  265,   40,   59,  223,   86,  267,
  262,  256,  223,   41,  267,  262,   94,  201,  202,  203,
  204,  262,  182,   94,  229,  161,  227,  105,  106,  107,
  108,   -1,   -1,   -1,   -1,   -1,  252,   -1,   -1,  223,
  256,  252,   -1,  121,  187,  256,   -1,   -1,  126,  265,
   -1,   -1,   -1,   -1,  265,   -1,   -1,  135,  201,  202,
  203,  204,   -1,   -1,   -1,   -1,   -1,  256,  252,   -1,
  259,  260,  256,  262,  263,  264,   -1,  266,  267,  256,
  223,  265,  259,  260,  273,   -1,   -1,  264,  277,  278,
   -1,  280,  281,   -1,  283,  284,  273,   -1,   -1,   -1,
  277,  278,   -1,  280,  281,   -1,  283,  284,  186,  252,
   -1,   -1,   -1,  256,   -1,   -1,   -1,   -1,   -1,  256,
   -1,   -1,  265,  260,  261,   -1,   -1,  264,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  273,   -1,  216,  217,
  277,  278,   -1,  280,  281,  256,  283,  284,   -1,  260,
   -1,   -1,   -1,  264,   -1,   -1,  267,   -1,   -1,   -1,
  256,   -1,  273,   -1,  260,  261,  277,  278,  264,  280,
  281,   -1,  283,  284,   -1,  256,  254,  273,   -1,  260,
  261,  277,  278,  264,  280,  281,   -1,  283,  284,   -1,
  256,   -1,  273,   -1,  260,   -1,  277,  278,  264,  280,
  281,   -1,  283,  284,   -1,   -1,   -1,  273,   -1,   -1,
   -1,  277,  278,   -1,  280,  281,   -1,  283,  284,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=285;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"MULTILINE","SINGLE","FUN","ID","BEGIN",
"END","END_IF","IF","THEN","ELSE","RET","ASIGN","PAIR","NEQ","GE","LE","OUTF",
"NUM","UP","DOWN","FOR","TYPEDEF","NUM_PAIR","OCTAL_TYPE","PAIR_TYPE","OCTAL",
"ULONGINT_TYPE","SINGLE_TYPE","TIPO_FUN",
};
final static String yyrule[] = {
"$accept : start",
"$$1 :",
"start : ID BEGIN $$1 body END",
"start : ID error",
"start : error",
"body : retorno body",
"body : expresion body",
"body :",
"$$2 :",
"retorno : tipo FUN ID '(' parametro ')' $$2 BEGIN function END",
"retorno : tipo FUN error",
"retorno : FUN error",
"retorno : tipo ID '(' ')'",
"function : RET '(' operacion ')' ';'",
"function : declaracion function",
"function : asignacion function",
"function : for function",
"function : print function",
"function : llamado_function ';' function",
"function : if_en_function",
"function : error ';'",
"$$3 :",
"$$4 :",
"if_en_function : IF '(' condicion ')' $$3 THEN cuerpo_if $$4 body_if_function",
"if_en_function : IF error",
"if_en_function : IF '(' palabras_reservadas error",
"$$5 :",
"$$6 :",
"body_if_function : return ELSE $$5 else $$6 return end_if",
"body_if_function : return end_if function",
"body_if_function : end_if function",
"$$7 :",
"$$8 :",
"body_if_function : ELSE $$7 else $$8 end_if function",
"return : RET '(' operacion ')' ';'",
"bloque_compuesto : expresion",
"bloque_compuesto : expresion bloque_compuesto",
"cuerpo_if : expresion",
"cuerpo_if : BEGIN bloque_compuesto END",
"$$9 :",
"$$10 :",
"if : IF '(' condicion ')' $$9 THEN cuerpo_if $$10 body_if",
"if : IF error",
"if : IF '(' palabras_reservadas error",
"$$11 :",
"$$12 :",
"body_if : ELSE $$11 else $$12 end_if",
"body_if : end_if",
"end_if : END_IF ';'",
"end_if : error",
"else : cuerpo_if",
"else : error",
"expresion : declaracion",
"expresion : asignacion",
"expresion : if",
"expresion : for",
"expresion : print",
"expresion : llamado_function ';'",
"expresion : error ';'",
"print : OUTF '(' MULTILINE ')' ';'",
"print : OUTF '(' operacion ')' ';'",
"print : OUTF '(' palabras_reservadas ')'",
"print : OUTF '(' ')'",
"print : OUTF '(' error",
"print : OUTF ')' error",
"print : OUTF ';' error",
"declaracion : tipo lista_variables ';'",
"declaracion : tipo lista_variables ASIGN lista_operaciones ';'",
"declaracion : TYPEDEF PAIR '<' tipo '>' ID ';'",
"declaracion : TYPEDEF '<' tipo '>' error",
"declaracion : TYPEDEF PAIR tipo ID ';' error",
"declaracion : TYPEDEF PAIR '<' tipo '>' ';' error",
"asignacion : lista_variables ASIGN lista_operaciones ';'",
"lista_variables : variable",
"lista_variables : lista_variables ',' variable",
"lista_variables : lista_variables ',' error",
"variable : ID",
"variable : ID '{' NUM '}'",
"variable : ID '{' error",
"operacion : operacion '+' multi_div",
"operacion : operacion '-' multi_div",
"operacion : multi_div",
"multi_div : multi_div '*' valor",
"multi_div : multi_div '/' valor",
"multi_div : valor",
"multi_div : error",
"valor : variable",
"valor : OCTAL",
"valor : SINGLE",
"valor : NUM",
"valor : ID MULTILINE",
"valor : NUM_PAIR",
"valor : llamado_function",
"valor : '-' valor",
"lista_operaciones : operacion",
"lista_operaciones : lista_operaciones ',' operacion",
"llamado_function : ID '(' operacion ')'",
"llamado_function : ID '(' ')' error",
"parametro : tipo ID",
"parametro : tipo error",
"parametro : error ID",
"parametro : error",
"tipo : ID",
"tipo : PAIR_TYPE",
"tipo : ULONGINT_TYPE",
"tipo : OCTAL_TYPE",
"tipo : SINGLE_TYPE",
"palabras_reservadas : FOR",
"palabras_reservadas : BEGIN",
"palabras_reservadas : IF",
"palabras_reservadas : END",
"palabras_reservadas : END_IF",
"palabras_reservadas : ELSE",
"comp : '='",
"comp : NEQ",
"comp : '>'",
"comp : '<'",
"comp : GE",
"comp : LE",
"comp : error",
"condicion : '(' condicion ')'",
"condicion : valor comp valor",
"condicion :",
"condicion : valor valor",
"$$13 :",
"for : FOR '(' declaracion_For ';' condicion ';' incremento ')' $$13 cuerpo_for",
"$$14 :",
"for : FOR '(' declaracion_For ';' condicion ';' incremento ';' condicion ')' $$14 cuerpo_for",
"for : FOR error",
"for : FOR '(' error",
"for : FOR '(' declaracion_For condicion error",
"for : FOR '(' declaracion_For ';' condicion incremento error",
"cuerpo_for : BEGIN expresion_multiple END ';'",
"cuerpo_for : expresion",
"cuerpo_for : error",
"declaracion_For : ID ASIGN NUM",
"declaracion_For : ID ASIGN ID",
"incremento : UP NUM",
"incremento : DOWN NUM",
"incremento : error ';'",
"expresion_multiple : expresion expresion_multiple",
"expresion_multiple : expresion",
};

//#line 591 "test.y"

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
        if (lexer.getTablaSimbolos().get(cadena).getTipo() != null && !lexer.getTablaSimbolos().get(cadena).getTipo().equals(param.toLowerCase())) {
            System.out.println("REDECLARACION DE VARIABLE: " + p1 + " desde el tipo " + lexer.getTablaSimbolos().get(p1).getTipo() + " a " + 
            param.toLowerCase() + " en la linea: " + lexer.getLineaActual());
            break;
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
    StringBuilder resultado = new StringBuilder(nombre);
    for (int i = 0; i < alcance.size(); i++) {
        String elemento = alcance.get(i);
        resultado.append(":").append(elemento);
    }
    return resultado.toString();
}

public static boolean validar_alcance(String token){
    if (token.split(":").length <=  1) {
        return true;
    }
    Stack<String> alcance_aux = (Stack<String>) alcance.clone();    
    String clave = token;
    while (alcance_aux.size() > 0){
        String actual = alcance_aux.pop();
        if (polaca_map.containsKey(actual)){

            List<String> polaca = polaca_map.get(actual);
            for (int i = 0; i < polaca.size(); i++){
                if (polaca.get(i).equals(clave)){
                    return true;
                }
            }
        }
        if (token.contains(":")) {
                clave = clave.substring(0, clave.lastIndexOf(":"));
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
//#line 829 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 37 "test.y"
{ 
                actual = val_peek(1).sval;
                polaca_map.put(actual, new ArrayList<String>());
                token palabra = (token) val_peek(1).obj; 
                start = palabra.getKey();
                alcance.push(palabra.getKey());}
break;
case 2:
//#line 42 "test.y"
{alcance.pop();}
break;
case 3:
//#line 43 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta el delimitador BEGIN o END."); }
break;
case 4:
//#line 44 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta el nombre del programa (ID)."); }
break;
case 6:
//#line 49 "test.y"
{}
break;
case 8:
//#line 54 "test.y"
{
                                        actual = val_peek(3).sval; 
                                        token palabra = (token) val_peek(3).obj;

                                        if(polaca_map.containsKey(palabra.getKey())){
                                            lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Función " + palabra.getKey() + " ya declarada.");
                                        }
                                        actualizar_tipo(val_peek(5).sval, palabra.getKey());
                                        lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey())).setUso("funcion");
                                        token t =  lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey()));

                                        token_function functionpalabra = new token_function(t.getNombre(), t.getTipo(), t.getValue());
                                        functionpalabra.setRetorno(val_peek(5).sval);
                                        
                                        functionpalabra.setParametro((token) val_peek(1).obj);
                                        lexer.getTablaSimbolos().put(generarNombreConStack(t.getKey()), functionpalabra);

                                        alcance.push(val_peek(3).sval);
                                        polaca_map.put(actual, new ArrayList<String>()); 
                                        
                                        polaca_map.get(actual).add(generarNombreConStack(val_peek(1).sval));
                                        lexer.getTablaSimbolos().put(generarNombreConStack(val_peek(1).sval), (token) val_peek(1).obj);
    
                                        }
break;
case 9:
//#line 77 "test.y"
{alcance.pop(); actual = alcance.peek();}
break;
case 10:
//#line 78 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el nombre de la funcion");}
break;
case 12:
//#line 80 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta definir el tipo de la funcion");}
break;
case 13:
//#line 83 "test.y"
{
                                List<String> lista = (List<String>) val_peek(2).obj; 
                                for(String op : lista){
                                    agregar_a_polaca(op);
                                }
                                agregar_a_polaca("retorno");}
break;
case 20:
//#line 95 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el retorno en la funcion"); }
break;
case 21:
//#line 99 "test.y"
{alcance.push("if"+ String.valueOf(polaca_map.get(actual).size()));}
break;
case 22:
//#line 99 "test.y"
{ alcance.pop(); yyval.ival = polaca_map.get(actual).size();
                                                polaca_map.get(actual).set(val_peek(4).ival, String.valueOf(polaca_map.get(actual).size()));}
break;
case 24:
//#line 102 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Faltan parentesis o existe un error de syntaxis ");}
break;
case 25:
//#line 103 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Condición del if no puede ser una palabra reservada."); }
break;
case 26:
//#line 107 "test.y"
{agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));}
break;
case 27:
//#line 107 "test.y"
{alcance.pop(); polaca_map.get(actual).set(val_peek(2).ival, String.valueOf(polaca_map.get(actual).size()));}
break;
case 31:
//#line 110 "test.y"
{agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));}
break;
case 32:
//#line 110 "test.y"
{ alcance.pop(); polaca_map.get(actual).set(val_peek(1).ival, String.valueOf(polaca_map.get(actual).size()));}
break;
case 34:
//#line 114 "test.y"
{List<String> lista = (List<String>) val_peek(2).obj; 
                                for(String op : lista){
                                    agregar_a_polaca(op);
                                }
                                agregar_a_polaca("retorno");}
break;
case 35:
//#line 124 "test.y"
{ yyval = val_peek(0); }
break;
case 36:
//#line 125 "test.y"
{}
break;
case 37:
//#line 129 "test.y"
{yyval = val_peek(0);
                yyval.ival = polaca_map.get(actual).size()+1;}
break;
case 38:
//#line 131 "test.y"
{ yyval = val_peek(1); yyval.ival = polaca_map.get(actual).size()+1;}
break;
case 39:
//#line 137 "test.y"
{alcance.push("if"+ String.valueOf(polaca_map.get(actual).size()));}
break;
case 40:
//#line 139 "test.y"
{ alcance.pop(); yyval.ival = polaca_map.get(actual).size();
                        polaca_map.get(actual).set(val_peek(4).ival, String.valueOf(polaca_map.get(actual).size()));}
break;
case 42:
//#line 143 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Faltan parentesis o existe un error de syntaxis ");}
break;
case 43:
//#line 144 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Condición del if no puede ser una palabra reservada."); }
break;
case 44:
//#line 148 "test.y"
{agregar_a_polaca(""); agregar_a_polaca("bi"); alcance.push("else"+ String.valueOf(polaca_map.get(actual).size()));}
break;
case 45:
//#line 149 "test.y"
{polaca_map.get(actual).set(val_peek(1).ival, String.valueOf(polaca_map.get(actual).size()));}
break;
case 49:
//#line 155 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta end_if");}
break;
case 50:
//#line 159 "test.y"
{yyval = val_peek(0);}
break;
case 51:
//#line 160 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Falta el contenido del else");}
break;
case 52:
//#line 164 "test.y"
{ yyval = val_peek(0); }
break;
case 53:
//#line 165 "test.y"
{ yyval = val_peek(0); }
break;
case 54:
//#line 166 "test.y"
{ yyval = val_peek(0); }
break;
case 55:
//#line 167 "test.y"
{ yyval = val_peek(0); }
break;
case 56:
//#line 168 "test.y"
{ yyval = val_peek(0); }
break;
case 57:
//#line 169 "test.y"
{ yyval = val_peek(1); }
break;
case 58:
//#line 170 "test.y"
{   lexer.getErrores().add("Error en línea " + 
                    lexer.getLineaActual() + ": ; en expresion"); }
break;
case 59:
//#line 175 "test.y"
{agregar_a_polaca(val_peek(2).sval);
                                agregar_a_polaca("OUTF");}
break;
case 60:
//#line 177 "test.y"
{List<String> lista = (List<String>) val_peek(2).obj;  
                                    for(String op : lista){
                                        agregar_a_polaca(op);
                                    }
                                   agregar_a_polaca("OUTF");}
break;
case 61:
//#line 182 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": parámetro incorrecto en OUTF, palabra reservada no permitida."); }
break;
case 62:
//#line 183 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ":parametro inexistente o incorrecto en OUTF"); }
break;
case 63:
//#line 185 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Falta parentesis de cierre en la expresion de OUTF"); }
break;
case 64:
//#line 187 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Falta parentesis de apertura en la expresion de OUTF"); }
break;
case 65:
//#line 189 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                    + ": Faltan los parametros y parentesis en la expresion de OUTF"); }
break;
case 66:
//#line 194 "test.y"
{List<String> lista = (List<String>) val_peek(1).obj;

                                    for(String item : lista){
                                        String[] partes = item.split(":");
                                        if(validar_alcance(item)){
                                            System.out.println("Warning: " + lexer.getLineaActual() + ": Variable " + item + " ya declarada en el alcance actual.");
                                        }
                                        String p1 = partes.length > 0 ? partes[0] : ""; 
                                        lexer.getTablaSimbolos().get(p1).setTipo(val_peek(2).sval.toLowerCase());
                                        if(lexer.getTablaSimbolos().get(item) == null){
                                            token palabra = new token(item, val_peek(2).sval.toLowerCase(), ID);
                                            lexer.getTablaSimbolos().put(item, palabra);
                                        }
                                        actualizar_tipo(val_peek(2).sval, item);
                                        
                                        agregar_a_polaca(item);
                                    }
                                   }
break;
case 67:
//#line 214 "test.y"
{

                        List<String> lista = (List<String>) val_peek(3).obj;
                        List<List<String>> valores = (List<List<String>>) val_peek(1).obj;
                        
                        for(String item : lista){
                            String[] partes = item.split(":");
                            String p1 = partes.length > 0 ? partes[0] : ""; 
                            token palabra = (token) lexer.getTablaSimbolos().get(p1);
                            palabra.setTipo(val_peek(4).sval.toLowerCase());
                            actualizar_tipo(val_peek(4).sval, palabra.getKey());
                            }
                        cargar_asignacion_en_polaca(lista, valores);
    
                        }
break;
case 68:
//#line 229 "test.y"
{token palabra = (token) val_peek(1).obj;
                                        palabra.setUso("tipo");      
                                        String resultado = palabra.getKey();
                                        actualizar_tipo(val_peek(3).sval,resultado);
                                        agregar_a_polaca(resultado);
                                        }
break;
case 69:
//#line 236 "test.y"
{ lexer.getErrores().add("Error en línea " 
                                    + lexer.getLineaActual() + ": Falta definir el tipo pair "); }
break;
case 70:
//#line 238 "test.y"
{ lexer.getErrores().add("Error en línea " 
                                + lexer.getLineaActual() + ": Falta encerrar el tipo entre <> "); }
break;
case 71:
//#line 240 "test.y"
{ lexer.getErrores().add("Error en línea " 
                                + lexer.getLineaActual() + ": Falta definir el nombre del pair "); }
break;
case 72:
//#line 247 "test.y"
{
        boolean error = false;
        for (String op : (List<String>) val_peek(3).obj) { 

            if (op.contains("|POSITION")) {
                op = op.substring(0, op.indexOf("|POSITION"));
            }
            if(!validar_alcance(op)){
                error = true;
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Variable " + op + " no declarada en el alcance actual."); }
        }
        if (!error){
            List<String> lista = (List<String>) val_peek(3).obj;
            List<List<String>> valores = (List<List<String>>) val_peek(1).obj;
            cargar_asignacion_en_polaca(lista, valores);
            yyval = new ParserVal(lista);
        }
    }
break;
case 73:
//#line 269 "test.y"
{
        List<String> lista = new ArrayList<>();  
        lista.add(val_peek(0).sval);  
        yyval = new ParserVal(lista);  
    }
break;
case 74:
//#line 275 "test.y"
{
        List<String> lista = (List<String>) val_peek(2).obj;  
        lista.add(val_peek(0).sval);  
        yyval = new ParserVal(lista); 
    }
break;
case 75:
//#line 281 "test.y"
{ 
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta una variable después de la coma"); 
        yyval = val_peek(2);  
    }
break;
case 76:
//#line 290 "test.y"
{
        token palabra = (token) val_peek(0).obj;
        yyval = new ParserVal(generarNombreConStack(palabra.getKey()));
    }
break;
case 77:
//#line 294 "test.y"
{token palabra = (token) val_peek(3).obj;
                        yyval = new ParserVal(generarNombreConStack(palabra.getKey())+"|POSITION"+val_peek(1).sval+"|");}
break;
case 78:
//#line 297 "test.y"
{
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": falta pasar valor entre llaves o valor mal definido ");
    }
break;
case 79:
//#line 302 "test.y"
{
        yyval = new ParserVal(combineListsWithOperator((List<String>) val_peek(2).obj, (List<String>) val_peek(0).obj, "+"));
    }
break;
case 80:
//#line 305 "test.y"
{
        yyval = new ParserVal(combineListsWithOperator((List<String>) val_peek(2).obj, (List<String>) val_peek(0).obj, "-"));
    }
break;
case 81:
//#line 308 "test.y"
{
        yyval = val_peek(0);  
    }
break;
case 82:
//#line 314 "test.y"
{
        yyval = new ParserVal(combineListsWithOperator((List<String>) val_peek(2).obj, (List<String>) val_peek(0).obj, "*"));
    }
break;
case 83:
//#line 317 "test.y"
{
        yyval = new ParserVal(combineListsWithOperator((List<String>) val_peek(2).obj, (List<String>) val_peek(0).obj, "/"));
    }
break;
case 84:
//#line 320 "test.y"
{
        yyval = val_peek(0);
        List<String> lista = (List<String>) val_peek(0).obj;
        for(String op : lista){
            if (op.contains("|POSITION")) {
                op = op.substring(0, op.indexOf("|POSITION"));
            }
            if(!validar_alcance(op)){
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Variable " + op + " no declarada en el alcance actual.");
            }  
        }
        
    }
break;
case 85:
//#line 333 "test.y"
{
        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Error en la operación.");
    }
break;
case 86:
//#line 339 "test.y"
{List<String> lista = new ArrayList<String>();  
                lista.add(val_peek(0).sval);
                yyval = new ParserVal(lista);}
break;
case 87:
//#line 342 "test.y"
{token palabra = (token) val_peek(0).obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                yyval = new ParserVal(lista);}
break;
case 88:
//#line 346 "test.y"
{token palabra = (token) val_peek(0).obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                yyval = new ParserVal(lista);}
break;
case 89:
//#line 350 "test.y"
{token palabra = (token) val_peek(0).obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                yyval = new ParserVal(lista);}
break;
case 90:
//#line 354 "test.y"
{token palabra = (token) val_peek(1).obj; 
                    token multilinea = (token) val_peek(0).obj;
                    List<String> lista = new ArrayList<String>();
                    lista.add(palabra.getKey());
                    lista.add(multilinea.getKey());
                    yyval = new ParserVal(lista);}
break;
case 91:
//#line 360 "test.y"
{token palabra = (token) val_peek(0).obj; 
                List<String> lista = new ArrayList<String>();
                lista.add(palabra.getKey());
                yyval = new ParserVal(lista);}
break;
case 92:
//#line 364 "test.y"
{ yyval = val_peek(0); }
break;
case 93:
//#line 365 "test.y"
{token palabra = (token) val_peek(0).obj; 
                 String resultado =  palabra.getKey(); 
                 agregarSimbolo(resultado, palabra);
                 List<String> lista = new ArrayList<String>();
                 lista.add("-"+ palabra.getKey()); }
break;
case 94:
//#line 374 "test.y"
{   List<List<String>> lista = new ArrayList<List<String>>();
        List<String> lista2 = (List<String>) val_peek(0).obj;
        lista.add(lista2);
        
        yyval = new ParserVal(lista);
    }
break;
case 95:
//#line 381 "test.y"
{
        List<List<String>> lista = (List<List<String>>) val_peek(2).obj;
        List<String> lista2 = (List<String>) val_peek(0).obj;
        lista.add(lista2);
        yyval = new ParserVal(lista);
    }
break;
case 96:
//#line 391 "test.y"
{
            token palabra = (token) val_peek(3).obj; 
            token_function tf = (token_function) lexer.getTablaSimbolos().get(generarNombreConStack(palabra.getKey()));
            List<String> lista = new ArrayList<String>();

            if(!polaca_map.containsKey(val_peek(3).sval)){
                lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Función " + val_peek(3).sval + " no declarada.");
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
                lista.addAll((List<String>) val_peek(1).obj);  
                lista.add("=");
                lista.add("CALL");
                lista.add(val_peek(3).sval);
            }
  

            yyval = new ParserVal(lista); }
break;
case 97:
//#line 438 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Falta argumento en llamado de función."); 
                        yyval = new ParserVal(new ArrayList<String>());}
break;
case 98:
//#line 444 "test.y"
{token palabra = (token) val_peek(0).obj;
            palabra.setUso("parametro");
            palabra.setTipo(val_peek(1).sval);
            yyval = new ParserVal(palabra);
            yyval.sval = palabra.getKey(); }
break;
case 99:
//#line 449 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el nombre de parametro formal en la declaracion de la funcion"); }
break;
case 100:
//#line 450 "test.y"
{ lexer.getErrores().add("Error en línea " + lexer.getLineaActual() +  ": Falta el tipo de parametro formal en la declaracion de la funcion"); }
break;
case 101:
//#line 451 "test.y"
{ lexer.getErrores().add("PARAMETRO NO VALIDO"); }
break;
case 102:
//#line 455 "test.y"
{yyval = new ParserVal(val_peek(0).sval); 
        if (lexer.getTablaSimbolos().get(val_peek(0).sval).getUso() != "tipo"){
            lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Tipo " + val_peek(0).sval + " no valido");
        }
        }
break;
case 103:
//#line 460 "test.y"
{ yyval = new ParserVal(val_peek(0).sval); }
break;
case 104:
//#line 461 "test.y"
{yyval = new ParserVal(val_peek(0).sval); }
break;
case 105:
//#line 462 "test.y"
{yyval = new ParserVal(val_peek(0).sval); }
break;
case 106:
//#line 463 "test.y"
{yyval = new ParserVal(val_peek(0).sval); }
break;
case 113:
//#line 476 "test.y"
{yyval.sval = "=";}
break;
case 114:
//#line 477 "test.y"
{yyval.sval = "!=";}
break;
case 115:
//#line 478 "test.y"
{yyval.sval = ">";}
break;
case 116:
//#line 479 "test.y"
{yyval.sval = "<";}
break;
case 117:
//#line 480 "test.y"
{yyval.sval = ">=";}
break;
case 118:
//#line 481 "test.y"
{yyval.sval = "<=";}
break;
case 119:
//#line 482 "test.y"
{ lexer.getErrores().add("Error en línea " 
                + lexer.getLineaActual() + ":falta comparador"); }
break;
case 120:
//#line 487 "test.y"
{ yyval = val_peek(1); }
break;
case 121:
//#line 488 "test.y"
{List<String> lista = new ArrayList<String>();
                        lista.addAll((List<String>) val_peek(2).obj);  
                        for(String op : (List<String>) val_peek(2).obj){
                            agregar_a_polaca(op);
                        }
                        for(String op : (List<String>) val_peek(0).obj){
                            agregar_a_polaca(op);
                        }
                        agregar_a_polaca(val_peek(1).sval);
                        yyval.ival = polaca_map.get(actual).size();
                        agregar_a_polaca("");
                        agregar_a_polaca("bf");
                        }
break;
case 122:
//#line 501 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Error en la condicion");}
break;
case 123:
//#line 502 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Error en la condicion, falta comparador");}
break;
case 124:
//#line 507 "test.y"
{val_peek(7).ival = i; alcance.push("for"+ String.valueOf(polaca_map.get(actual).size())); }
break;
case 125:
//#line 507 "test.y"
{
                    alcance.pop();
                    List<String> lista = (List<String>) val_peek(3).obj;
                    agregar_a_polaca(val_peek(7).sval);
                    for(String op : lista){
                        agregar_a_polaca(op);
                    }
                    agregar_a_polaca(String.valueOf(val_peek(9).ival));
                    agregar_a_polaca("bi");
                    polaca_map.get(actual).set(val_peek(5).ival, String.valueOf(polaca_map.get(actual).size()));
                    }
break;
case 126:
//#line 519 "test.y"
{ alcance.push("for"+ String.valueOf(polaca_map.get(actual).size())); val_peek(9).ival = i;}
break;
case 127:
//#line 519 "test.y"
{
                                            alcance.pop();
                                            List<String> lista = (List<String>) val_peek(5).obj;
                                            agregar_a_polaca(val_peek(9).sval);
                                            for(String op : lista){
                                                agregar_a_polaca(op);
                                            }
                                            agregar_a_polaca(String.valueOf(val_peek(11).ival));
                                            agregar_a_polaca("bi");
                                            polaca_map.get(actual).set(val_peek(3).ival, String.valueOf(polaca_map.get(actual).size()));
                                            polaca_map.get(actual).set(val_peek(7).ival, String.valueOf(polaca_map.get(actual).size()));
                                           }
break;
case 128:
//#line 531 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + 
                                                ":Falta parentesis en la condicion del for");}
break;
case 129:
//#line 533 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual()
                                                + ": condicion del for mal definida");}
break;
case 130:
//#line 535 "test.y"
{lexer.getErrores().add("Error en línea "
                                                + lexer.getLineaActual() + ":Falta ; en for");}
break;
case 131:
//#line 537 "test.y"
{lexer.getErrores().add("Error en línea "
                                                + lexer.getLineaActual() + ":Falta ; en for");}
break;
case 132:
//#line 542 "test.y"
{yyval = val_peek(2);}
break;
case 133:
//#line 543 "test.y"
{yyval = val_peek(0);}
break;
case 134:
//#line 544 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ":Falta el cuerpo del for");}
break;
case 135:
//#line 549 "test.y"
{agregar_a_polaca(val_peek(2).sval);
                    agregar_a_polaca(val_peek(0).sval);
                    agregar_a_polaca("ASSING");
                    lexer.getTablaSimbolos().put(val_peek(2).sval, new token(val_peek(2).sval, "ulongint", ID));
                    yyval = new ParserVal(val_peek(2).sval);}
break;
case 136:
//#line 554 "test.y"
{
                    String id = generarNombreConStack(val_peek(0).sval);
                    if (id.contains("|POSITION")) {
                        id = id.substring(0, id.indexOf("|POSITION"));
                    }

                    if(!validar_alcance(id)){
                        lexer.getErrores().add("Error en línea " + lexer.getLineaActual() + ": Variable " + val_peek(0).sval + " no declarada en el alcance actual.");
                    }
                    agregar_a_polaca(val_peek(2).sval);
                    agregar_a_polaca(val_peek(0).sval);
                    agregar_a_polaca("ASSING");
                    lexer.getTablaSimbolos().put(val_peek(2).sval, new token(val_peek(2).sval, "ulongint", ID));
                    yyval = new ParserVal(val_peek(2).sval);}
break;
case 137:
//#line 571 "test.y"
{List<String> lista = new ArrayList<String>();
            lista.add(val_peek(0).sval);
            lista.add("+");
            yyval= new ParserVal(lista);}
break;
case 138:
//#line 576 "test.y"
{
                List<String> lista = new ArrayList<String>();
                lista.add(val_peek(0).sval);
                lista.add("-");
                yyval= new ParserVal(lista);}
break;
case 139:
//#line 581 "test.y"
{lexer.getErrores().add("Error en línea " + lexer.getLineaActual() 
                        + ":incremento/decremento del bucle mal definido");}
break;
//#line 1736 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
