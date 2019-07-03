import java.io.File
import java.io.IOException
import java.util.ArrayList

class CompilationEngine {

    var inputTxmlFilePath : String  = ""
    var outputXmlFilePath : String  = ""
    var input: List<String> = emptyList()
    var allTokens : ArrayList<Token> = ArrayList()
    var allParser : String = ""
    var token : Token = Token()
    var currentTokenIndex : Int = 0
    var twoWhiteSpaces : String = "  "
    var oneWhiteSpaces : String = " "



    constructor(inputTxmlFilePath :String, outputXmlFilePath :String    )
    {
        try
        {
            this.inputTxmlFilePath = inputTxmlFilePath
            this.outputXmlFilePath = outputXmlFilePath

            this.input =  File(inputTxmlFilePath).readLines()
            this.input.subList(1, (this.input.count()-1)).forEach {
                this.token = this.fromXmlLineToToken(it)
                this.allTokens.add(this.token)
                this.token = Token()
            }

            this.CompileClass("")

            File(this.outputXmlFilePath).writeText(this.allParser)  //write all parser

        }

        catch (e: IOException) {
            println("IOException: file not found.")
        }
    }


    fun fromXmlLineToToken(lineWithToken: String): Token
    {
        var tempToken : Token = Token()
        var lineXml : String = lineWithToken

        lineXml = lineXml.substring(1)
        tempToken.type = lineXml.takeWhile { !it.equals('>') }
        lineXml = lineXml.dropWhile { !it.equals(' ') }
        lineXml = lineXml.substring(1)
        tempToken.token = lineXml.takeWhile { !it.equals('<') }
        tempToken.token = tempToken.token.dropLast(1)

        return tempToken
    }


    fun CompileClass(space : String)
    {
        this.allParser+=space+"<class>\n"
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString()
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex+1].toXmlString()
        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex+2].toXmlString()
        currentTokenIndex+=3
        while(this.allTokens[this.currentTokenIndex].token == "static" || this.allTokens[this.currentTokenIndex].token =="field" )
        {
            this.CompileClassVarDec(space + twoWhiteSpaces)
        }
        while(this.allTokens[this.currentTokenIndex].token == "constructor" ||
            this.allTokens[this.currentTokenIndex].token =="function" || this.allTokens[this.currentTokenIndex].token =="method" )
        {
            this.CompileSubroutine(space+twoWhiteSpaces)
        }

        this.allParser+=space+twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString()
        currentTokenIndex++
        this.allParser+=space+"</class>\n"
    }


    fun CompileClassVarDec(space : String)
    {
        this.allParser += space + "<classVarDec>\n"
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString()
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 1].toXmlString()
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 2].toXmlString()
        currentTokenIndex += 3
        while (this.allTokens[this.currentTokenIndex].token != ";")
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 2].toXmlString()
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 2].toXmlString()
            currentTokenIndex++
        }
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString()
        this.allParser += space + "</classVarDec>\n"
    }

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

        if(this.allTokens[this.currentTokenIndex].token != ")")
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  type
            currentTokenIndex++
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  varName
            currentTokenIndex++
            while(this.allTokens[this.currentTokenIndex].token != ")")
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

    fun compileSubroutineBody(space : String)
    {
        this.allParser += space + "<subroutineBody>\n"
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token == "var")
        {
            this.compileVarDec(space + twoWhiteSpaces)
        }
        this.compileStatements(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
        this.allParser += space + "</subroutineBody>\n"
    }

    fun compileVarDec(space : String)
    {
        this.allParser += space + "<varDec>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'var'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // type
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token != ";")
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


    fun compileLet(space : String)
    {
        this.allParser += space + "<letStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'let'
        currentTokenIndex++
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // varName
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token == "[")
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

    fun compileReturn(space : String)
    {
        this.allParser += space + "<returnStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'return'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token != ";")
            this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</returnStatement>\n"
    }

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
        if(this.allTokens[this.currentTokenIndex].token == "else")
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

    fun CompileExpression(space : String)
    {
        this.allParser += space + "<expression>\n"

        this.CompileTerm(space + twoWhiteSpaces)
        while (this.allTokens[this.currentTokenIndex].token != "]" && this.allTokens[this.currentTokenIndex].token != ";" &&
            this.allTokens[this.currentTokenIndex].token != ")" )
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // op
            currentTokenIndex++
            this.CompileTerm(space + twoWhiteSpaces)
        }

        this.allParser += space + "</expression>\n"
    }

    fun CompileTerm(space : String)
    {
        this.allParser += space + "<term>\n"

        if(this.allTokens[this.currentTokenIndex].token == "-" || this.allTokens[this.currentTokenIndex].token == "~" )
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // unaryOp
            currentTokenIndex++
            this.CompileTerm(space + twoWhiteSpaces)
        }
        else if(this.allTokens[this.currentTokenIndex].token == "(")
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpression(space + twoWhiteSpaces)
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else if(this.allTokens[(this.currentTokenIndex+1)].token == "(" || this.allTokens[(this.currentTokenIndex+1)].token == ".")
            this.compileSubroutineCall(space )
        else
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // integerConstant|stringConstant|keywordConstant|varName
            currentTokenIndex++
            if(this.allTokens[(this.currentTokenIndex)].token == "[")
            {
                // this.allParser += space + uniqSpace + this.allTokens[currentTokenIndex].toXmlString() // varName
                // currentTokenIndex++
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '['
                currentTokenIndex++
                this.CompileExpression(space + twoWhiteSpaces)
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ']'
                currentTokenIndex++
            }

        }

        this.allParser += space + "</term>\n"
    }

    fun CompileExpressionList(space : String)
    {
        this.allParser += space + "<expressionList>\n"
        if(this.allTokens[this.currentTokenIndex].token != ")")
        {
            this.CompileExpression(space + twoWhiteSpaces)
            while (this.allTokens[this.currentTokenIndex].token == ",")
            {
                this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
                currentTokenIndex++
                this.CompileExpression(space + twoWhiteSpaces)
            }
        }
        this.allParser += space + "</expressionList>\n"
    }


    fun compileSubroutineCall(space : String)
    {
        if(this.allTokens[(this.currentTokenIndex+1)].token == "(")
        {
            this.allParser += space + oneWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // subroutineName
            currentTokenIndex++
            this.allParser += space + oneWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpressionList(space + oneWhiteSpaces)
            this.allParser += space + oneWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else
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