package Scanner;

import AST.*;
import Parser.*;

%%

%class Scanner
%public
%7bit
%pack

%cup

%line
%column

%{
  public static String curLine = "";
  public static int lineCount = 0;	
  public static boolean debug = false;

  public void addToLine(String s, int line) {
    if (line != lineCount) 
      curLine = s;
    else
      curLine = curLine + s;
    lineCount = line;
  }


  private java_cup.runtime.Symbol token(int kind) {
    Token t;
    addToLine(yytext(), yyline+1);
    t = new Token(kind, yytext(), yyline+1, yycolumn+1, yycolumn + yylength());
    if (debug)
      System.out.println(t);
    return new java_cup.runtime.Symbol(kind, t);
  } 
%}


/* Main Character Classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]
//tab        = [\t]


/* Comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment} | {EndOfFileComment}

TraditionalComment = "/*" [^*] {CommentContent} \*+ "/"
UnterminatedComment = "/*" [^*] {CommentContent} \** "/"?
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} \*+ "/"
EndOfFileComment = "//" {InputCharacter}* 

CommentContent = ( [^*] | \*+[^*/] )*

/* Identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* Integer Literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]
 
HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]
 
OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]      

/* Floating Point Literals */
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}|{FLit4}) [fF]
DoubleLiteral = {FLit1}|{FLit2}|{FLit3}|{FLit4} 
 
FLit1 = [0-9]+ \. [0-9]* {Exponent}?
FLit2 = \. [0-9]+ {Exponent}?
FLit3 = [0-9]+ {Exponent}
FLit4 = [0-9]+ {Exponent}?
 
Exponent = [eE] [+\-]? [0-9]+       
 
