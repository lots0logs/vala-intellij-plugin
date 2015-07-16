/**
 * Hand-written lexer used for syntax highlighting in IntelliJ.
 *
 * Derived from the lexer generated by Grammar-Kit at 10 January 2015.
 */

package org.intellij.vala.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.intellij.vala.psi.ValaTypes.*;
import java.util.Stack;

%%

%{
  public _ValaLexer() {
    this((java.io.Reader)null);
  }

  private Stack<Integer> stack = new Stack<Integer>();

  private void pushState(int state) {
    stack.push(yystate());
    yybegin(state);
  }

  private void popState() {
    if (stack.empty()) {
        yybegin(YYINITIAL);

    } else {
        int state = stack.pop();
        yybegin(state);
    }
  }
%}

%public
%class _ValaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

ID=[A-Za-z_][A-Za-z0-9_]*
ESCAPED_ID=[A-Za-z0-9_]+
VERBATIM_STRING_LITERAL=\"\"\"(.|\n|\r)*\"\"\"
STRING_LITERAL=\"([^\"\\]|\\\"|\\'|\\)*\"
REAL_LITERAL=-?[0-9]+\.[0-9]+
INTEGER_LITERAL=-?[0-9]+
LINE_COMMENT="//".*
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?
PREPROCESSOR_DIRECTIVE=("#if" | "#endif" | "#elif" | "#else") .* ("\r"|"\n"|"\r\n")
CHARACTER_LITERAL='[A-Za-z0-9]'

%state AT_SGN

