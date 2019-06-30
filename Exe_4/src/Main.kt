import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.io.File


fun main() {

    try
    {
        println("Please enter input dir path:")
        var inputDirPath = readLine()!!

        println("Please enter output dir path:")
        var outputDirPath = readLine()

        File(inputDirPath).walkTopDown().forEach {
            if (it.isFile)
                if (File(it.name).extension == "jack") {

                    var outputFileTokenizer = outputDirPath + '\\' + File(it.name).nameWithoutExtension + "T.xml"
                    if (File(outputFileTokenizer).exists()) {
                        File(outputFileTokenizer).delete()
                    }

                    var tokenizer: JackTokenizer = JackTokenizer(it.path)
                    tokenizer.writeTokInOutputFile(outputFileTokenizer)

                    var outputFileParser = outputDirPath + '\\' + File(it.name).nameWithoutExtension + ".xml"
                    if (File(outputFileParser).exists()) {
                        File(outputFileParser).delete()
                    }

                    var parser: CompilationEngine = CompilationEngine(outputFileTokenizer, outputFileParser)

                }
        }
    }
catch (e:IOException) {
    println("IOException: error.")
}
}

//C:\MyProjects\intellijProjects\examples\tests\exe4Test         //input test
//C:\MyProjects\intellijProjects\examples\tests\exe4Output       //output