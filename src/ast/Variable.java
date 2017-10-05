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
    public Variable (String name, Type type, String qualifier, ParamList paramList) {
        this.name = name;
        this.type = type;
        this.qualifier = qualifier;
        this.paramList = paramList;
    }
    
    public ParamList getParam() {
        return this.paramList;
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
    private ParamList paramList;
}