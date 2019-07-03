import java.io.File
import java.io.IOException
import java.util.ArrayList

class CompilationEngine {

    var inputTxmlFilePath : String  = ""
    var outputVMFilePath : String  = ""
    var input: List<String> = emptyList()
    var allTokens : ArrayList<Token> = ArrayList()
    var allParser : String = ""
    var token : Token = Token()
    var currentTokenIndex : Int = 0
    var twoWhiteSpaces : String = "  "
    var oneWhiteSpaces : String = " "
    var allSymbolTable: AllSymbolTables = AllSymbolTables()
    var  vmWriter: VMWriter

    /**
    var currentScope : String = ""
    var currentClassName : String = ""
    var currentSubroutineName : String = ""
    var ClassScope_SymbolTable  = arrayListOf<SymbolTable>()
    var SubroutineScope_SymbolTable  = arrayListOf<SymbolTable>()
*/


    constructor(inputTxmlFilePath :String, outputVMFilePath :String    )
    {
        this.vmWriter = VMWriter(outputVMFilePath)

        try
        {
            this.inputTxmlFilePath = inputTxmlFilePath
            this.outputVMFilePath = outputVMFilePath

            this.input =  File(inputTxmlFilePath).readLines()
            this.input.subList(1, (this.input.count()-1)).forEach {
                this.token = this.fromXmlLineToToken(it)
                this.allTokens.add(this.token)
                this.token = Token()
            }

            this.CompileClass()

            File(this.outputVMFilePath).writeText(this.allParser)  //write all parser in vm file

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


//  class: 'class' className '{' classVarDec*  subroutineDec* '}'
    fun CompileClass() {

    this.allSymbolTable.ClassScope_SymbolTable.clear()
    this.allSymbolTable.currentScope = "Class"
    this.vmWriter.writeText("//CompileClass\n")

   // this.allParser+="<class>\n"
    //this.allParser+=twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString()  // 'class'
    var className=this.allTokens[currentTokenIndex+1].token  // className
    this.allSymbolTable.currentClassName = className
   // this.allParser+=twoWhiteSpaces+ this.allTokens[currentTokenIndex+2].toXmlString()  //  '{'
    currentTokenIndex+=3
    while(this.allTokens[this.currentTokenIndex].token == "static" || this.allTokens[this.currentTokenIndex].token =="field" )
    {
    this.CompileClassVarDec()
    }
    while(this.allTokens[this.currentTokenIndex].token == "constructor" ||
    this.allTokens[this.currentTokenIndex].token =="function" || this.allTokens[this.currentTokenIndex].token =="method" )
    {
    this.CompileSubroutineDec()
    }

  //  this.allParser+=twoWhiteSpaces+ this.allTokens[currentTokenIndex].toXmlString()  // '}'
    currentTokenIndex++
   // this.allParser+="</class>\n"

    }

//  classVarDec: ('static' | 'field' ) type varName (',' varName)*  ';'
    fun CompileClassVarDec()
    {

        this.vmWriter.writeText("//CompileClassVarDec\n")

        var kind = this.allTokens[currentTokenIndex].token  // ('static' | 'field' )
        var type = this.allTokens[currentTokenIndex+1].token  //  type
        var varName = this.allTokens[currentTokenIndex+2].token  //  varName

        when (kind)
        {
            "static" -> this.allSymbolTable.define(varName, type, Kind.STATIC)
            "field" -> this.allSymbolTable.define(varName, type, Kind.FIELD)
        }
        currentTokenIndex += 3
         while (this.allTokens[this.currentTokenIndex].token != ";")   //  (',' varName)*
        {
           // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex + 2].toXmlString()  //  ','
            currentTokenIndex++
            var varName = this.allTokens[currentTokenIndex].token  //  varName

            when (kind)
            {
                "static" -> this.allSymbolTable.define(varName, type, Kind.STATIC)
                "field" -> this.allSymbolTable.define(varName, type, Kind.FIELD)
            }
            currentTokenIndex++
        }
       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString()  //  ';'

     //   this.allParser += space + "</classVarDec>\n"
    }

    //  subroutineDec: ('constructor' | 'function' | 'method')  ('void' | type) subroutineName  '(' parameterList ')' subroutineBody
    fun CompileSubroutineDec()
    {
        this.vmWriter.writeText("//CompileSubroutineDec\n")

        var subroutineType= this.allTokens[currentTokenIndex].token //('constructor' | 'function' | 'method')
        currentTokenIndex++
        var subroutineReturnType= this.allTokens[currentTokenIndex].token // ('void' | type)
        currentTokenIndex++
        var subroutineName= this.allTokens[currentTokenIndex].token // subroutineName
        currentTokenIndex++
    //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  '('
        currentTokenIndex++
        this.allSymbolTable.startSubroutine(subroutineName, subroutineType)
        this.compileParameterList()
     //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  ')'
        currentTokenIndex++
        this.compileSubroutineBody()
    }


    //  parameterList: ( (type varName)  (',' type varName)*)?
    fun compileParameterList()
    {
        this.vmWriter.writeText("//compileParameterList\n")

        if(this.allTokens[this.currentTokenIndex].token != ")")
        {
            var type= this.allTokens[currentTokenIndex].token //  type
            currentTokenIndex++
            var varName= this.allTokens[currentTokenIndex].token //  varName
            currentTokenIndex++
            this.allSymbolTable.define(varName, type, Kind.ARG)

            while(this.allTokens[this.currentTokenIndex].token != ")")
            {
               // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  ','
                currentTokenIndex++
                var type= this.allTokens[currentTokenIndex].token //  type
                currentTokenIndex++
                var varName= this.allTokens[currentTokenIndex].token //  varName
                currentTokenIndex++
                this.allSymbolTable.define(varName, type, Kind.ARG)
            }
        }
    }


    //  subroutineBody: '{' varDec* statements '}'
    fun compileSubroutineBody()
    {
        this.vmWriter.writeText("//compileSubroutineBody\n")
        var nLocals: Int = 0
// אחרי שנדע כמה משתנים מקומיים יש לפונקציה הזאת אז צריך להצהיר על הפונקציה.

        //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token == "var")
        {
            this.compileVarDec()
            nLocals++
        }
        this.vmWriter.writeFunction(this.allSymbolTable.currentSubroutineName, nLocals)
        this.compileStatements()
        //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
    }


    // varDec: 'var' type varName (',' varName)* ';'
    fun compileVarDec()
    {
        this.vmWriter.writeText("//compileVarDec\n")

        //this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'var'
        currentTokenIndex++
        var type= this.allTokens[currentTokenIndex].token // type
        currentTokenIndex++
        var varName= this.allTokens[currentTokenIndex].token // varName
        currentTokenIndex++
        this.allSymbolTable.define(varName, type, Kind.VAR)
        while (this.allTokens[this.currentTokenIndex].token != ";")
        {
            //this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
            currentTokenIndex++
            var varName= this.allTokens[currentTokenIndex].token // varName
            currentTokenIndex++
            this.allSymbolTable.define(varName, type, Kind.VAR)
        }
       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++
    }

    fun compileStatements()
    {
       /** this.allParser += space + "<statements>\n"

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
       */
    }

    fun compileDo()
    {
        /** this.allParser += space + "<doStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'do'
        currentTokenIndex++
        this.compileSubroutineCall(space)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</doStatement>\n"
        */
    }


    fun compileLet()
    {
        /** this.allParser += space + "<letStatement>\n"

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
        */
    }

    fun compileWhile()
    {
       /** this.allParser += space + "<whileStatement>\n"

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
       */
    }

    fun compileReturn()
    {
        /** this.allParser += space + "<returnStatement>\n"

        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'return'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token != ";")
            this.CompileExpression(space + twoWhiteSpaces)
        this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++

        this.allParser += space + "</returnStatement>\n"
        */
    }

    fun compileIf()
    {
        /** this.allParser += space + "<ifStatement>\n"

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
        */
    }

    fun CompileExpression()
    {
        /** this.allParser += space + "<expression>\n"

        this.CompileTerm(space + twoWhiteSpaces)
        while (this.allTokens[this.currentTokenIndex].token != "]" && this.allTokens[this.currentTokenIndex].token != ";" &&
            this.allTokens[this.currentTokenIndex].token != ")" )
        {
            this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // op
            currentTokenIndex++
            this.CompileTerm(space + twoWhiteSpaces)
        }

        this.allParser += space + "</expression>\n"
        */
    }

    fun CompileTerm()
    {
        /** this.allParser += space + "<term>\n"

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
        */
    }

    fun CompileExpressionList()
    {
        /** this.allParser += space + "<expressionList>\n"
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
        */
    }


    fun compileSubroutineCall()
    {
       /** if(this.allTokens[(this.currentTokenIndex+1)].token == "(")
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
        */
    }


}