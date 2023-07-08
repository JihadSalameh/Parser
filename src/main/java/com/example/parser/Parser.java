package com.example.parser;

import java.util.Objects;
import java.util.Queue;

//All "names" and "integer-value" are user defined names and values in the source code.
//The tokens in bold letters are reserved words.
//The words between "" are terminals (tokens).
public class Parser {

    private final Queue<Token> tokenQueue;

    private Token token;

    public Parser(Queue<Token> tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    public void Parse() {
        projectDeclaration();
    }

    //project-declaration  project-def     "."
    private void projectDeclaration() {
        projectDef();
        if(token.getX().equals(".")) {
            System.out.println("Parsing is done successfully!");
        } else {
            ERROR(".");
        }
    }

    //project-def     project-heading        declarations          compound-stmt
    private void projectDef() {
        projectHeading();
        declarations();
        compoundStmt();
    }

    //project-heading    project      "name"       ";"
    private void projectHeading() {
        token = tokenQueue.poll();
        assert token != null;
        if(token.getX().equals("project")) {
            token = tokenQueue.poll();
        } else {
            ERROR("project");
        }

        assert token != null;
        if(token.getType().equals("user_defined_name")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name");
        }

        assert token != null;
        if(token.getX().equals(";")) {
            token = tokenQueue.poll();
        } else {
            ERROR(";");
        }
    }

    //continue with the rest of the debugging from here
    //declarations    const-decl       var-decl       subroutine-decl
    private void declarations() {
        if(token.getX().equals("const")) {
            constDecl();
        }
        if(token.getX().equals("var")) {
            varDecl();
        }
        if(token.getX().equals("routine")) {
            subroutineDecl();
        }
    }

    //compund-stmt  start       stmt-list       end
    private void compoundStmt() {
        if(token.getX().equals("start")) {
            token = tokenQueue.poll();
        } else {
            ERROR("start");
        }
        stmtList();
        if(token.getX().equals("end")) {
            token = tokenQueue.poll();
        } else {
            ERROR("end");
        }
    }

    //stmt-list    ( statement    ";" )*
    private void stmtList() {
        Token temp = tokenQueue.peek();
        assert temp != null;
        while(!temp.getX().equals("end")) {
            statement();
            if(token.getX().equals(";")) {
                temp = tokenQueue.peek();
                token = tokenQueue.poll();
                assert temp != null;
            } else {
                ERROR(";");
            }
        }
    }

    //statement  ass-stmt  |  inout-stmt  |  if-stmt  |  loop-stmt   |   compound-stmt    |     Lambda
    private void statement() {
        switch (token.getX()) {
            case "input", "output" -> inoutStmt();
            case "if" -> ifStmt();
            case "loop" -> loopStmt();
            case "start" -> compoundStmt();
            default -> assStmt();
        }
    }

    //loop-stmt  loop   “(“    bool-exp   “)”  do      statement
    private void loopStmt() {
        if(token.getX().equals("loop")) {
            token = tokenQueue.poll();
        } else {
            ERROR("loop");
        }

        assert token != null;
        if(token.getX().equals("(")) {
            token = tokenQueue.poll();
        } else {
            ERROR("(");
        }

        boolExp();

        if(token.getX().equals(")")) {
            token = tokenQueue.poll();
        } else {
            ERROR(")");
        }

        assert token != null;
        if(token.getX().equals("do")) {
            token = tokenQueue.poll();
        } else {
            ERROR("do");
        }

        statement();
    }

    //if-stmt  if     “(“    bool-exp    “)”    then     statement     else-part       endif
    private void ifStmt() {
        if(token.getX().equals("if")) {
            token = tokenQueue.poll();
        } else {
            ERROR("if");
        }

        assert token != null;
        if(token.getX().equals("(")) {
            token = tokenQueue.poll();
        } else {
            ERROR("(");
        }

        boolExp();

        if(token.getX().equals(")")) {
            token = tokenQueue.poll();
        } else {
            ERROR(")");
        }

        assert token != null;
        if(token.getX().equals("then")) {
            token = tokenQueue.poll();
        } else {
            ERROR("then");
        }

        statement();
        elsePart();

        if(token.getX().equals("endif")) {
            token = tokenQueue.poll();
        } else {
            ERROR("endif");
        }
    }

    //else-part   else     statement   |   Lamba
    private void elsePart() {
        if(token.getX().equals("else")) {
            token = tokenQueue.poll();
            statement();
        }
    }

    //bool-exp  name-value       relational-oper        name-value
    private void boolExp() {
        nameValue();
        relationalOper();
        nameValue();
    }

    //relational-oper       "="     |     "<>"     |     "<"    |     "<="     |     ">"    |     ">="
    private void relationalOper() {
        switch (token.getX()) {
            case "=", ">=", "<>", "<", ">", "<=" -> token = tokenQueue.poll();
        }
    }

    //inout-stmt  input "("    "name"     ")"    |    output  "("   name-value   ")"
    private void inoutStmt() {
        if(token.getX().equals("input")) {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals("(")) {
                token = tokenQueue.poll();
            } else {
                ERROR("(");
            }

            assert token != null;
            if(token.getType().equals("user_defined_name")) {
                token = tokenQueue.poll();
            } else {
                ERROR("user_defined_name");
            }

            assert token != null;
            if(token.getX().equals(")")) {
                token = tokenQueue.poll();
            } else {
                ERROR(")");
            }
        } else {
            token = tokenQueue.poll();

            assert token != null;
            if(token.getX().equals("(")) {
                token = tokenQueue.poll();
            } else {
                ERROR("(");
            }

            nameValue();

            if(token.getX().equals(")")) {
                token = tokenQueue.poll();
            } else {
                ERROR(")");
            }
        }
    }

