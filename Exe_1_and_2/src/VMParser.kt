package ex01
import java.io.File
import java.util.Scanner;


class VMParser(filename:String) {
    private var inputFile: File = File(filename)
    private var input : Scanner = Scanner(inputFile)
    private var command:String
    private var aPattern = "^(add|sub|neg|eq|gt|lt|and|or|not).*"
    private var pushPattern = "^push.*"
    private var popPattern = "^pop.*"
    private var labelPattern = "^label.*"
    private var gotoPattern = "^goto.*"
    private var ifPattern = "^if.*"
    private var functionPattern = "^function.*"
    private var returnPattern = "^return.*"
    private var callPattern = "^call.*"
    init{
        command = ""
    }

    //check if VM file has more commands
    fun hasMoreCommands():Boolean {
        return when (input.hasNextLine()) {
            true -> true
            false -> {input.close()
                false
            }
        }
    }

    //get next command
    fun advance() {
        var currentLine:String
        do
        {
            currentLine = input.nextLine().trim()
        }
        while ((currentLine == "" || currentLine.substring(0, 2) == "//"))
        val parts = currentLine.split("//").toTypedArray()
        command = parts[0]
    }

    //get command type
    fun commandType():CommandType {
        return when {
            command.matches((aPattern).toRegex()) -> CommandType.ARITHMETIC
            command.matches((pushPattern).toRegex()) -> CommandType.PUSH
            command.matches((popPattern).toRegex()) -> CommandType.POP
            command.matches((labelPattern).toRegex()) -> CommandType.LABEL
            command.matches((gotoPattern).toRegex()) -> CommandType.GOTO
            command.matches((ifPattern).toRegex()) -> CommandType.IF
            command.matches((functionPattern).toRegex()) -> CommandType.FUNCTION
            command.matches((returnPattern).toRegex()) -> CommandType.RETURN
            command.matches((callPattern).toRegex()) -> CommandType.CALL
            else -> CommandType.ERROR
        }
    }

//get first argument
    fun arg1(): String {
        var result = ""
        val type = commandType()
        if (type == CommandType.ARITHMETIC)
        {
            result = command
        }
        else if (type != CommandType.RETURN) {
            result = command.split(" ").toTypedArray()[1]
        }
        return result
    }

    //get second argument
    fun arg2():Int {
        val type = commandType()
        var result = ""
        if ((type == CommandType.PUSH || type == CommandType.POP
                        || type == CommandType.FUNCTION || type == CommandType.CALL))
        {
            result = command.split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[2]
        }
        if (!isNumeric(result)) {
            return -1
        }
        return Integer.parseInt(result)
    }

    private fun isNumeric(str:String):Boolean {
        if (str.isEmpty()) {
            return false
        }
        for (c in str.toCharArray())
        {
            if (!Character.isDigit(c)) return false
        }
        return true
    }


}