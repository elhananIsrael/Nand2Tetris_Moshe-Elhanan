class AllSymbolTables {
    var currentScope : String = ""
    var currentClassName : String = ""
    var currentSubroutineName : String = ""
    var currentSubroutineType : String = ""
    var ClassScope_SymbolTable  = arrayListOf<SymbolTable>()
    var SubroutineScope_SymbolTable  = arrayListOf<SymbolTable>()


    fun currentClassST_IndexOfKind(kind: Kind): Int
    {
        var index : Int = -1
        this.ClassScope_SymbolTable.forEach {
            if(it.kind.equals(kind) && it.index > index  )
                index = it.index        }
        index +=1
        return index
    }

    fun currentSubroutineST_IndexOfKind(kind: Kind): Int
    {
        var index : Int = -1
        this.SubroutineScope_SymbolTable.forEach {
            if(it.kind.equals(kind) && it.index > index  )
                index = it.index        }
        index +=1
        return index
    }

    fun startSubroutine(newSubroutineName: String, newSubroutineType: String)
    {
        this.SubroutineScope_SymbolTable.clear()
        currentScope= "Subroutine"
        this.currentSubroutineName = newSubroutineName
        this.currentSubroutineType = newSubroutineType
        if(newSubroutineType.equals("method"))
            this.define("this", this.currentClassName, Kind.ARG )
    }

    fun define(name: String, type: String, kind: Kind)
    {
        when (kind)
        {
            Kind.STATIC, Kind.FIELD -> {
                var index = this.currentClassST_IndexOfKind(kind)
                this.ClassScope_SymbolTable.add(SymbolTable(name, type, kind, index))
            }
            Kind.ARG, Kind.VAR -> {
                var index = this.currentSubroutineST_IndexOfKind(kind)
                this.SubroutineScope_SymbolTable.add(SymbolTable(name, type, kind, index))
            }

        }
    }

    fun varCount(kind: Kind): Int
    {
        var count: Int = 0
        when (this.currentScope)
        {
            "Subroutine" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if(it.kind.equals(kind))
                        count++       }
               if(!(this.currentSubroutineType.equals("function") && kind.equals(Kind.FIELD)) )
                this.ClassScope_SymbolTable.forEach {
                    if (it.kind.equals(kind))
                        count++       }
            }
            "Class" -> {
                this.ClassScope_SymbolTable.forEach {
                    if (it.kind.equals(kind))
                        count++       }
            }
        }
        return count
    }


    fun kindOf(name: String): Kind
    {
        when (this.currentScope)
        {
            "Subroutine" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.kind       }
            }
            "Class" -> {
                this.ClassScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.kind       }
            }
        }
        return Kind.NONE
    }


    fun typeOf(name: String): String
    {
        when (this.currentScope)
        {
            "Subroutine" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.type       }
            }
            "Class" -> {
                this.ClassScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.type       }
            }
        }
        return "NONE"
    }

    fun indexOf(name: String): Int
    {
        when (this.currentScope)
        {
            "Subroutine" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.index       }
            }
            "Class" -> {
                this.ClassScope_SymbolTable.forEach {
                    if(it.name.equals(name))
                        return it.index      }
            }
        }
        return -1
    }

    /**
     fun segmentOfVarName(varName: String): Segment {
       var mySegment = Segment.TEMP
        when (this.currentScope)
        {
            "Subroutine" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if(it.kind.equals(kind))
                        count++       }
            }
            "Class" -> {
                this.SubroutineScope_SymbolTable.forEach {
                    if (it.kind.equals(kind))
                        count++       }
            }
        }
    }
    */

}