    //ass-stmt  ”name”     ":="      arith-exp
    private void assStmt() {
        if(token.getType().equals("user_defined_name")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name");
        }

        assert token != null;
        if(token.getX().equals(":=")) {
            token = tokenQueue.poll();
        } else {
            ERROR(":=");
        }

        arithExp();
    }

    //arith-exp  term    ( add-sign      term )*
    private void arithExp() {
        term();
        while(addSign()) {
            term();
        }
    }

    //add-sign   "+"    |     "-"
    private boolean addSign() {
        if(token.getX().equals("+") || token.getX().equals("-")) {
            token = tokenQueue.poll();
            return true;
        } else {
            return false;
        }
    }

    //term  factor    ( mul-sign       factor  )*
    private void term() {
        factor();
        while(mulSign()) {
            factor();
        }
    }

    //mul-sign  "*"    |      "/"     |        “%”
    private boolean mulSign() {
        if(token.getX().equals("*") || token.getX().equals("/") || token.getX().equals("%")) {
            token = tokenQueue.poll();
            return true;
        } else {
            return false;
        }
    }

    //factor   "("   arith-exp  ")"   |     name-value
    private void factor() {
        if(token.getX().equals("(")) {
            token = tokenQueue.poll();
            arithExp();
            if(token.getX().equals(")")) {
                token = tokenQueue.poll();
            } else {
                ERROR(")");
            }
        } else {
            nameValue();
        }

    }

    //name-value   "name"      |        "integer-value"
    private void nameValue() {
        if(token.getType().equals("user_defined_name") || token.getType().equals("user_defined_integer")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name or user_defined_integer");
        }
    }

    //const-decl    const      ( const-item      ";" )+        |     Lambda
    private void constDecl() {
        if(token.getX().equals("const")) {
            token = tokenQueue.poll();
            while(constItem()) {
                if(token.getX().equals(";")) {
                    token = tokenQueue.poll();
                    assert token != null;
                    if(!token.getType().equals("user_defined_name")) {
                        break;
                    }
                } else {
                    ERROR(";");
                }
            }
        } else {
            ERROR("const");
        }
    }

    //const-item     "name"   =   "integer-value"
    private boolean constItem() {
        if(token.getType().equals("user_defined_name")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name");
            return false;
        }

        assert token != null;
        if(token.getX().equals("=")) {
            token = tokenQueue.poll();
        } else {
            ERROR("=");
            return false;
        }

        assert token != null;
        if(token.getType().equals("user_defined_integer")) {
            token = tokenQueue.poll();
            return true;
        } else {
            ERROR("user_defined_integer");
            return false;
        }
    }

    //var-decl    var    (var-item     ";" )+         |     Lambda
    private void varDecl() {
        if(token.getX().equals("var")) {
            token = tokenQueue.poll();
            while(varItem()) {
                if(token.getX().equals(";")) {
                    token = tokenQueue.poll();
                    assert token != null;
                    if(!token.getType().equals("user_defined_name")) {
                        break;
                    }
                } else {
                    ERROR(";");
                }
            }
        } else {
            ERROR("var");
        }
    }

    //var-item     name-list       ":"       int
    private boolean varItem() {
        nameList();
        if(token.getX().equals(":")) {
            token = tokenQueue.poll();
        } else {
            ERROR(":");
            return false;
        }

        assert token != null;
        if(token.getX().equals("int")) {
            token = tokenQueue.poll();
            return true;
        } else {
            ERROR("int");
            return false;
        }
    }

    //name-list    "name"    ( ","     "name" )*
    private void nameList() {
        if(token.getType().equals("user_defined_name")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name");
        }

        assert token != null;
        if(token.getX().equals(",")) {
            while(Objects.requireNonNull(token).getX().equals(",")) {
                token = tokenQueue.poll();
                assert token != null;
                if(token.getType().equals("user_defined_name")) {
                    token = tokenQueue.poll();
                } else {
                    ERROR("user_defined_name");
                }
            }
        }
    }

    //subroutine-decl  subroutine-heading      declarations      compound-stmt    “;”   |     Lambda
    private void subroutineDecl() {
        subroutineHeading();
        declarations();
        compoundStmt();
        if(token.getX().equals(";")) {
            token = tokenQueue.poll();
        } else {
            ERROR(";");
        }

    }

    //subroutine-heading    routine      "name"       ";"
    private void subroutineHeading() {
        if(token.getX().equals("routine")) {
            token = tokenQueue.poll();
        } else {
            ERROR("routine");
        }

        assert token != null;
        if(token.getType().equals("user_defined_name")) {
            token = tokenQueue.poll();
        } else {
            ERROR("user_defined_name");
        }

        assert token != null;
        if(token.getX().equals(";")) {
            token = tokenQueue.poll();
        } else {
            ERROR(";");
        }
    }

    private void ERROR(String expected) {
        System.out.println("ERROR: Expected -> " + expected + " but got -> " + token.getX());
        System.exit(0);
    }

}
