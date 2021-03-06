/* Generated By:JJTree&JavaCC: Do not edit this line. TransformLangParserConstants.java */
package org.jetel.interpreter;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface TransformLangParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLE_LINE_COMMENT = 9;
  /** RegularExpression Id. */
  int INTEGER_LITERAL = 10;
  /** RegularExpression Id. */
  int DIGIT = 11;
  /** RegularExpression Id. */
  int LETTER = 12;
  /** RegularExpression Id. */
  int UNDERSCORE = 13;
  /** RegularExpression Id. */
  int HEX_LITERAL = 14;
  /** RegularExpression Id. */
  int OCTAL_LITERAL = 15;
  /** RegularExpression Id. */
  int FLOATING_POINT_LITERAL = 16;
  /** RegularExpression Id. */
  int EXPONENT = 17;
  /** RegularExpression Id. */
  int STRING_LITERAL = 18;
  /** RegularExpression Id. */
  int DQUOTED_STRING = 19;
  /** RegularExpression Id. */
  int UNTERMINATED_STRING_LITERAL = 20;
  /** RegularExpression Id. */
  int UNTERMINATED_DQUOTED_STRING = 21;
  /** RegularExpression Id. */
  int BOOLEAN_LITERAL = 22;
  /** RegularExpression Id. */
  int TRUE = 23;
  /** RegularExpression Id. */
  int FALSE = 24;
  /** RegularExpression Id. */
  int DATE_LITERAL = 25;
  /** RegularExpression Id. */
  int DATETIME_LITERAL = 26;
  /** RegularExpression Id. */
  int SEMICOLON = 27;
  /** RegularExpression Id. */
  int BLOCK_START = 28;
  /** RegularExpression Id. */
  int BLOCK_END = 29;
  /** RegularExpression Id. */
  int NULL_LITERAL = 30;
  /** RegularExpression Id. */
  int STRING_PLAIN_LITERAL = 31;
  /** RegularExpression Id. */
  int MAPPING = 32;
  /** RegularExpression Id. */
  int OR = 33;
  /** RegularExpression Id. */
  int AND = 34;
  /** RegularExpression Id. */
  int NOT = 35;
  /** RegularExpression Id. */
  int EQUAL = 36;
  /** RegularExpression Id. */
  int NON_EQUAL = 37;
  /** RegularExpression Id. */
  int IN_OPER = 38;
  /** RegularExpression Id. */
  int LESS_THAN = 39;
  /** RegularExpression Id. */
  int LESS_THAN_EQUAL = 40;
  /** RegularExpression Id. */
  int GREATER_THAN = 41;
  /** RegularExpression Id. */
  int GREATER_THAN_EQUAL = 42;
  /** RegularExpression Id. */
  int REGEX_EQUAL = 43;
  /** RegularExpression Id. */
  int CMPOPERATOR = 44;
  /** RegularExpression Id. */
  int MINUS = 45;
  /** RegularExpression Id. */
  int PLUS = 46;
  /** RegularExpression Id. */
  int MULTIPLY = 47;
  /** RegularExpression Id. */
  int DIVIDE = 48;
  /** RegularExpression Id. */
  int MODULO = 49;
  /** RegularExpression Id. */
  int INCR = 50;
  /** RegularExpression Id. */
  int DECR = 51;
  /** RegularExpression Id. */
  int TILDA = 52;
  /** RegularExpression Id. */
  int FIELD_ID = 53;
  /** RegularExpression Id. */
  int REC_NAME_FIELD_ID = 54;
  /** RegularExpression Id. */
  int REC_NAME_FIELD_NUM = 55;
  /** RegularExpression Id. */
  int REC_NUM_FIELD_ID = 56;
  /** RegularExpression Id. */
  int REC_NUM_FIELD_NUM = 57;
  /** RegularExpression Id. */
  int REC_NUM_WILDCARD = 58;
  /** RegularExpression Id. */
  int REC_NAME_WILDCARD = 59;
  /** RegularExpression Id. */
  int REC_NUM_ID = 60;
  /** RegularExpression Id. */
  int REC_NAME_ID = 61;
  /** RegularExpression Id. */
  int OPEN_PAR = 62;
  /** RegularExpression Id. */
  int CLOSE_PAR = 63;
  /** RegularExpression Id. */
  int INT_VAR = 64;
  /** RegularExpression Id. */
  int LONG_VAR = 65;
  /** RegularExpression Id. */
  int DATE_VAR = 66;
  /** RegularExpression Id. */
  int DOUBLE_VAR = 67;
  /** RegularExpression Id. */
  int DECIMAL_VAR = 68;
  /** RegularExpression Id. */
  int BOOLEAN_VAR = 69;
  /** RegularExpression Id. */
  int STRING_VAR = 70;
  /** RegularExpression Id. */
  int BYTE_VAR = 71;
  /** RegularExpression Id. */
  int LIST_VAR = 72;
  /** RegularExpression Id. */
  int MAP_VAR = 73;
  /** RegularExpression Id. */
  int RECORD_VAR = 74;
  /** RegularExpression Id. */
  int OBJECT_VAR = 75;
  /** RegularExpression Id. */
  int BREAK = 76;
  /** RegularExpression Id. */
  int CONTINUE = 77;
  /** RegularExpression Id. */
  int ELSE = 78;
  /** RegularExpression Id. */
  int FOR = 79;
  /** RegularExpression Id. */
  int FOR_EACH = 80;
  /** RegularExpression Id. */
  int FUNCTION = 81;
  /** RegularExpression Id. */
  int IF = 82;
  /** RegularExpression Id. */
  int RETURN = 83;
  /** RegularExpression Id. */
  int WHILE = 84;
  /** RegularExpression Id. */
  int CASE = 85;
  /** RegularExpression Id. */
  int ENUM = 86;
  /** RegularExpression Id. */
  int IMPORT = 87;
  /** RegularExpression Id. */
  int SWITCH = 88;
  /** RegularExpression Id. */
  int CASE_DEFAULT = 89;
  /** RegularExpression Id. */
  int DO = 90;
  /** RegularExpression Id. */
  int TRY = 91;
  /** RegularExpression Id. */
  int CATCH = 92;
  /** RegularExpression Id. */
  int RETURN_RECORD_SKIP = 93;
  /** RegularExpression Id. */
  int RETURN_RECORD_SEND_ALL = 94;
  /** RegularExpression Id. */
  int RETURN_RECORD_OK = 95;
  /** RegularExpression Id. */
  int RETURN_RECORD_ERROR = 96;
  /** RegularExpression Id. */
  int YEAR = 97;
  /** RegularExpression Id. */
  int MONTH = 98;
  /** RegularExpression Id. */
  int WEEK = 99;
  /** RegularExpression Id. */
  int DAY = 100;
  /** RegularExpression Id. */
  int HOUR = 101;
  /** RegularExpression Id. */
  int MINUTE = 102;
  /** RegularExpression Id. */
  int SECOND = 103;
  /** RegularExpression Id. */
  int MILLISEC = 104;
  /** RegularExpression Id. */
  int READ_DICT = 105;
  /** RegularExpression Id. */
  int WRITE_DICT = 106;
  /** RegularExpression Id. */
  int DELETE_DICT = 107;
  /** RegularExpression Id. */
  int IDENTIFIER = 108;
  /** RegularExpression Id. */
  int DATE_FIELD_LITERAL = 134;
  /** RegularExpression Id. */
  int ERROR = 135;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int WithinComment = 1;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\n\\r\"",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 8>",
    "<SINGLE_LINE_COMMENT>",
    "<INTEGER_LITERAL>",
    "<DIGIT>",
    "<LETTER>",
    "<UNDERSCORE>",
    "<HEX_LITERAL>",
    "<OCTAL_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<EXPONENT>",
    "<STRING_LITERAL>",
    "<DQUOTED_STRING>",
    "<UNTERMINATED_STRING_LITERAL>",
    "<UNTERMINATED_DQUOTED_STRING>",
    "<BOOLEAN_LITERAL>",
    "\"true\"",
    "\"false\"",
    "<DATE_LITERAL>",
    "<DATETIME_LITERAL>",
    "\";\"",
    "\"{\"",
    "\"}\"",
    "\"null\"",
    "\"\\\'\"",
    "\":=\"",
    "<OR>",
    "<AND>",
    "<NOT>",
    "<EQUAL>",
    "<NON_EQUAL>",
    "\".in.\"",
    "<LESS_THAN>",
    "<LESS_THAN_EQUAL>",
    "<GREATER_THAN>",
    "<GREATER_THAN_EQUAL>",
    "<REGEX_EQUAL>",
    "<CMPOPERATOR>",
    "\"-\"",
    "\"+\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "\"++\"",
    "\"--\"",
    "\"~\"",
    "<FIELD_ID>",
    "<REC_NAME_FIELD_ID>",
    "<REC_NAME_FIELD_NUM>",
    "<REC_NUM_FIELD_ID>",
    "<REC_NUM_FIELD_NUM>",
    "<REC_NUM_WILDCARD>",
    "<REC_NAME_WILDCARD>",
    "<REC_NUM_ID>",
    "<REC_NAME_ID>",
    "\"(\"",
    "\")\"",
    "\"int\"",
    "\"long\"",
    "\"date\"",
    "<DOUBLE_VAR>",
    "\"decimal\"",
    "\"boolean\"",
    "\"string\"",
    "\"bytearray\"",
    "\"list\"",
    "\"map\"",
    "\"record\"",
    "\"object\"",
    "\"break\"",
    "\"continue\"",
    "\"else\"",
    "\"for\"",
    "\"foreach\"",
    "\"function\"",
    "\"if\"",
    "\"return\"",
    "\"while\"",
    "\"case\"",
    "\"enum\"",
    "\"import\"",
    "\"switch\"",
    "\"default\"",
    "\"do\"",
    "\"try\"",
    "\"catch\"",
    "\"SKIP\"",
    "\"ALL\"",
    "\"OK\"",
    "\"ERROR\"",
    "\"year\"",
    "\"month\"",
    "\"week\"",
    "\"day\"",
    "\"hour\"",
    "\"minute\"",
    "\"second\"",
    "\"millisec\"",
    "\"read_dict\"",
    "\"write_dict\"",
    "\"delete_dict\"",
    "<IDENTIFIER>",
    "\",\"",
    "\"=\"",
    "\":\"",
    "\"[\"",
    "\"]\"",
    "\"isnull(\"",
    "\"nvl(\"",
    "\"nvl2(\"",
    "\"iif(\"",
    "\"print_stack(\"",
    "\"breakpoint(\"",
    "\"raise_error(\"",
    "\"print_err(\"",
    "\"eval(\"",
    "\"eval_exp(\"",
    "\"print_log(\"",
    "\"sequence(\"",
    "\".next\"",
    "\".current\"",
    "\".reset\"",
    "\"lookup(\"",
    "\".\"",
    "\"lookup_next(\"",
    "\"lookup_found(\"",
    "\"lookup_admin(\"",
    "<DATE_FIELD_LITERAL>",
    "<ERROR>",
  };

}
