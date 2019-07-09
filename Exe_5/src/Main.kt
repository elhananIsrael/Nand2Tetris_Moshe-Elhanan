package ex05

import java.io.IOException
import java.io.File
import java.nio.file.Path


fun main() {

    try
    {
       /** println("Please enter input dir path:")
        var inputDirPath = readLine()!!*/
        var inputDirPath= "tests\\5\\input"

        /**println("Please enter output dir path:")
        var outputDirPath = readLine()*/
        var outputDirPath= "tests\\5\\output"


        File(inputDirPath).walkTopDown().forEach {
            if (it.isFile) {
                var inputFileTokenizerFile = File(it.absolutePath)
                if (inputFileTokenizerFile.extension == "jack") {

                    var outputFileTokenizer = outputDirPath + '\\' + File(it.name).nameWithoutExtension + "T.xml"
                    // var inputFileTokenizerFile = File(outputFileTokenizer)
                    var outputFileTokenizerFile = File(outputFileTokenizer)
                    if (outputFileTokenizerFile.exists()) {
                        outputFileTokenizerFile.delete()
                    }

                    // var tokenizer: JackTokenizer = JackTokenizer(it.path)
                    var tokenizer: JackTokenizer = JackTokenizer(inputFileTokenizerFile)

                    tokenizer.writeTokInOutputFile(outputFileTokenizerFile)

                    var outputFileParser = outputDirPath + '\\' + File(it.name).nameWithoutExtension + ".vm"
                    var outputFileParserFile = File(outputFileParser)

                    if (outputFileParserFile.exists()) {
                        outputFileParserFile.delete()
                    }

                    var parser: CompilationEngine = CompilationEngine(outputFileTokenizerFile, outputFileParserFile)

                }
            }
        }
    }
catch (e:IOException) {
    println("IOException: error.")
}
}

//  tests\‏‏exe_5\input         //input test
//  tests\exe_5\output      //output