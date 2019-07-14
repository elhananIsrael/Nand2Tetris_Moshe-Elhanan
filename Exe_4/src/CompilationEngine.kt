package ex04

import java.io.File
import java.io.IOException
import java.util.ArrayList

class CompilationEngine {

    var inputTxmlFile : File
    var outputXmlFile : File
    var input: List<String> = emptyList()
    var allTokens : ArrayList<Token> = ArrayList()
    var allParser : String = ""
    var token : Token = Token()
    var currentTokenIndex : Int = 0
    var twoWhiteSpaces : String = "  "



    constructor(inputTxmlFile :File, outputXmlFile :File    )
    {
        this.inputTxmlFile = inputTxmlFile
        this.outputXmlFile = outputXmlFile

        try
        {

            this.input =  this.inputTxmlFile.readLines()
            this.input.subList(1, (this.input.count()-1)).forEach {
                this.token = this.fromXmlLineToToken(it)
                this.allTokens.add(this.token)
                this.token = Token()
            }

            this.CompileClass("")

            this.outputXmlFile.writeText(this.allParser)  //write all parser

        }

        catch (e: IOException) {
            println("IOException: file not found.")
        }
    }


    fun fromXmlLineToToken(lineWithToken: String): Token
    {
        var tempToken = Token()
        var lineXml : String = lineWithToken

        lineXml = lineXml.substring(1)
        tempToken.type = lineXml.takeWhile { !it.equals('>') }
        lineXml = lineXml.dropWhile { !it.equals(' ') }
        lineXml = lineXml.substring(1)
        tempToken.token = lineXml.takeWhile { !it.equals('<') }
        tempToken.token = tempToken.token.dropLast(1)

        return tempToken
    }


// class: 'class' className '{' classVarDec*  subroutineDec* '}'
    fun CompileClass(space : String)
    {
        this.allParser+=space+"<class>\n"
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString() // 'class'
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex+1].toXmlString() // className
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex+2].toXmlString() // '{'
        currentTokenIndex+=3
        while(this.allTokens[this.currentTokenIndex].token == "static" ||
            this.allTokens[this.currentTokenIndex].token =="field" ) // classVarDec*
        {
            this.CompileClassVarDec(space + twoWhiteSpaces) // classVarDec
        }
        while(this.allTokens[this.currentTokenIndex].token == "constructor" ||
            this.allTokens[this.currentTokenIndex].token =="function" ||
            this.allTokens[this.currentTokenIndex].token =="method" ) // subroutineDec*
        {
            this.CompileSubroutine(space+twoWhiteSpaces) // subroutineDec
        }

        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString()  // '}'
        currentTokenIndex++
        this.allParser+=space+"</class>\n"
    }


    // classVarDec: ('static' | 'field' ) type varName (',' varName)*  ';'
    fun CompileClassVarDec(space : String)
    {
        this.allParser += space + "<classVarDec>\n"
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString()  //('static' | 'field' )
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 1].toXmlString() // type
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 2].toXmlString() // varName
        currentTokenIndex += 3
        while (this.allTokens[this.currentTokenIndex].token != ";") // (',' varName)*
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
            currentTokenIndex++
        }
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</classVarDec>\n"
    }


    // subroutineDec: ('constructor' | 'function' | 'method')  ('void' | type) subroutineName
    // '(' parameterList ')' subroutineBody
    fun CompileSubroutine(space : String)
    {
        this.allParser += space + "<subroutineDec>\n"
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //('constructor' | 'function' | 'method')
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ('void' | type)
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // subroutineName
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  '('
        currentTokenIndex++
        this.compileParameterList(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  ')'
        currentTokenIndex++
        this.compileSubroutineBody(space + twoWhiteSpaces)
        this.allParser += space + "</subroutineDec>\n"
    }

   //  parameterList: ( (type varName)  (',' type varName)*)?
    fun compileParameterList(space : String)
    {
        this.allParser += space + "<parameterList>\n"

        if(this.allTokens[this.currentTokenIndex].token != ")")  // ( (type varName)  (',' type varName)*)?
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  type
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  varName
            currentTokenIndex++
            while(this.allTokens[this.currentTokenIndex].token != ")") // (',' type varName)*
            {
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  ','
                currentTokenIndex++
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  type
                currentTokenIndex++
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  varName
                currentTokenIndex++
            }
        }

        this.allParser += space + "</parameterList>\n"
    }


    // subroutineBody: '{' varDec* statements '}'
    fun compileSubroutineBody(space : String)
    {
        this.allParser += space + "<subroutineBody>\n"
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token == "var")  // varDec*
        {
            this.compileVarDec(space + twoWhiteSpaces)
        }
        this.compileStatements(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
        this.allParser += space + "</subroutineBody>\n"
    }


    // varDec: 'var' type varName (',' varName)* ';'
    fun compileVarDec(space : String)
    {
        this.allParser += space + "<varDec>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'var'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // type
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token != ";") // (',' varName)*
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
            currentTokenIndex++
        }
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</varDec>\n"
    }


    // statements: statement*
    // statement: letStatement | ifStatement | whileStatement | doStatement | returnStatement
    fun compileStatements(space : String)
    {
        this.allParser += space + "<statements>\n"

        while (this.allTokens[this.currentTokenIndex].token == "let" || this.allTokens[this.currentTokenIndex].token == "if" ||
            this.allTokens[this.currentTokenIndex].token == "while" || this.allTokens[this.currentTokenIndex].token == "do" ||
            this.allTokens[this.currentTokenIndex].token == "return" )
        {
            if(this.allTokens[this.currentTokenIndex].token == "let")
                this.compileLet(space + twoWhiteSpaces)
            else if(this.allTokens[this.currentTokenIndex].token == "if")
                this.compileIf(space + twoWhiteSpaces)
            else if(this.allTokens[this.currentTokenIndex].token == "while")
                this.compileWhile(space + twoWhiteSpaces)
            else if(this.allTokens[this.currentTokenIndex].token == "do")
                this.compileDo(space + twoWhiteSpaces)
            else if(this.allTokens[this.currentTokenIndex].token == "return")
                this.compileReturn(space + twoWhiteSpaces)
        }

        this.allParser += space + "</statements>\n"
    }


    // doStatement: 'do'  subroutineCall ';'
    fun compileDo(space : String)
    {
        this.allParser += space + "<doStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'do'
        currentTokenIndex++
        this.compileSubroutineCall(space)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</doStatement>\n"
    }


    // letStatement: 'let'  varName ('[' expression ']')? '=' expression ';'
    fun compileLet(space : String)
    {
        this.allParser += space + "<letStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'let'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token == "[") // ('[' expression ']')?
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '['
            currentTokenIndex++
            this.CompileExpression(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ']'
            currentTokenIndex++
        }
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '='
        currentTokenIndex++
        this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</letStatement>\n"
    }


   // whileStatement: 'while' '(' expression ')' '{' statements '}'
    fun compileWhile(space : String)
    {
        this.allParser += space + "<whileStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'while'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
        currentTokenIndex++
        this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        this.compileStatements(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++

        this.allParser += space + "</whileStatement>\n"
    }


   // ReturnStatement 'return'  expression? ';'
    fun compileReturn(space : String)
    {
        this.allParser += space + "<returnStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'return'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token != ";") // expression?
            this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</returnStatement>\n"
    }


    // ifStatement: 'if' '(' expression ')' '{' statements '}'  ( 'else' '{' statements '}' )?
    fun compileIf(space : String)
    {
        this.allParser += space + "<ifStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'if'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
        currentTokenIndex++
        this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        this.compileStatements(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token == "else") // ( 'else' '{' statements '}' )?
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'else'
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
            currentTokenIndex++
            this.compileStatements(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
            currentTokenIndex++
        }

        this.allParser += space + "</ifStatement>\n"
    }


    // expression: term (op term)*
    fun CompileExpression(space : String)
    {
        this.allParser += space + "<expression>\n"

        this.CompileTerm(space + twoWhiteSpaces)
        while (this.allTokens[this.currentTokenIndex].token != "]" && this.allTokens[this.currentTokenIndex].token != ";" &&
            this.allTokens[this.currentTokenIndex].token != ")" && this.allTokens[this.currentTokenIndex].token != "," )  // (op term)*
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // op
            currentTokenIndex++
            this.CompileTerm(space + twoWhiteSpaces)
        }

        this.allParser += space + "</expression>\n"
    }


   // term: integerConstant | stringConstant | keywordConstant | varName |
   // varName '[' expression ']' | subroutineCall  | '(' expression ')' | unaryOp term
    fun CompileTerm(space : String)
    {
        this.allParser += space + "<term>\n"

        if(this.allTokens[this.currentTokenIndex].token == "-" || this.allTokens[this.currentTokenIndex].token == "~" ) // unaryOp term
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // unaryOp
            currentTokenIndex++
            this.CompileTerm(space + twoWhiteSpaces)
        }
        else if(this.allTokens[this.currentTokenIndex].token == "(") //  '(' expression ')'
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpression(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else if(this.allTokens[(this.currentTokenIndex+1)].token == "(" || this.allTokens[(this.currentTokenIndex+1)].token == ".")  // subroutineCall
            this.compileSubroutineCall(space )
        else // integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']'
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // integerConstant|stringConstant|keywordConstant|varName
            currentTokenIndex++
            if(this.allTokens[(this.currentTokenIndex)].token == "[") // '[' expression ']'
            {
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '['
                currentTokenIndex++
                this.CompileExpression(space + twoWhiteSpaces)
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ']'
                currentTokenIndex++
            }

        }

        this.allParser += space + "</term>\n"
    }


    // expressionList: (expression (',' expression)* )?
    fun CompileExpressionList(space : String)
    {
        this.allParser += space + "<expressionList>\n"
        if(this.allTokens[this.currentTokenIndex].token != ")") // (expression (',' expression)* )?
        {
            this.CompileExpression(space + twoWhiteSpaces)
            while (this.allTokens[this.currentTokenIndex].token == ",")  // (',' expression)*
            {
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
                currentTokenIndex++
                this.CompileExpression(space + twoWhiteSpaces)
            }
        }
        this.allParser += space + "</expressionList>\n"
    }


   // subroutineCall: subroutineName '(' expressionList ')' |
   // ( className | varName) '.' subroutineName  '(' expressionList ')'
    fun compileSubroutineCall(space : String)
    {
        if(this.allTokens[(this.currentTokenIndex+1)].token == "(") // subroutineName '(' expressionList ')'
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // subroutineName
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpressionList(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else // ( className | varName) '.' subroutineName  '(' expressionList ')'
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // className | varName
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '.'
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // subroutineName
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpressionList(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
    }


}