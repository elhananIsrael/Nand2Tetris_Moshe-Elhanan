package ex04

import java.io.IOException
import java.io.File
import java.nio.file.Path


fun main() {

    try
    {
        /** println("Please enter input dir path:")
        var inputDirPath = readLine()!!*/
        var inputDirPath= "tests\\4\\input"

        /**println("Please enter output dir path:")
        var outputDirPath = readLine()*/
        var outputDirPath= "tests\\4\\output"


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

                    var outputFileParser = outputDirPath + '\\' + File(it.name).nameWithoutExtension + ".xml"
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

// tests\4\input         //input test
// tests\4\output      //output