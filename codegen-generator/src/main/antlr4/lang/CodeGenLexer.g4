lexer grammar CodeGenLexer;

@lexer::header {
import de.chrlembeck.codegen.generator.GeneratorException;
}

@lexer::members {
// @lexer::members
/**
 * Hält fest, ob sich der Lexer gerade innerhalb eines Template-Statements befindet oder nicht.
 */
private boolean insideTemplate = false;

private void notifySyntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
        final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
    getErrorListeners().stream().forEach(
            listener -> listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
}
// @lexer::members
}

/**
 * Die doppelte spitze Klammer nach links definiert den Beginn eines Statements innerhalb der Template-Datei.
 * Wird sie erkannt, wird in den STATEMENT_MODE gewechselt.
 */
LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK    : '\u00ab' -> pushMode(STATEMENT_MODE); // «

/**
 * Außerhalb von Statements in doppelten spitzen Klammern und Außerhalb von Template-Definitionen dürfen
 * keine Zeichen außer Whitespace stehen. Wird doch etwas gefunden, was kein Whitespace ist,
 * erzeugen wir einen Syntax-Fehler.
 */
AnyChar : ~'\u00ab'+ { if (!insideTemplate && getText().trim().length() > 0) {
                           notifySyntaxError(this, getText(), _tokenStartLine, _tokenStartCharPositionInLine, "Unbekannte Zeichen.", null);
	                   }
                     };       

/**
 * Kommentare durfen beliebige Zeichen enthalten. 
 */
BlockComment : '\u00abCOMMENT\u00bb' .*? '\u00abENDCOMMENT\u00bb';

/**
 * Mode für die Elemente innerhalb eines Import-Statements.
 */
mode IMPORT_MODE;
    
/**
 * Alle Zeichen, die zur Definition eines URI erlaubt sind.
 */    
