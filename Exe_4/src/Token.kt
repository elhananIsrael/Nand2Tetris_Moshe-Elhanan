class Token {

    var token: String = ""
    var type: String = ""

    fun Token(type: String, tok: String) {
        this.type = type
        this.token = tok
    }

    fun Token() {
        this.type = ""
        this.token = ""
    }

    fun toXmlString() : String
    {
        var XmlTok : String
        if(this.token.equals("<") && this.type.equals("symbol"))
            XmlTok ="<" + this.type + "> " +"&lt;" + " </" + this.type + ">\n"

        else if(this.token.equals(">") && this.type.equals("symbol") )
            XmlTok ="<" + this.type + "> " +"&gt;" + " </" + this.type + ">\n"

        else if(this.token.equals('\"') && this.type.equals("symbol") )
            XmlTok ="<" + this.type + "> " +"&quot;" + " </" + this.type + ">\n"

        else if(this.token.equals("&") && this.type.equals("symbol") )
            XmlTok ="<" + this.type + "> " +"&amp;" + " </" + this.type + ">\n"

        else XmlTok ="<" + this.type + "> " +this.token + " </" + this.type + ">\n"

        return  XmlTok
    }

}