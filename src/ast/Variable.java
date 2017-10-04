package ast;

public class Variable {

    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
    }
    
    public Variable( String name, Type type, String qualifier ) {
        this.name = name;
        this.type = type;
        this.qualifier = qualifier;
    }

    public String getName() { return name; }

    public Type getType() {
        return type;
    }
    
    public String getQualifier(){
        return this.qualifier;
    }
    
    private String name;
    private Type type;
    private String qualifier;
}