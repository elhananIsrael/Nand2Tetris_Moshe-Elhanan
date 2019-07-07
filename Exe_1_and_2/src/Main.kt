package ex01

import java.nio.file.Paths


val PATH_TO_WORKING_DIR = Paths.get("").toAbsolutePath().toString() + "\\"
const val NAME_OF_FILE = "SimpleFunction"
const val EXT_OF_VM_FILE = ".vm"
const val EXT_OF_ASM_FILE = ".asm"

fun main (args: Array<String>) {

 //   println(PATH_VM_FILE)
  //  var pathToFile = PATH_TO_WORKING_DIR + NAME_OF_FILE
    var pathToFile = "C:\\MyProjects\\intellijProjects\\examples\\tests\\testing_exe_1_and_2\\FunctionCalls\\SimpleFunction\\" + NAME_OF_FILE

    var pathToVMFile = pathToFile + EXT_OF_VM_FILE
    var pathToASMFile = pathToFile + EXT_OF_ASM_FILE
    println(pathToVMFile)
    var codeGen = VMCodeGenerator(pathToASMFile)

    val parser = VMParser(pathToVMFile)
    val _file = pathToVMFile.split("[.]").toTypedArray()[0]
    codeGen.setFileName(_file)
    while (parser.hasMoreCommands())
    {
        parser.advance()
        val currentCT = parser.commandType()
        if (currentCT == CommandType.ARITHMETIC)
        {
            val command = parser.arg1()
            codeGen.writeArithmetic(command)
        }
        else if (currentCT == CommandType.PUSH || currentCT == CommandType.POP)
        {
            val command = parser.arg1()
            val index = parser.arg2()
            codeGen.writePushPop(currentCT, command, index)
        }
        else if (currentCT == CommandType.FUNCTION)
        {
            val fName = parser.arg1()
            val numArgs = parser.arg2()
            codeGen.writeFunction(fName, numArgs)
        }
        else if (currentCT == CommandType.RETURN)
        {
            codeGen.writeReturn()
        }
        else if (currentCT == CommandType.CALL)
        {
            val fName = parser.arg1()
            val numArgs = parser.arg2()
            codeGen.writeCall(fName, numArgs)
        }
        else if (currentCT == CommandType.LABEL)
        {
            val label = parser.arg1()
            codeGen.writeLabel(label)
        }
        else if (currentCT == CommandType.GOTO)
        {
            val label = parser.arg1()
            codeGen.writeGoto(label)
        }
        else if (currentCT == CommandType.IF)
        {
            val label = parser.arg1()
            codeGen.writeIf(label)
        }
        else
        {
            println("Error:  Reading VM Command")
        }
    }
    test()
    println("hello world!!!")
}