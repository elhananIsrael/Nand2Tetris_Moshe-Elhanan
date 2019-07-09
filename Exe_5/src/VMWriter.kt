package ex05

import java.io.File

class VMWriter {

    var myOutputVMFile : File


    constructor(outputVMFilePath :String)
    {
        myOutputVMFile = File(outputVMFilePath)
    }


    fun writeText(text: String)
    {
        this.myOutputVMFile.appendText(text)
    }

    fun writePush(segment: Segment, index: Int)
    {
        when (segment)
        {
            Segment.CONST -> this.myOutputVMFile.appendText("push constant " + index.toString() + "\n")
            Segment.ARG -> this.myOutputVMFile.appendText("push argument " + index.toString() + "\n")
            Segment.LOCAL -> this.myOutputVMFile.appendText("push local " + index.toString() + "\n")
            Segment.STATIC -> this.myOutputVMFile.appendText("push static " + index.toString() + "\n")
            Segment.THIS -> this.myOutputVMFile.appendText("push this " + index.toString() + "\n")
            Segment.THAT -> this.myOutputVMFile.appendText("push that " + index.toString() + "\n")
            Segment.POINTER -> this.myOutputVMFile.appendText("push pointer " + index.toString() + "\n")
            Segment.TEMP -> this.myOutputVMFile.appendText("push temp " + index.toString() + "\n")
        }
    }

    fun writePop(segment: Segment, index: Int)
    {
        when (segment)
        {
            Segment.CONST -> this.myOutputVMFile.appendText("pop constant " + index.toString() + "\n")
            Segment.ARG -> this.myOutputVMFile.appendText("pop argument " + index.toString() + "\n")
            Segment.LOCAL -> this.myOutputVMFile.appendText("pop local " + index.toString() + "\n")
            Segment.STATIC -> this.myOutputVMFile.appendText("pop static " + index.toString() + "\n")
            Segment.THIS -> this.myOutputVMFile.appendText("pop this " + index.toString() + "\n")
            Segment.THAT -> this.myOutputVMFile.appendText("pop that " + index.toString() + "\n")
            Segment.POINTER -> this.myOutputVMFile.appendText("pop pointer " + index.toString() + "\n")
            Segment.TEMP -> this.myOutputVMFile.appendText("pop temp " + index.toString() + "\n")
        }
    }

    fun writeArithmetic(command: Command)
    {
        when (command)
        {
            Command.ADD  -> this.myOutputVMFile.appendText("add\n")
            Command.SUB -> this.myOutputVMFile.appendText("sub\n")
            Command.NEG -> this.myOutputVMFile.appendText("neg\n")
            Command.EQ -> this.myOutputVMFile.appendText("eq\n")
            Command.GT -> this.myOutputVMFile.appendText("gt\n")
            Command.LT -> this.myOutputVMFile.appendText("lt\n")
            Command.AND -> this.myOutputVMFile.appendText("and\n")
            Command.OR -> this.myOutputVMFile.appendText("or\n")
            Command.NOT -> this.myOutputVMFile.appendText("not\n")
        }
    }

    fun writeArithmeticFromStringOp(op: String) {
        when (op)
        {
            "+"  -> this.myOutputVMFile.appendText("add\n")
            "-" -> this.myOutputVMFile.appendText("sub\n")
            "*" -> this.myOutputVMFile.appendText("call Math.multiply 2\n")
            "/" -> this.myOutputVMFile.appendText("call Math.divide 2\n")
            "&amp;" -> this.myOutputVMFile.appendText("and\n")
            "|" -> this.myOutputVMFile.appendText("or\n")
            "&lt;" -> this.myOutputVMFile.appendText("lt\n")
            "&gt;" -> this.myOutputVMFile.appendText("gt\n")
            "=" -> this.myOutputVMFile.appendText("eq\n")
        }
    }

    fun writeLabel(label: String)
    {
        this.myOutputVMFile.appendText("label " +label+"\n")
    }

    fun writeGoto(label: String)
    {
        this.myOutputVMFile.appendText("goto " +label+"\n")
    }

    fun writeIf(label: String)
    {
        this.myOutputVMFile.appendText("if-goto " +label+"\n")
    }

    fun writeCall(name: String, nArgs: Int)
    {
        this.myOutputVMFile.appendText("call ${name} ${nArgs}\n")
    }

    fun writeFunction(name: String, nLocals: Int)
    {
        this.myOutputVMFile.appendText("function ${name} ${nLocals}\n")
    }

    fun writeReturn()
    {
        this.myOutputVMFile.appendText("return\n")
    }

    fun writePushString(myString: String)
    {

        this.writePush(Segment.CONST, myString.length)  //lenght of string
        this.writeCall("String.new", 1)
        for (char in myString) {
            this.writePush(Segment.CONST, char.toInt())
            this.writeCall("String.appendChar", 2)

        }
      //  this.writeCall("Output.printString", 1)
       // this.writePop(Segment.TEMP, 0)
    }

  /**  fun close()    {
    }*/

}

