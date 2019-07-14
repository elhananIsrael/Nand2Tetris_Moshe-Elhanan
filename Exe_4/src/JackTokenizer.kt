package ex04

import java.util.*
import java.io.*
import java.io.File
import kotlin.text.replace
import java.util.regex.Pattern

class JackTokenizer {

    var inputJackFile : File

    var input: String = ""
    var startFirstToken: Int = 0
    var endFirstToken: Int = 0
    var startStringToken: Int = 0
    var endStringToken: Int = 0
    var isHaveToken: Boolean = false
    var isHaveFirstToken: Boolean = false
    var isHaveStartStringToken: Boolean = false
    var currentToken : Token = Token()
    var allTokens : ArrayList<Token> = ArrayList()
    var allTokXml : String = ""

    val kwds = arrayOf("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean",
        "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")

    val syms = arrayOf("{", "}", "(", ")", "[", "]", ".", ",", ";", "+", "-",
        "*", "/", "&", "|", "<", ">", "=", "~")


    constructor( inputJackFile :File   )
    {
        //this.inputJackFilePath = inputJackFilePath
        this.inputJackFile = inputJackFile
        this.input =  this.inputJackFile.readText()


        try
        {

            var allCommentsPtrn : Pattern  = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)".toPattern()
            this.input= this.input.replace( Regex(allCommentsPtrn.toString()), " ")
            this.input= this.input.replace( Regex("[\\s]+")," ")

        }
        catch (e:IOException) {
            println("IOException: file not found.")
        }
    }

    fun hasMoreTokens(): Boolean   {

        startFirstToken = -1
        isHaveFirstToken = false
        isHaveStartStringToken = false
        startStringToken = -1
        var i : Int = 0

        while (!isHaveFirstToken  && i < this.input.length) {
            if(!this.input[i].isWhitespace())
            {
                startFirstToken = i
                isHaveFirstToken = true
                if(this.input[i].equals('\"'))
                {
                    startStringToken = i
                    this.isHaveStartStringToken = true
                }
            }
            i++
        }

        return isHaveFirstToken
    }


    fun advance()
    {
        if (this.hasMoreTokens())
        {
            currentToken = Token()

            endFirstToken = -1
            endStringToken = -1
            isHaveToken = false
            var newToken: String


            for ( index_1 in this.input.indices )
            {
                if(this.endFirstToken == -1 && index_1>startFirstToken)
                {
                    if(this.isHaveFirstToken && !this.isHaveStartStringToken)
                    {
                        if(this.input[index_1].isWhitespace() || this.input[index_1].equals('\"') )
                        {
                            endFirstToken = (index_1 - 1)
                            continue
                        }

                    }
                    else if(this.isHaveStartStringToken ) //if string end now
                    {
                        if(this.input[index_1].equals('\"'))
                        {
                            this.isHaveStartStringToken = true
                            endStringToken = index_1
                            endFirstToken = index_1
                            continue
                        }
                    }

                }

            }

            newToken= this.input.substring(this.startFirstToken, (this.endFirstToken+1) )

            for (sym in this.syms) // check if symbol
            {
                if(!isHaveToken)
                {
                    if (sym == newToken[0].toString() && !isHaveToken) {
                        endFirstToken = startFirstToken
                        newToken = newToken[0].toString()
                        this.currentToken.type = "symbol"
                        this.currentToken.token = newToken
                        this.input = this.input.substring((endFirstToken + 1))
                        isHaveToken = true
                        continue
                    }
                }
            }

            if(!isHaveToken)
            {
                for (kwd in this.kwds) //check if keyword
                {
                    for (index_kwds in endFirstToken downTo startFirstToken) {
                        if (kwd == this.input.substring(startFirstToken, index_kwds+1) &&
                            (this.input[index_kwds+1].toString() in this.syms || endFirstToken.equals(index_kwds) ) && !isHaveToken ) {
                            endFirstToken = index_kwds
                            newToken = this.input.substring(startFirstToken, index_kwds+1)
                            this.currentToken.type = "keyword"
                            this.currentToken.token = newToken
                            this.input = this.input.substring((endFirstToken + 1))
                            isHaveToken = true
                        }
                    }
                }
            }

            if(!isHaveToken)
            {
                if (newToken[0].equals('\"') && !isHaveToken ) //check if string
                {
                    endFirstToken = endStringToken
                    newToken = this.input.substring((startFirstToken + 1), endFirstToken )
                    this.currentToken.type = "stringConstant"
                    this.currentToken.token = newToken
                    this.input = this.input.substring((endFirstToken + 1))
                    isHaveToken = true
                }
            }

            if(!isHaveToken)
            {
                for (index_tok in endFirstToken downTo startFirstToken) //check if integer
                {
                    if (this.input.substring(startFirstToken, index_tok+1).toIntOrNull() != null && !isHaveToken ) {
                        endFirstToken = index_tok
                        newToken = this.input.substring(startFirstToken, index_tok+1)
                        this.currentToken.type = "integerConstant"
                        this.currentToken.token = newToken
                        this.input = this.input.substring((endFirstToken + 1))
                        isHaveToken = true
                    }
                }
            }

            if(!isHaveToken)
            {
                //   if ( (newToken[0] > 'A' && newToken[0] < 'z') || (newToken[0] == '_') ) //check if identifier
                if ( (newToken[0].toChar().isLetter()) || (newToken[0] == '_') ) //check if identifier
                {
                    for (i_tok in endFirstToken downTo (startFirstToken+1) ) //check if identifier
                    {
                        if (!(this.input[i_tok].toChar().isLetterOrDigit())  && (this.input[i_tok] != '_') )
                            endFirstToken = (i_tok - 1)
                    }

                    newToken = this.input.substring(startFirstToken, (endFirstToken+1) )
                    this.currentToken.type = "identifier"
                    this.currentToken.token = newToken
                    this.input = this.input.substring((endFirstToken + 1))
                    isHaveToken = true


                }
            }

            allTokens.add(currentToken)
        }
    }


    fun tokenType (): String {
        return this.currentToken.type
    }

    fun keyWord (): String {
        if(tokenType().equals("keyword"))
            return this.currentToken.token
        else return "Unknown"
    }

    fun symbol (): String {
        if(tokenType().equals("symbol"))
            return this.currentToken.token
        else return "Unknown"
    }

    fun identifier (): String {
        if(tokenType().equals("identifier"))
            return this.currentToken.token
        else return "Unknown"
    }

    fun intVal (): String {
        if(tokenType().equals("integerConstant"))
            return this.currentToken.token
        else return "Unknown"
    }

    fun stringVal (): String {
        if(tokenType().equals("stringConstant"))
            return this.currentToken.token
        else return "Unknown"
    }

    fun writeTokInOutputFile (outputFile : File)  {

        try {
            while (this.hasMoreTokens()) {
                this.advance()
            }

            allTokXml = "<tokens>\n"

            this.allTokens.forEach {

                allTokXml += it.toXmlString()
            }

            allTokXml += "</tokens>"

            outputFile.writeText(allTokXml)// write Txml file
        }
        catch (e:IOException) {
            println("IOException: error.")
        }
    }





}