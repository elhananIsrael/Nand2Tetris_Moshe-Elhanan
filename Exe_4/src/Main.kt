import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.io.File


fun main() {

    var  h: JackTokenizer = JackTokenizer("C:\\MyProjects\\intellijProjects\\examples\\tests\\exe4Test\\Main.jack")



    File("C:/MyProjects/intellijProjects/examples/tests/exe4Output/output2T.xml").writeText(h.allTokXml) // write Txml file


    var  h2: CompilationEngine = CompilationEngine(
        "C:/MyProjects/intellijProjects/examples/tests/exe4Output/output2T.xml",
        "C:/MyProjects/intellijProjects/examples/tests/exe4Output/output22.xml")



}