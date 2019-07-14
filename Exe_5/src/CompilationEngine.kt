package ex05

import java.io.File
import java.io.IOException
import java.util.ArrayList

class CompilationEngine {

    var inputTxmlFile : File
    var outputVMFile : File

    var input: List<String> = emptyList()
    var allTokens : ArrayList<Token> = ArrayList()
    var token : Token = Token()
    var currentTokenIndex : Int = 0
    var oneWhiteLines : String = "\n"
    var allSymbolTable: AllSymbolTables = AllSymbolTables()
    var vmWriter: VMWriter
    var whileExpIndex:Int = 0
    var whileEndIndex:Int = 0
    var ifTrueIndex:Int = 0
    var ifEndIndex:Int = 0
    var ifFalseIndex:Int = 0


    constructor(inputTxmlFile :File, outputVMFile :File    )
    {
        this.inputTxmlFile = inputTxmlFile
        this.outputVMFile = outputVMFile
        this.vmWriter = VMWriter(outputVMFile)

        try
        {

            this.input =  this.inputTxmlFile.readLines()
            this.input.subList(1, (this.input.count()-1)).forEach {
                this.token = this.fromXmlLineToToken(it)
                this.allTokens.add(this.token)
                this.token = Token()
            }

            this.CompileClass()

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


     //  class: 'class' className '{' classVarDec*  subroutineDec* '}'
    fun CompileClass() {

    this.allSymbolTable.ClassScope_SymbolTable.clear()
    this.allSymbolTable.currentScope = "Class"

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

    }

     //  classVarDec: ('static' | 'field' ) type varName (',' varName)*  ';'
    fun CompileClassVarDec()
    {

        this.vmWriter.writeText(this.oneWhiteLines)

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
        currentTokenIndex++

        this.vmWriter.writeText(this.oneWhiteLines)
    }

    //  subroutineDec: ('constructor' | 'function' | 'method')  ('void' | type) subroutineName  '(' parameterList ')' subroutineBody
    fun CompileSubroutineDec()
    {
        this.vmWriter.writeText(this.oneWhiteLines)

        var subroutineType= this.allTokens[currentTokenIndex].token //('constructor' | 'function' | 'method')
        currentTokenIndex++
        var subroutineReturnType= this.allTokens[currentTokenIndex].token // ('void' | type)
        currentTokenIndex++
        var subroutineName= this.allTokens[currentTokenIndex].token // subroutineName
        currentTokenIndex++
    //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  '('
        currentTokenIndex++
        this.allSymbolTable.startSubroutine(subroutineName, subroutineType, subroutineReturnType)
        this.compileParameterList()
     //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() //  ')'
        currentTokenIndex++
        this.compileSubroutineBody()

        this.vmWriter.writeText(this.oneWhiteLines)

    }


    //  parameterList: ( (type varName)  (',' type varName)*)?
    fun compileParameterList()
    {

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

        //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        while (this.allTokens[this.currentTokenIndex].token == "var")
        {
            this.compileVarDec()
        }
        var nLocals = this.allSymbolTable.varCount(Kind.VAR)
        this.vmWriter.writeFunction("${this.allSymbolTable.currentClassName}.${this.allSymbolTable.currentSubroutineName}", nLocals)

        when(this.allSymbolTable.currentSubroutineType) {
            "constructor" -> {
                var nFields = this.allSymbolTable.varCount(Kind.FIELD)
                vmWriter.writePush(Segment.CONST, nFields)
                vmWriter.writeCall("Memory.alloc", 1)  // creating a memory block for representing the new object
                vmWriter.writePop(Segment.POINTER, 0)  // sets THIS to the base address of this block (using pointer)
            }
            "method" -> {
                vmWriter.writePush(Segment.ARG, 0)  // push argument 0=push the class pointer   into the machsanit
                vmWriter.writePop(Segment.POINTER, 0)  //pop pointer 0=take it out :pointer 0=RAM[THIS]
            }
        }

        this.compileStatements()
        //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
    }


    // varDec: 'var' type varName (',' varName)* ';'
    fun compileVarDec()
    {

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


    // statements: statement*
    // statement: letStatement | ifStatement | whileStatement | doStatement | returnStatement
    fun compileStatements()
    {

        while (this.allTokens[this.currentTokenIndex].token == "let" || this.allTokens[this.currentTokenIndex].token == "if" ||
            this.allTokens[this.currentTokenIndex].token == "while" || this.allTokens[this.currentTokenIndex].token == "do" ||
            this.allTokens[this.currentTokenIndex].token == "return" )
        {
            if(this.allTokens[this.currentTokenIndex].token == "let")
                this.compileLet()
            else if(this.allTokens[this.currentTokenIndex].token == "if")
                this.compileIf()
            else if(this.allTokens[this.currentTokenIndex].token == "while")
                this.compileWhile()
            else if(this.allTokens[this.currentTokenIndex].token == "do")
                this.compileDo()
            else if(this.allTokens[this.currentTokenIndex].token == "return")
                this.compileReturn()
        }
    }


    // doStatement: 'do'  subroutineCall ';'
    fun compileDo()
    {

       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'do'
        currentTokenIndex++
        this.compileSubroutineCall()

        this.vmWriter.writePop(Segment.TEMP, 0) // pop temp 0 : because the function return something so we put it on temp var out from
       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
        currentTokenIndex++
    }


    // letStatement: 'let'  varName ('[' expression ']')? '=' expression ';'
    fun compileLet()
    {

       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'let'
        currentTokenIndex++
        var varName = this.allTokens[currentTokenIndex].token // varName
        currentTokenIndex++
          if(this.allTokens[this.currentTokenIndex].token == "[")  // varName[expression1]=expression2
        {
           // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '['
            currentTokenIndex++

            this.CompileExpression() // expression1
            vmWriter.writePush(this.allSymbolTable.segmentOfNormalVarName(varName), this.allSymbolTable.indexOf(varName))
            vmWriter.writeArithmetic(Command.ADD)  //  *(expression1+varName)= top stack value = RAM address of varName[expression1]

            // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ']'
            currentTokenIndex++
            // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '='
            currentTokenIndex++

            this.CompileExpression()  //expression2
            vmWriter.writePop(Segment.TEMP, 0)   // temp 0 =the value of expression2
            vmWriter.writePop(Segment.POINTER, 1) // pointer 1 (that 0) = point to the RAM address of varName[expression1]
            vmWriter.writePush(Segment.TEMP, 0)  // top stack value = expression2
            vmWriter.writePop(Segment.THAT, 0)  // that 0 =the value of expression2

            // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
            currentTokenIndex++
        }
        else {  // varName '=' expression ';'
              // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '='
              currentTokenIndex++
              this.CompileExpression()
               // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
              currentTokenIndex++
              vmWriter.writePop(this.allSymbolTable.segmentOfNormalVarName(varName), this.allSymbolTable.indexOf(varName))
        }
    }

    // whileStatement: 'while' '(' expression ')' '{' statements '}'
    fun compileWhile()
    {

     //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'while'
        currentTokenIndex++
     //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
        currentTokenIndex++
        var label_1 = "WHILE_EXP${this.whileExpIndex}"
        this.whileExpIndex++
        var label_2 = "WHILE_END${this.whileEndIndex}"
        this.whileEndIndex++

        this.vmWriter.writeLabel(label_1)  //label L1
        this.CompileExpression()   // compiled (expression)
        this.vmWriter.writeArithmetic(Command.NOT)  // not
        this.vmWriter.writeIf(label_2) // if-goto L2

       // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
        currentTokenIndex++
      //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++
        this.compileStatements()  // compiled (statements)
        this.vmWriter.writeGoto(label_1)  // goto L1
        this.vmWriter.writeLabel(label_2)  // label L2

        //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
    }

    // ReturnStatement 'return'  expression? ';'
    fun compileReturn()
    {

        //this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'return'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token != ";")
        {
            this.CompileExpression()
            // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
            currentTokenIndex++
            this.vmWriter.writeReturn()
        }
        else {
            // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ';'
            currentTokenIndex++
            this.vmWriter.writePush(Segment.CONST, 0)
            this.vmWriter.writeReturn()
        }
    }


    //  ifStatement: 'if' '(' expression ')' '{' statements '}'  ( 'else' '{' statements '}' )?
    fun compileIf()
    {

      //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'if'
        currentTokenIndex++
      //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
        currentTokenIndex++
        var label_1 = "IF_TRUE${this.ifTrueIndex}"
        this.ifTrueIndex++
        var label_2: String = "IF_FALSE${this.ifFalseIndex}"
        this.ifFalseIndex++

        this.CompileExpression()  //   compiled (expression)

        //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
        currentTokenIndex++
     //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
        currentTokenIndex++

        this.vmWriter.writeIf(label_1) // if-goto IF_TRUE
        this.vmWriter.writeGoto(label_2)  // goto IF_FALSE
        this.vmWriter.writeLabel(label_1)  //  label IF_TRUE

        this.compileStatements()  //  compiled (statements1)

      //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
        currentTokenIndex++
        if(this.allTokens[this.currentTokenIndex].token == "else") // have "else"
        {
       //     this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // 'else'
            currentTokenIndex++
       //     this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '{'
            currentTokenIndex++

            var label_3 = "IF_END${this.ifEndIndex}"
            this.ifEndIndex++

            this.vmWriter.writeGoto(label_3)  // goto IF_END
            this.vmWriter.writeLabel(label_2)  //  label IF_FALSE

            this.compileStatements()  // compiled (statements2)
            this.vmWriter.writeLabel(label_3)  //label IF_END

         //   this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '}'
            currentTokenIndex++
        }
        else { //not have "else"
            this.vmWriter.writeLabel(label_2)  //label IF_FALSE
        }
    }


    // expression: term (op term)*
    fun CompileExpression()
    {

        this.CompileTerm()
        while (this.allTokens[this.currentTokenIndex].token != "]" && this.allTokens[this.currentTokenIndex].token != ";" &&
            this.allTokens[this.currentTokenIndex].token != ")" && this.allTokens[this.currentTokenIndex].token != "," )
        {
            var op = this.allTokens[currentTokenIndex].token // op
            currentTokenIndex++
            this.CompileTerm()
            this.vmWriter.writeArithmeticFromStringOp(op)
        }

    }


     //  term: integerConstant | stringConstant | keywordConstant | varName |  varName '[' expression ']' |
     //  subroutineCall  | '(' expression ')' | unaryOp term
    fun CompileTerm()
    {

        if(this.allTokens[this.currentTokenIndex].token == "-" || this.allTokens[this.currentTokenIndex].token == "~" )  //  unaryOp term
        {
            var unaryOp= this.allTokens[currentTokenIndex].token // unaryOp
            currentTokenIndex++
            this.CompileTerm()
            when (unaryOp)
            {
                "-"  -> this.vmWriter.writeArithmetic(Command.NEG)
                "~" -> this.vmWriter.writeArithmetic(Command.NOT)
            }
        }
       else if(this.allTokens[this.currentTokenIndex].token == "(")  // '(' expression ')'
        {
            //this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            this.CompileExpression()
           // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else if(this.allTokens[(this.currentTokenIndex+1)].token == "(" || this.allTokens[(this.currentTokenIndex+1)].token == ".")  // subroutineCall
            this.compileSubroutineCall()
          else  // integerConstant | stringConstant | keywordConstant | varName |  varName '[' expression ']'
        {
            var token = this.allTokens[currentTokenIndex] // integerConstant|stringConstant|keywordConstant|varName
            currentTokenIndex++
            if(this.allTokens[(this.currentTokenIndex)].token == "[")  // token = varName and next tokens='[' expression ']'
            {
                //this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '['
                currentTokenIndex++
                vmWriter.writePush(this.allSymbolTable.segmentOfNormalVarName(token.token), this.allSymbolTable.indexOf(token.token))
                this.CompileExpression()
                vmWriter.writeArithmetic(Command.ADD)  //  *(expression+varName)= top stack value = RAM address of varName[expression1]
                vmWriter.writePop(Segment.POINTER, 1) // pointer 1 (that 0) = point to the RAM address of varName[expression1]
                vmWriter.writePush(Segment.THAT, 0)  // that 0 =the value of expression2 ?????pop?push

                // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ']'
                currentTokenIndex++

            }
            else  //token = integerConstant | stringConstant | keywordConstant | varName
            {
                when(token.type)
                {
                    "integerConstant" -> {this.vmWriter.writePush(Segment.CONST, token.token.toInt() )}
                    "stringConstant" -> {this.vmWriter.writePushString(token.token)}
                    "keyword" -> {
                        // 'true' | 'false' | 'null' | 'this'
                        when (token.token)
                        {
                            "true"  -> {
                                this.vmWriter.writePush(Segment.CONST, 1)
                                this.vmWriter.writeArithmetic(Command.NEG)
                            }
                            "false" -> {this.vmWriter.writePush(Segment.CONST, 0)}
                            "null"  -> {this.vmWriter.writePush(Segment.CONST, 0)}
                            "this"  -> {this.vmWriter.writePush(Segment.POINTER, 0)}
                        }
                    }
                    "identifier" ->
                    {
                        vmWriter.writePush(this.allSymbolTable.segmentOfNormalVarName(token.token), this.allSymbolTable.indexOf(token.token))
                    }
                }
            }

        }


    }


// expressionList: (expression (',' expression)* )?
    fun CompileExpressionList() : Int
    {

       var nArg : Int = 0

        if(this.allTokens[this.currentTokenIndex].token != ")")
        {
            this.CompileExpression()
            nArg++
            while (this.allTokens[this.currentTokenIndex].token == ",")
            {
              //  this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ','
                currentTokenIndex++
                this.CompileExpression()
                nArg++
            }
        }
        return nArg
    }


    // subroutineCall: subroutineName '(' expressionList ')' |
    // ( className | varName) '.' subroutineName  '(' expressionList ')'
    fun compileSubroutineCall()
    {

         if(this.allTokens[(this.currentTokenIndex+1)].token == "(") // subroutineName '(' expressionList ')'
        {
            var subroutineName = this.allTokens[currentTokenIndex].token // subroutineName
            currentTokenIndex++
          //  this.allParser += space + oneWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++

            this.vmWriter.writePush(Segment.POINTER, 0)  // because this is method
          var nArg =  this.CompileExpressionList()
            this.vmWriter.writeCall(this.allSymbolTable.currentClassName+"."+subroutineName, (nArg+1))

            // this.allParser += space + oneWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }
        else  // ( className | varName) '.' subroutineName  '(' expressionList ')'
        {
            var classNameOrVarName = this.allTokens[currentTokenIndex].token // className | varName
            currentTokenIndex++
            var dot= this.allTokens[currentTokenIndex].token // '.'
            currentTokenIndex++
            var subroutineName = this.allTokens[currentTokenIndex].token // subroutineName
            currentTokenIndex++
           // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // '('
            currentTokenIndex++
            var type = this.allSymbolTable.typeOf(classNameOrVarName)
            if(type.equals("NONE"))  // classNameOrVarName is current class or other class and subroutineName is function (static)
            {
                var nArg =  this.CompileExpressionList()
                this.vmWriter.writeCall(classNameOrVarName+dot+subroutineName, nArg)
            }
            else{  // classNameOrVarName is object of other class and subroutineName is method
                vmWriter.writePush(
                    this.allSymbolTable.segmentOfNormalVarName(classNameOrVarName),
                    this.allSymbolTable.indexOf(classNameOrVarName))
                var nArg =  this.CompileExpressionList()
                this.vmWriter.writeCall(type+dot+subroutineName, (nArg+1))
            }
           // this.allParser += space + twoWhiteSpaces + this.allTokens[currentTokenIndex].toXmlString() // ')'
            currentTokenIndex++
        }

    }


    fun isIfElse(ifIndex: Int): Boolean {
        var isIfElse: Boolean = false
        var startSum: Int = 1
        var endSum: Int = 0
        var i:Int = ifIndex

        while (!startSum.equals(endSum))
        {
            if(this.allTokens[i].token.equals("{"))
                startSum++
            else if(this.allTokens[i].token.equals("}"))
                endSum++
            i++
        }
        if(this.allTokens[i].token.equals("else"))
            isIfElse = true

        return isIfElse
    }

}