%%
<YYINITIAL> {
  {WHITE_SPACE}                  { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "=="                           { return OP_EQ; }
  "="                            { return OP_ASGN; }
  "+="                           { return OP_ADD_ASGN; }
  "-="                           { return OP_SUB_ASGN; }
  "*="                           { return OP_MUL_ASGN; }
  "/="                           { return OP_DIV_ASGN; }
  "|="                           { return OP_OR_ASGN; }
  "%="                           { return OP_MOD_ASGN; }
  "^="                           { return OP_XOR_ASGN; }
  "&="                           { return OP_AND_ASGN; }
  "<<="                          { return OP_LSHIFT_ASGN; }
  ">>="                          { return OP_RSHIFT_ASGN; }
  "|"                            { return OP_BIT_OR; }
  "+"                            { return OP_ADD; }
  "-"                            { return OP_SUB; }
  "*"                            { return OP_MUL; }
  "&&"                           { return OP_AND; }
  "||"                           { return OP_OR; }
  "!"                            { return OP_NOT; }
  "?"                            { return OP_COND; }
  "??"                           { return OP_COALESCE; }
  "~"                            { return OP_BIN_NOT; }
  "&"                            { return OP_REF; }
  "=>"                           { return OP_LAMBDA; }
  "!="                           { return OP_NEG; }
  "<"                            { return OP_LT; }
  ">"                            { return OP_GT; }
  "<="                           { return OP_LE; }
  ">="                           { return OP_GE; }
  "/"                            { return OP_DIV; }
  "%"                            { return OP_MOD; }
  "++"                           { return OP_INC; }
  "--"                           { return OP_DEC; }
  "<<"                           { return OP_LSHIFT; }
  ">>"                           { return OP_RSHIFT; }
  "^"                            { return OP_XOR; }
  ","                            { return COMA; }
  ";"                            { return SEMICOLON; }
  ":"                            { return COLON; }
  "{"                            { return LEFT_CURLY; }
  "}"                            { return RIGHT_CURLY; }
  "["                            { return LEFT_SQUARE; }
  "]"                            { return RIGHT_SQUARE; }
  "("                            { return LEFT_PAREN; }
  ")"                            { return RIGHT_PAREN; }
  "global::"                     { return GLOBAL; }
  "true"                         { return KEY_TRUE; }
  "false"                        { return KEY_FALSE; }
  "null"                         { return KEY_NULL; }
  "is"                           { return KEY_IS; }
  "as"                           { return KEY_AS; }
  "namespace"                    { return KEY_NAMESPACE; }
  "using"                        { return KEY_USING; }
  "class"                        { return KEY_CLASS; }
  "ref"                          { return KEY_REF; }
  "protected"                    { return KEY_PROTECTED; }
  "public"                       { return KEY_PUBLIC; }
  "internal"                     { return KEY_INTERNAL; }
  "private"                      { return KEY_PRIVATE; }
  "throws"                       { return KEY_THROWS; }
  "if"                           { return KEY_IF; }
  "else"                         { return KEY_ELSE; }
  "switch"                       { return KEY_SWITCH; }
  "case"                         { return KEY_CASE; }
  "default"                      { return KEY_DEFAULT; }
  "do"                           { return KEY_DO; }
  "while"                        { return KEY_WHILE; }
  "for"                          { return KEY_FOR; }
  "foreach"                      { return KEY_FOREACH; }
  "var"                          { return KEY_VAR; }
  "in"                           { return KEY_IN; }
  "break"                        { return KEY_BREAK; }
  "continue"                     { return KEY_CONTINUE; }
  "return"                       { return KEY_RETURN; }
  "yield"                        { return KEY_YIELD; }
  "try"                          { return KEY_TRY; }
  "throw"                        { return KEY_THROW; }
  "finally"                      { return KEY_FINALLY; }
  "catch"                        { return KEY_CATCH; }
  "lock"                         { return KEY_LOCK; }
  "delete"                       { return KEY_DELETE; }
  "base"                         { return KEY_BASE; }
  "this"                         { return KEY_THIS; }
  "new"                          { return KEY_NEW; }
  "out"                          { return KEY_OUT; }
  "interface"                    { return KEY_INTERFACE; }
  "struct"                       { return KEY_STRUCT; }
  "params"                       { return KEY_PARAMS; }
  "ensures"                      { return KEY_ENSURES; }
  "requires"                     { return KEY_REQUIRES; }
  "async"                        { return KEY_ASYNC; }
  "inline"                       { return KEY_INLINE; }
  "abstract"                     { return KEY_ABSTRACT; }
  "extern"                       { return KEY_EXTERN; }
  "virtual"                      { return KEY_VIRTUAL; }
  "override"                     { return KEY_OVERRIDE; }
  "const"                        { return KEY_CONST; }
  "get"                          { return KEY_GET; }
  "set"                          { return KEY_SET; }
  "construct"                    { return KEY_CONSTRUCT; }
  "signal"                       { return KEY_SIGNAL; }
  "static"                       { return KEY_STATIC; }
  "typeof"                       { return KEY_TYPEOF; }
  "sizeof"                       { return KEY_SIZEOF; }
  "owned"                        { return KEY_OWNED; }
  "dynamic"                      { return KEY_DYNAMIC; }
  "unowned"                      { return KEY_UNOWNED; }
  "weak"                         { return KEY_WEAK; }
  "errordomain"                  { return KEY_ERRORDOMAIN; }
  "enum"                         { return KEY_ENUM; }
  "delegate"                     { return KEY_DELEGATE; }
  "void"                         { return TYPE_VOID; }
  "(owned)"                      { return CAST_OWNED; }
  "(void)"                       { return CAST_VOID; }
  "(dynamic)"                    { return CAST_DYNAMIC; }
  "(!)"                          { return CAST_NON_NULL; }
  "\""                           { return QUOTE; }
  "@"                            { pushState(AT_SGN); return com.intellij.psi.TokenType.WHITE_SPACE;}
  "..."                          { return VARARGS; }
  "."                            { return DOT; }
  "->"                           { return ARROW; }
  "pointer_member_access"        { return POINTER_MEMBER_ACCESS; }
  "element_access"               { return ELEMENT_ACCESS; }
  "post_increment_expression"    { return POST_INCREMENT_EXPRESSION; }
  "post_decrement_expression"    { return POST_DECREMENT_EXPRESSION; }

  {ID}                   		 { return ID; }
  {VERBATIM_STRING_LITERAL}      { return VERBATIM_STRING_LITERAL; }
  {STRING_LITERAL}               { return STRING_LITERAL; }
  {REAL_LITERAL}                 { return REAL_LITERAL; }
  {INTEGER_LITERAL}              { return INTEGER_LITERAL; }
  {LINE_COMMENT}                 { return LINE_COMMENT; }
  {BLOCK_COMMENT}                { return BLOCK_COMMENT; }
  {PREPROCESSOR_DIRECTIVE}       { return PREPROCESSOR_DIRECTIVE; }
  {CHARACTER_LITERAL}            { return CHARACTER_LITERAL; }

  [^]                            { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<AT_SGN> {
  {ESCAPED_ID}                   		 { popState(); return ID; }
  [^]                            { popState(); yypushback(yylength()); return TEMPLATE_STRING_OPEN; }
}