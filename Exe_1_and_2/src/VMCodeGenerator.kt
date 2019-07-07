package ex01
import java.io.File

import java.util.Hashtable

class VMCodeGenerator(out:String) {
    private val EQ = 0
    private val GT = 1
    private val LT = 2
    private var eqCounter:Int = 0
    private var gtCounter:Int = 0
    private var ltCounter:Int = 0
    private var returnCounter:Int = 0
    private val ramName:Hashtable<String, String>
    private val output: File
    private var outputName = "sample.asm"
    private var functionName = ""
    private var filename:String = ""


    init{
        outputName = out
        output = File(outputName)
        ramName = Hashtable<String, String>()
        ramName["local"] =  "LCL"
        ramName["argument"] = "ARG"
        ramName["this"] = "THIS"
        ramName["that"] = "THAT"
        ramName["temp"] = "5"
    }

    fun setFileName(fn:String) {
        filename = fn
        functionName = fn
        if (filename == "Sys")
            writeInit()
    }

        // Arithmetics
    fun writeDebug() {
        output.appendText("@7777")
        output.appendText("M=0")
        output.appendText("M=1")
    }

    // Push and Pop
    fun writePushPop(command:CommandType, seg:String, index:Int) {
        var segment = seg
        if (segment == "pointer")
        {
            if (index == 0) {
                segment = "THIS"
            }
            if (index == 1) {
                segment = "THAT"
            }
            writePointerPushPopCommand(command, segment)
            return
        }
        if (ramName.containsKey(segment))
        {
            segment = ramName.get(segment)!!
        }
        if (command == CommandType.PUSH)
        {
            writePushCommand(segment, index)
        }
        else if (command == CommandType.POP)
        {
            writePopCommand(segment, index)
        }
        else
        {
            return
        }
    }

        // Arithmetics
    fun writeArithmetic(command:String) {
        if (command.matches(("^add.*").toRegex()))
        {
            writeAddCommand()
        }
        else if (command.matches(("^sub.*").toRegex()))
        {
            writeSubCommand()
        }
        else if (command.matches(("^neg.*").toRegex()))
        {
            writeNegCommand()
        }
        else if (command.matches(("^eq.*").toRegex()))
        {
            writeEqCommand()
        }
        else if (command.matches(("^gt.*").toRegex()))
        {
            writeGtCommand()
        }
        else if (command.matches(("^lt.*").toRegex()))
        {
            writeLtCommand()
        }
        else if (command.matches(("^and.*").toRegex()))
        {
            writeAndCommand()
        }
        else if (command.matches(("^or.*").toRegex()))
        {
            writeOrCommand()
        }
        else if (command.matches(("^not.*").toRegex()))
        {
            writeNotCommand()
        }
        else
        {
            return
        }
    }

