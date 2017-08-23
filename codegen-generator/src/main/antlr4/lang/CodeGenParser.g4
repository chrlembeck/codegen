parser grammar CodeGenParser;

options {
	tokenVocab = CodeGenLexer;
}

/*
 * Root-Element der Grammatik. Beschreibt den kompletten Aufbau einer Template-Datei.
 */
templateFile
    : (AnyChar | importStatement | commentStatement)* 
      (AnyChar | templateStatement | commentStatement)* EOF
    ;
    
/* 
 * Definition eines einzelnen Import-Statements.
 */
importStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK IMPORT prefix=Identifier AS url=IURL RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;

/* 
 * Definition eines Template-Statements.
 */
templateStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK TEMPLATE Identifier FOR classOrInterfaceType RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK 
          userCodeOrStatements 
      LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK ENDTEMPLATE RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;        

/* 
 * Liste von aufeinander folgenden userCode-Blöcken oder statements.
 */    
userCodeOrStatements
    : (userCode | statements)*
    ;    

/* 
 * Als UserCode sind beliebige Eingaben erlaubt.
 */    
userCode:
    AnyChar
    ;

/*
 * Liste der in einem Template erlaubten Statements.
 */        
statements
    : outputStatement
    | executeStatement
    | ifStatement
    | forStatement
    | commentStatement
    | expressionStatement
    ;
    
/*
 * Output-Statements steuern die Ausgabe der Artefakte in Ausgabeströme.
 */    
outputStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK OUTPUT expression RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK userCodeOrStatements LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK ENDOUTPUT RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;
   
/*
 * Execute-Statements rufen weitere auszuführende Templates auf.
 */
executeStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK EXEC (prefix=Identifier DOT)? templateName=Identifier 
         (FOR|FOREACH) expression (SEPARATOR expression)? RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;    

/*
 * If-Statement zur Realisierung von bedingten Verzweigungen
 */
ifStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK IF expression RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK userCodeOrStatements (LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK ELSE RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK userCodeOrStatements)? LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK ENDIF RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;
    
/*
 * For-Statements erlauben die Ausführung von Schleifen, die über Collections iterieren.
 */    
forStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK FOREACH Identifier FROM expression (COUNTER Identifier)? (SEPARATOR expression)? RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK userCodeOrStatements LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK ENDFOREACH RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;    

/*
 * Kommentare, die sich über eine beliebige Menge an Zeichen strecken dürfen. 
 */
commentStatement
    : BlockComment
    ;

/*
 * Expression Statements enthalten Java-ähnliche Ausdrücke, welche bei der Generierung ausgewertet werden.
 */        
expressionStatement
    : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK expression RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    ;

/*
 * Enthält eine Typdefinition entweder eines primitiven Datentyps oder in Form eines
 * qualifizierten Klassenbezeichners.
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.16
 */
classOrPrimitiveType
    : classOrInterfaceType (arr+=LEFT_BRACKET RIGHT_BRACKET)*   # typeClassOrInterface
    | primitiveType (arr+=LEFT_BRACKET RIGHT_BRACKET)*          # typePrimitive
    ;

/*
 * Entspricht einem voll qualifizierten Klassennamen.
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-ClassOrInterfaceType
 */
classOrInterfaceType
    : Identifier (DOT Identifier )*
    ;

/*
 * Bezeichner für die primitiven Datentypen.
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-PrimitiveType
 */
primitiveType
    : type=BOOLEAN
    | type=CHAR
    | type=BYTE
    | type=SHORT
    | type=INT
    | type=LONG
    | type=FLOAT
    | type=DOUBLE
    ;

/*
 * Liste der möglichen Literale. 
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-Literal
 */
literal
    : IntegerLiteral
    | FloatingPointLiteral
    | CharacterLiteral
    | StringLiteral
    | BooleanLiteral
    | NullLiteral
    ;

/*
 * Liste von durch Kommata separierten Expressions. Wird z.B. für Methodenaufrufe verwendet.
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-ArgumentList
 */
expressionList
    : expression (COMMA expression)*
    ;

/*
 * Mögliche Expressions, die als Java-ähnliche Ausdrücke in der Template-Datei verwendet werden können. 
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2
 */
expression
    : primary                                                            # expressionPrimary
    | expression DOT Identifier                                          # expressionAttribute
    | expression DOT THIS                                                # expressionThis
    | expression DOT SUPER DOT Identifier arguments?                     # expressionSuper
    | expression LEFT_BRACKET expression RIGHT_BRACKET                   # expressionArrayAccess
    | expression LEFT_PARENTHESES expressionList? RIGHT_PARENTHESES      # expressionMethodCall
    | LEFT_PARENTHESES classOrPrimitiveType RIGHT_PARENTHESES expression # expressionCast
    | operator=(ADD | SUB) expression                                    # expressionSign
    | operator=(TILDE | BANG) expression                                 # expressionNeg
    | expression operator=(MUL | DIV | MOD) expression                   # expressionMultDivMod
    | expression operator=(ADD | SUB) expression                         # expressionPlusMinus
    | expression (op+=LT op+=LT 
    	           | op+=GT op+=GT op+=GT 
    	           | op+=GT op+=GT
                 ) expression                                            # expressionShift
    | expression operator=(LE | GE | GT | LT) expression                 # expressionCompare
    | expression INSTANCEOF classOrPrimitiveType                         # expressionInstanceof
    | expression operator=(EQUAL | NOTEQUAL) expression                  # expressionEquals
    | expression BITAND expression                                       # expressionAnd
    | expression XOR expression                                          # expressionXor
    | expression BITOR expression                                        # expressionOr
    | expression AND expression                                          # expressionConditionalAnd
    | expression OR expression                                           # expressionConditionalOr
    | condition=expression QUESTION 
          ifExpression=expression COLON 
          elseExpression=expression                                      # expressionConditional
    ;

/*
 * Triviale Expressions
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.8 
 */
primary
    : LEFT_PARENTHESES expression RIGHT_PARENTHESES    # primarySubExpression
    | THIS                                             # primaryThisReference
    | SUPER                                            # primarySuperReference
    | literal                                          # primaryLiteral
    | Identifier                                       # primaryIdentifier
    | classOrPrimitiveType DOT CLASS                               # primaryTypeClass
    | VOID DOT CLASS                                   # primaryVoidClass 
    ;

/*
 * Definition von Methodenparametern mit Klammern drum herum.
 */    
arguments
    : LEFT_PARENTHESES expressionList? RIGHT_PARENTHESES
    ;