/* String Literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]
StringEscape  =   \\([btnfr\"\'\\]|[0-3]?{OctDigit}?{OctDigit}|u{HexDigit}{HexDigit}{HexDigit}{HexDigit}) 

%%

  /* Atomic Types */
  "boolean"   { return token(sym.BOOLEAN); } 
  "byte"      { return token(sym.BYTE); }
  "short"     { return token(sym.SHORT); }
  "int"       { return token(sym.INT); }
  "long"      { return token(sym.LONG); }
  "float"     { return token(sym.FLOAT); }
  "double"    { return token(sym.DOUBLE); }
  "char"      { return token(sym.CHAR); }
  "string"    { return token(sym.STRING); }
  "void"      { return token(sym.VOID); }

  /* Channel related keywords */
  "chan"      { return token(sym.CHAN); }
  "read"      { return token(sym.READ); }
  "write"     { return token(sym.WRITE); }
  "shared"    { return token(sym.SHARED); }
  "claim"     { return token(sym.CLAIM); }

  /* Barrier related keywords */
  "barrier"   { return token(sym.BARRIER); }
  "sync"      { return token(sym.SYNC); }
  "enroll"    { return token(sym.ENROLL); }

  /* Timer related keywords */
  "timer"     { return token(sym.TIMER); }
  "timeout"   { return token(sym.TIMEOUT); }

  /* Silly keywords */
  "skip"      { return token(sym.SKIP); }
  "stop"      { return token(sym.STOP); }

  /* Control flow related keywords */
  "if"        { return token(sym.IF); }
  "else"      { return token(sym.ELSE); }
  "for"       { return token(sym.FOR); }
  "while"     { return token(sym.WHILE); }
  "switch"    { return token(sym.SWITCH); }
  "case"      { return token(sym.CASE); }
  "do"        { return token(sym.DO); }
  "default"   { return token(sym.DEFAULT); }
  "break"     { return token(sym.BREAK); }
  "continue"  { return token(sym.CONTINUE); }
  "return"    { return token(sym.RETURN); }

  /* Other process oriented programming related keywords */
  "seq"       { return token(sym.SEQ); }
  "par"       { return token(sym.PAR); }
  "pri"       { return token(sym.PRI); }
  "alt"       { return token(sym.ALT); }

  /* Other non process oriented programming related keywords */
  "new"       { return token(sym.NEW); }  

  /* Mobile processes related keywords */
  "resume"    { return token(sym.RESUME); }
  "suspend"   { return token(sym.SUSPEND); }
  "with"      { return token(sym.WITH); }

  /* Top level element related keywords */
  "proc"      { return token(sym.PROC); }
  "protocol"  { return token(sym.PROTOCOL); }
  "record"    { return token(sym.RECORD); }
  "extends"   { return token(sym.EXTENDS); }
  "implements" { return token(sym.IMPLEMENTS); }

  /* Package related keywords */
  "package"   { return token(sym.PACKAGE); }
  "import"    { return token(sym.IMPORT); }

  /* Modifiers */
  "mobile"    { return token(sym.MOBILE); }
  "native"    { return token(sym.NATIVE); } 
  "public"    { return token(sym.PUBLIC); }
  "private"   { return token(sym.PRIVATE); }
  "protected" { return token(sym.PROTECTED); }
  "const"     { return token(sym.CONST); }
  "extern"    { return token(sym.EXTERN); }

  /* Boolean Literals */
  "true"      { return token(sym.BOOLEAN_LITERAL); } 
  "false"     { return token(sym.BOOLEAN_LITERAL); } 

  /* Other stuff */
  "#pragma"    { return token(sym.PRAGMA); }

  /* Parentheses */
  "("         { return token(sym.LPAREN); } 
  ")"         { return token(sym.RPAREN); }  
  "{"         { return token(sym.LBRACE); }  
  "}"         { return token(sym.RBRACE); }  
  "["         { return token(sym.LBRACK); }
  "]"         { return token(sym.RBRACK); }

  /* Separators */
  ";"         { return token(sym.SEMICOLON); }  
  ","         { return token(sym.COMMA); }  

  /* Assignment Operators */
  "="         { return token(sym.EQ); }  
  "*="        { return token(sym.MULTEQ); } 
  "/="        { return token(sym.DIVEQ); } 
  "%="        { return token(sym.MODEQ); } 
  "+="        { return token(sym.PLUSEQ); } 
  "-="        { return token(sym.MINUSEQ); } 
  "<<="       { return token(sym.LSHIFTEQ); } 
  ">>="       { return token(sym.RSHIFTEQ); } 
  ">>>="      { return token(sym.RRSHIFTEQ); } 
  "&="        { return token(sym.ANDEQ); } 
  "^="        { return token(sym.XOREQ); } 
  "|="        { return token(sym.OREQ); }  

  /* Relational Operators */
  ">"         { return token(sym.GT); }
  "<"         { return token(sym.LT); } 
  "=="        { return token(sym.EQEQ); } 
  "<="        { return token(sym.LTEQ); } 
  ">="        { return token(sym.GTEQ); } 
  "!="        { return token(sym.NOTEQ); } 

  /* Binary Operators (Some Unary: +, -) */

  "<<"        { return token(sym.LSHIFT); } 
  ">>"        { return token(sym.RSHIFT); } 
  ">>>"       { return token(sym.RRSHIFT); } 
  "&&"        { return token(sym.ANDAND); } 
  "||"        { return token(sym.OROR); } 
  "+"         { return token(sym.PLUS); } 
  "-"         { return token(sym.MINUS); } 
  "*"         { return token(sym.MULT); } 
  "/"         { return token(sym.DIV); } 
  "&"         { return token(sym.AND); } 
  "|"         { return token(sym.OR); } 
  "^"         { return token(sym.XOR); } 
  "%"         { return token(sym.MOD); } 

  /* Unary Operators */
  "!"         { return token(sym.NOT); } 
  "~"         { return token(sym.COMP); } 
  "++"        { return token(sym.PLUSPLUS); } 
  "--"        { return token(sym.MINUSMINUS); } 

  /* Other stuff */
  "?"         { return token(sym.QUEST); }
  "::"        { return token(sym.COLONCOLON); }
  ":"         { return token(sym.COLON); }
  "."         { return token(sym.DOT); }  
  
  /* Numeric literals */
  {DecIntegerLiteral}            { return token(sym.INTEGER_LITERAL); }
  {DecLongLiteral}               { return token(sym.LONG_LITERAL); }  
 
  {HexIntegerLiteral}            { return token(sym.INTEGER_LITERAL); }
  {HexLongLiteral}               { return token(sym.LONG_LITERAL); }
 
  {OctIntegerLiteral}            { return token(sym.INTEGER_LITERAL); }
  {OctLongLiteral}               { return token(sym.LONG_LITERAL); }
 
  {FloatLiteral}                 { return token(sym.FLOAT_LITERAL); }
  {DoubleLiteral}                { return token(sym.DOUBLE_LITERAL); }
  {DoubleLiteral}[dD]            { return token(sym.DOUBLE_LITERAL); } 

  /* String Literals */

  \"({StringCharacter})*\"       { return token(sym.STRING_LITERAL); }
  \"({StringCharacter})*{LineTerminator}
                               { throw new RuntimeException("Unterminated string at end-of-line \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }

  /* Character Literal */
  \'{SingleCharacter}\'          { return token(sym.CHARACTER_LITERAL); }
  \'{StringEscape}\'             { return token(sym.CHARACTER_LITERAL); }
  \'{SingleCharacter}?{LineTerminator} 
                                 { throw new RuntimeException("Unterminated character at end-of-line \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }
  \'{StringEscape}?{LineTerminator} 
                                 { throw new RuntimeException("Unterminated character at end-of-line \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }   

  /* Comments */
  {Comment}                      { addToLine(yytext(), yyline+1); }
  {UnterminatedComment}	         { throw new RuntimeException("Unterminated comment at EOF at line "+(yyline+1)+", column "+(yycolumn+1)); }

  /* Whitespace */
  {WhiteSpace}                 { addToLine(yytext(), yyline+1); 
	//if (yytext().equals("\t")) yycolumn += 6; System.out.println(":::'" + yytext()+"'"); 
	}
//  {tab}                          { addToLine("    ", yyline+1);  yycolumn += 5; }

  /* Identifiers */ 
  {Identifier}                   { return token(sym.IDENTIFIER); } 

  /* error fallback */
  .|\n                           { throw new RuntimeException("Illegal character \""+yytext()+"\" at line "+(yyline+1)+", column "+(yycolumn+1)); }