    private fun writeAddCommand() {
        writeBasicArithCommand("M=D+M")
    }
    private fun writeSubCommand() {
        writeBasicArithCommand("M=M-D")
    }
    private fun writeAndCommand() {
        writeBasicArithCommand("M=D&M")
    }
    private fun writeOrCommand() {
        writeBasicArithCommand("M=D|M")
    }
    private fun writeBasicArithCommand(insert:String) {
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1"+ "\n")
        output.appendText("D=M" + "\n")
        output.appendText("A=A-1" + "\n")
        output.appendText(insert + "\n")
        output.appendText("D=A+1" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=D" + "\n")
    }
    private fun writeNotCommand() {
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("M=!M" + "\n")
    }
    private fun writeNegCommand() {
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@0" + "\n")
        output.appendText("D=A-D" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("M=D" + "\n")
    }

    // Eq, Gt and Lt
    private fun writeEqCommand() {
        writeLogicCommand(EQ)
    }
    private fun writeGtCommand() {
        writeLogicCommand(GT)
    }
    private fun writeLtCommand() {
        writeLogicCommand(LT)
    }

    private fun writeLogicCommand(logic:Int) {
        var trueTag = "TRUE_"
        var continueTag = "CON_"
        var command:String = ""
        if (logic == EQ)
        {
            trueTag += ("EQ_" + Integer.toString(eqCounter))
            continueTag += ("EQ_" + Integer.toString(eqCounter++))
            command = "D;JEQ"
        }
        else if (logic == GT)
        {
            trueTag += ("GT_" + Integer.toString(gtCounter))
            continueTag += ("GT_" + Integer.toString(gtCounter++))
            command = "D;JGT"
        }
        else if (logic == LT)
        {
            trueTag += ("LT_" + Integer.toString(ltCounter))
            continueTag += ("LT_" + Integer.toString(ltCounter++))
            command = "D;JLT"
        }
        writeBasicLogicCommand(trueTag, continueTag, command)
    }
    internal fun writeBasicLogicCommand(trueTag:String, continueTag:String,
                                        command:String) {
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("A=A-1" + "\n")
        output.appendText("D=M-D" + "\n")
        output.appendText("@R13" + "\n")
        output.appendText("M=D" + "\n") // R13 has the stack substraction
        output.appendText("@SP" + "\n")
        output.appendText("D=M-1" + "\n")
        output.appendText("@R14" + "\n")
        output.appendText("M=D" + "\n") // R14 has the stack address
        output.appendText("@0" + "\n")
        output.appendText("D=A" + "\n")
        output.appendText("@R14" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("M=D" + "\n")
        output.appendText("@R13" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@" + trueTag + "\n")
        output.appendText(command + "\n")
        output.appendText("@" + continueTag + "\n")
        output.appendText("0;JMP" + "\n")
        output.appendText("(" + trueTag + ")" + "\n")
        output.appendText("@0" + "\n")
        output.appendText("D=!A" + "\n")
        output.appendText("@R14" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("M=D" + "\n")
        output.appendText("(" + continueTag + ")" + "\n")
        output.appendText("@R14" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=D" + "\n")
    }

     private fun writePointerPushPopCommand(command:CommandType, segment:String) {
        if (command == CommandType.PUSH)
        {
            output.appendText("@$segment\n")
            output.appendText("D=M" + "\n")
            output.appendText("@SP" + "\n")
            output.appendText("A=M" + "\n")
            output.appendText("M=D" + "\n")
            output.appendText("D=A+1" + "\n")
            output.appendText("@SP" + "\n")
            output.appendText("M=D" + "\n")
        }
        else if (command == CommandType.POP)
        {
            output.appendText("@SP" + "\n")
            output.appendText("A=M-1" + "\n")
            output.appendText("D=M" + "\n")
            output.appendText("@$segment\n")
            output.appendText("M=D" + "\n")
            output.appendText("@SP" + "\n")
            output.appendText("D=M-1" + "\n")
            output.appendText("@SP" + "\n")
            output.appendText("M=D" + "\n")
        }
    }
    private fun writePushCommand(seg:String, index:Int) {
        var segment = seg
        if (segment == "constant")
        {
            output.appendText("@$index\n")
            output.appendText("D=A" + "\n")
            output.appendText("@SP" + "\n")
            output.appendText("A=M" + "\n")
            output.appendText("M=D" + "\n")
//            output.appendText("D=A+1" + "\n")
            output.appendText("@SP" + "\n")
//            output.appendText("M=D" + "\n")
            output.appendText("M=M+1" + "\n")
            return
        }
        if (segment == "static")
        {
//            segment = filename + "." + Integer.toString(index)
            segment = filename.substring(filename.lastIndexOf('\\') + 1) + "." + Integer.toString(index)
        }
        output.appendText("@$segment\n")
        if (segment == "5")
        {
            output.appendText("D=A" + "\n")
        }
        else
        {
            output.appendText("D=M" + "\n")
        }
        if (!segment.contains(filename))
        {
            output.appendText("@$index\n")
            output.appendText("A=D+A" + "\n")
            output.appendText("D=M" + "\n")
        }
        output.appendText("@SP" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("M=D" + "\n")
//        output.appendText("D=A+1" + "\n")
        output.appendText("@SP" + "\n")
//        output.appendText("M=D" + "\n")
        output.appendText("M=M+1" + "\n")
    }
    private fun writePopCommand(seg:String, index:Int) {
        var segment = seg
        if (segment == "static")
        {
            segment = filename.substring(filename.lastIndexOf('\\') + 1) + "." + Integer.toString(index)
        }
        output.appendText("@$segment\n") // locate segment register A
        if (segment == "5" || segment.contains(filename))
        {
            output.appendText("D=A" + "\n")
        }
        else
        {
            output.appendText("D=M" + "\n") // read in segments address D
        }
        if (!segment.contains(filename))
        {
            output.appendText("@$index\n") // A has the index
            output.appendText("D=D+A" + "\n") // D has the right address
        }
        output.appendText("@R13" + "\n") // A has temp register
        output.appendText("M=D" + "\n") // temp = right address
        output.appendText("@SP" + "\n") // A has the stack pointer register
        output.appendText("A=M" + "\n") // A has the sp memory address
        output.appendText("A=A-1" + "\n") // A has the right address
        output.appendText("D=M" + "\n") // D has the data needed to be popped
        output.appendText("@R13" + "\n") // A has R13 register
        output.appendText("A=M" + "\n") // A has R13's stored memory address
        output.appendText("M=D" + "\n") // save the value to the right segment
        output.appendText("@SP" + "\n") // load SP again
        output.appendText("A=M" + "\n") // SP address again
        output.appendText("A=A-1" + "\n") // last item on the stack's address
        output.appendText("D=A" + "\n") // D has the SP address now
        output.appendText("@SP" + "\n") // a has the SP register
        output.appendText("M=D" + "\n") // sp register stores the right address
    }

    fun writeInit() {
        // Set up SP address
        output.appendText("@256" + "\n")
        output.appendText("D=A" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=D" + "\n")
        // calll Sys.init
        functionName = "Sys.init"
        writeCall(functionName, 0)
    }
    fun writeLabel(label:String) {
        val labelName = generateLabel(label)
        output.appendText("(" + labelName + ")" + "\n")
    }
    fun writeGoto(label:String) {
        val labelName = generateLabel(label)
        output.appendText("@$labelName\n")
        output.appendText("0;JMP" + "\n")
    }
    fun writeIf(label:String) {
        val labelName = generateLabel(label)
        output.appendText("@SP" + "\n")
        output.appendText("M=M-1" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@0" + "\n")
        output.appendText("D=D-A" + "\n")
        output.appendText("@$labelName\n")
        output.appendText("D;JNE" + "\n")
    }
    private fun generateLabel(label:String):String {
        return (functionName + "$" + label)
    }
    fun writeCall(fName:String, numArgs:Int) {
        val returnName = "RETURN" + "_" + Integer.toString(returnCounter++)
        // Push return address
        output.appendText("@$returnName\n")
        output.appendText("D=A" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("M=D" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=M+1" + "\n")
        // Push local, argument, this and that
        writePushAddresses("LCL")
        writePushAddresses("ARG")
        writePushAddresses("THIS")
        writePushAddresses("THAT")
        // Calculate ARG = SP - n - 5
        output.appendText("@SP" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@$numArgs\n" )
        output.appendText("D=D-A" + "\n")
        output.appendText("@5" + "\n")
        output.appendText("D=D-A" + "\n")
        output.appendText("@ARG" + "\n")
        output.appendText("M=D" + "\n")
        // LCL = SP
        output.appendText("@SP" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@LCL" + "\n")
        output.appendText("M=D" + "\n")
        // goto f
        output.appendText("@$fName\n")
        output.appendText("0;JMP" + "\n")
        // (return-address)
        output.appendText("(" + returnName + ")" + "\n")
    }
    private fun writePushAddresses(seg:String) {
        output.appendText("@$seg\n")
        output.appendText("D=M" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("M=D" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=M+1" + "\n")
    }
    fun writeReturn() {
        // frame = LCL
        output.appendText("@LCL" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@FRAME" + "\n")
        output.appendText("M=D" + "\n")
        // RET = *(FRAME-5)
        output.appendText("@5" + "\n")
        output.appendText("A=D-A" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@RET" + "\n")
        output.appendText("M=D" + "\n")
        // *ARG = pop()
        //writePushPop(CT.C_POP, "argument", 0);
        output.appendText("@SP" + "\n")
        output.appendText("A=M-1" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@ARG" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("M=D" + "\n")
        // SP = ARG + 1
        output.appendText("@ARG" + "\n")
        output.appendText("D=M+1" + "\n")
        output.appendText("@SP" + "\n")
        output.appendText("M=D" + "\n")
        writeRestoreValues("THAT", 1)
        writeRestoreValues("THIS", 2)
        writeRestoreValues("ARG", 3)
        writeRestoreValues("LCL", 4)
        // goto RET
        output.appendText("@RET" + "\n")
        output.appendText("A=M" + "\n")
        output.appendText("0;JMP" + "\n")
    }
    private fun writeRestoreValues(target:String, offset:Int) {
        output.appendText("@$offset\n")
        output.appendText("D=A" + "\n")
        output.appendText("@FRAME" + "\n")
        output.appendText("A=M-D" + "\n")
        output.appendText("D=M" + "\n")
        output.appendText("@$target\n")
        output.appendText("M=D" + "\n")
    }
    fun writeFunction(fName:String, numLocals:Int) {
        functionName = fName
        output.appendText("(" + fName + ")" + "\n")
        for (i in 0 until numLocals)
            writePushPop(CommandType.PUSH, "constant", 0)
    }
//    companion object {
//        internal var EQ = 0
//        internal var GT = 1
//        internal var LT = 2
//    }
}