IURI : ([a-zA-Z0-9$_^:;/?#@=!~(),%+\\-] | '.' | '[' | ']')+ -> popMode;

/**
 * Whitespace innerhalb des IMPORT-Statements.
 */
IWS  : [ \t\r\n\u000C]+ -> channel(HIDDEN);

/**
 * Alle anderen Zeichen in den HIDDEN-channel, damit der Editor mit der Darstellung der Tokens
 * nicht aus dem Tritt kommt...
 */
IERR_CHAR : .+? { notifySyntaxError(this, getText(), _tokenStartLine, _tokenStartCharPositionInLine, "Unbekannte Zeichen.", null);
                } -> channel(HIDDEN) ;

/**
 * Innerhalb von durch Klammern definierten Statements gilt eine eigene Logik,
 * also gibt es dafür einen eigenen MODE. 
 */               
mode STATEMENT_MODE;

/*
 * Die Keywords.
 */
SEPARATOR     : 'SEPARATOR';
IMPORT        : 'IMPORT';
TEMPLATE      : 'TEMPLATE'    {insideTemplate = true;};  // in Templates ist beliebiger Text erlaubt
ENDTEMPLATE   : 'ENDTEMPLATE' {insideTemplate = false;}; // außerhalb nur Whitespace
OUTPUT        : 'OUTPUT';
ENDOUTPUT     : 'ENDOUTPUT';
EXEC          : 'EXEC';
IF            : 'IF';
ENDIF         : 'ENDIF';
ELSE          : 'ELSE';
FOREACH       : 'FOREACH';
ENDFOREACH    : 'ENDFOREACH';
FROM          : 'FROM';
COUNTER       : 'COUNTER';
FOR           : 'FOR';
BOOLEAN       : 'boolean';
BYTE          : 'byte';
CHAR          : 'char';
CLASS         : 'class';
DOUBLE        : 'double';
FLOAT         : 'float';
INSTANCEOF    : 'instanceof';
INT           : 'int';
LONG          : 'long';
PUBLIC        : 'public';
SHORT         : 'short';
SUPER         : 'super';
THIS          : 'this';
VOID          : 'void';
/**
 * Wird nur im Import-Statement verwendet. Da hier eine URI verwendet werden kann, welche 'etwas schwieriger'
 * zu lexen ist...
 */
AS            : 'AS'-> pushMode(IMPORT_MODE);


/**
 * IntegerLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.1
 */
IntegerLiteral
    : DecimalIntegerLiteral
    | HexIntegerLiteral
    | OctalIntegerLiteral
    | BinaryIntegerLiteral
    ;

/**
 * DecimalIntegerLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-DecimalIntegerLiteral
 */
fragment
DecimalIntegerLiteral
    : DecimalNumeral IntegerTypeSuffix?
    ;

/**
 * HexIntegerLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexIntegerLiteral
 */
fragment
HexIntegerLiteral
    : HexNumeral IntegerTypeSuffix?
    ;

/**
 * OctalIntegerLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalIntegerLiteral
 */
fragment
OctalIntegerLiteral
    : OctalNumeral IntegerTypeSuffix?
    ;

/**
 * BinaryIntegerLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryIntegerLiteral
 */
fragment
BinaryIntegerLiteral
    : BinaryNumeral IntegerTypeSuffix?
    ;

/**
 * IntegerTypeSuffix aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-IntegerTypeSuffix
 */
fragment
IntegerTypeSuffix
    : [lL]
    ;

/**
 * DecimalNumeral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-DecimalNumeral
 */
fragment
DecimalNumeral
    : '0'
    | NonZeroDigit (Digits? | Underscores Digits)
    ;

/**
 * Digits aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-Digits
 */
fragment
Digits
    : Digit (DigitOrUnderscore* Digit)?
    ;

/**
 * Digit aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-Digit
 */
fragment
Digit
    : '0'
    | NonZeroDigit
    ;

/**
 * NonZeroDigit aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-NonZeroDigit
 */
fragment
NonZeroDigit
    : [1-9]
    ;

/**
 * DigitOrUnderscore aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-DigitOrUnderscore
 */
fragment
DigitOrUnderscore
    : Digit
    | '_'
    ;

/**
 * Underscores aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-Underscores
 */
fragment
Underscores
    : '_'+
    ;

/**
 * HexNumeral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexNumeral
 */
fragment
HexNumeral
    : '0' [xX] HexDigits
    ;

/**
 * HexDigits aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexDigits
 */
fragment
HexDigits
    : HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

/**
 * HexDigit aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexDigit
 */
fragment
HexDigit
    : [0-9a-fA-F]
    ;

/**
 * HexDigitOrUnderscore aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexDigitOrUnderscore
 */
fragment
HexDigitOrUnderscore
    : HexDigit
    | '_'
    ;

/**
 * OctalNumeral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalNumeral
 */
fragment
OctalNumeral
    : '0' Underscores? OctalDigits
    ;

/**
 * OctalDigits aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalDigits
 */
fragment
OctalDigits
    : OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

/**
 * aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalDigit
 */
fragment
OctalDigit
    : [0-7]
    ;

/**
 * OctalDigitOrUnderscore aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalDigitOrUnderscore
 */
fragment
OctalDigitOrUnderscore
    : OctalDigit
    | '_'
    ;

/**
 * BinaryNumeral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryNumeral
 */
fragment
BinaryNumeral
    : '0' [bB] BinaryDigits
    ;

/**
 * BinaryDigits aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryDigits
 */
fragment
BinaryDigits
    : BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

/**
 * BinaryDigit aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryDigit
 */
fragment
BinaryDigit
    : [01]
    ;

/**
 * BinaryDigitOrUnderscore aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryDigitOrUnderscore
 */
fragment
BinaryDigitOrUnderscore
    : BinaryDigit
    | '_'
    ;

/**
 * FloatingPointLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-FloatingPointLiteral
 */
FloatingPointLiteral
    : DecimalFloatingPointLiteral
    | HexadecimalFloatingPointLiteral
    ;

/** 
 * DecimalFloatingPointLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-DecimalFloatingPointLiteral
 */
fragment
DecimalFloatingPointLiteral
    : Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    | '.' Digits ExponentPart? FloatTypeSuffix?
    | Digits ExponentPart FloatTypeSuffix?
    | Digits FloatTypeSuffix
    ;

/**
 * ExponentPart aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-ExponentPart
 */
fragment
ExponentPart
    : ExponentIndicator SignedInteger
    ;

/** 
 * ExponentIndicator aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-ExponentIndicator
 */
fragment
ExponentIndicator
    : [eE]
    ;

/** 
 * SignedInteger aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-SignedInteger
 */
fragment
SignedInteger
    : Sign? Digits
    ;

/**
 * Sign aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-Sign
 */
fragment
Sign
    : [+-]
    ;

/**
 * FloatTypeSuffix aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-FloatTypeSuffix
 */
fragment
FloatTypeSuffix
    : [fFdD]
    ;

/** 
 * HexadecimalFloatingPointLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexadecimalFloatingPointLiteral
 */
fragment
HexadecimalFloatingPointLiteral
    : HexSignificand BinaryExponent FloatTypeSuffix?
    ;

/** 
 * HexSignificand aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-HexSignificand
 */
fragment
HexSignificand
    : HexNumeral '.'?
    | '0' [xX] HexDigits? '.' HexDigits
    ;

/** 
 * BinaryExponent aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryExponent
 */
fragment
BinaryExponent
    : BinaryExponentIndicator SignedInteger
    ;

/** 
 * BinaryExponentIndicator aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BinaryExponentIndicator
 */
fragment
BinaryExponentIndicator
    : [pP]
    ;


/**
 * BooleanLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-BooleanLiteral
 */
BooleanLiteral
    : 'true'
    | 'false'
    ;

/**
 * CharacterLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-CharacterLiteral
 */
CharacterLiteral
    : '\'' SingleCharacter '\''
    | '\'' EscapeSequence '\''
    ;

/**
 * SingleCharacter aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-SingleCharacter
 */
fragment
SingleCharacter
    : ~['\\]
    ;

/**
 * StringLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-StringLiteral
 */
StringLiteral
    : '"' StringCharacters? '"'
    ;

/**
 * StringCharacters aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-StringLiteral
 */
fragment
StringCharacters
    : StringCharacter+
    ;

/**
 * StringCharacter aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-StringCharacter
 */
fragment
StringCharacter
    : ~["\\]
    | EscapeSequence
    ;

/**
 * EscapeSequence aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-EscapeSequence
 */
fragment
EscapeSequence
    : '\\' [btnfr"'\\]
    | OctalEscape
    | UnicodeEscape
    ;

/**
 * OctalEscape aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-OctalEscape
 */
fragment
OctalEscape
    : '\\' OctalDigit
    | '\\' OctalDigit OctalDigit
    | '\\' ZeroToThree OctalDigit OctalDigit
    ;

/**
 * UnicodeEscape aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-UnicodeEscape
 */
fragment
UnicodeEscape
    : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

/**
 * ZeroToThree aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-ZeroToThree
 */
fragment
ZeroToThree
    : [0-3]
    ;

/**
 * NullLiteral aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-NullLiteral
 */
NullLiteral
    : 'null'
    ;

/*
 * Separators
 */
RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK : '\u00bb' -> popMode; // »
LEFT_PARENTHESES                           : '(';
RIGHT_PARENTHESES                          : ')';
LEFT_BRACKET                               : '[';
RIGHT_BRACKET                              : ']';
SEMI                                       : ';';
COMMA                                      : ',';
DOT                                        : '.';

/*
 * Operators 
 */
ASSIGN    : '=';
GT        : '>';
LT        : '<';
BANG      : '!';
TILDE     : '~';
QUESTION  : '?';
COLON     : ':';
EQUAL     : '==';
LE        : '<=';
GE        : '>=';
NOTEQUAL  : '!=';
AND       : '&&';
OR        : '||';
INC       : '++';
DEC       : '--';
ADD       : '+';
SUB       : '-';
MUL       : '*';
DIV       : '/';
BITAND    : '&';
BITOR     : '|';
XOR       : '^';
MOD       : '%';

/**
 * Identifier aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8
 */
Identifier
    :   JavaLetter JavaLetterOrDigit*
    ;

/**
 * JavaLetter aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-JavaLetter
 */
fragment
JavaLetter
    :   [a-zA-Z$_] 
    |   ~[\u0000-\u007F\uD800-\uDBFF\u00ab\u00bb]
    |   [\uD800-\uDBFF] [\uDC00-\uDFFF]
    ;

/**
 * JavaLetterOrDigit aus der java language specification
 * Siehe auch: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-JavaLetterOrDigit
 */
fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_]
    |   ~[\u0000-\u007F\uD800-\uDBFF\u00ab\u00bb]
    |   [\uD800-\uDBFF] [\uDC00-\uDFFF]
    ;

/**
 *  Whitespace
 */
WS  :  [ \t\r\n\u000C]+ -> channel(HIDDEN);

/**
 *  Alle anderen Zeichen in den HIDDEN-channel, damit der Editor mit der Darstellung der Tokens nicht aus dem Tritt kommt...
 */
ERR_CHAR : .+? { notifySyntaxError(this, getText(), _tokenStartLine, _tokenStartCharPositionInLine, "Unbekannte Zeichen!", null);
               } -> channel(HIDDEN) ;