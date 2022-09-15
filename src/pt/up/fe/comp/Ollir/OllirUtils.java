package pt.up.fe.comp.Ollir;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class OllirUtils {

    public static String getCode(Symbol symbol){
        return symbol.getName()+"."+getCode(symbol.getType());
    }
    public static String getCode(Type type){
        StringBuilder ollircode=new StringBuilder();

        if(type.isArray())
            ollircode.append("array.");

        ollircode.append(getOllirType(type.getName()));
        return ollircode.toString();
    }

    public static String getOllirType(String jmmType){
        switch (jmmType){
            case "void":
                return "V";
            case "int":
            case "EEInt":
                return"i32";
            case "EETrue":
                return"1.bool";
            case "EEFalse":
                return"0.bool";
            case "boolean":
                return"bool";
            default:
                return jmmType;
        }
    }
    public static String getOllirOperator(JmmNode node){
        switch (node.get("op")){
            case "add":
                return "+.i32 ";
            case "less":
                return "<.bool ";
            case "sub":
                return "-.i32 ";
            case "mult":
                return "*.i32 ";
            case "div":
                return "/.i32 ";
            case "and":
                return "&&.bool ";
        }
        throw new NotImplementedException(node.get("op"));
    }

    public static String getTypeOperator(JmmNode node){
        switch (node.get("op")){
            case "add":
            case "sub":
            case "mult":
            case "div":
                return ".i32 ";
            case "less":
            case "and":
                return ".bool ";
        }
        throw new NotImplementedException(node.getKind());
    }

    public static String getParentMethod(JmmNode node) {

        while(!node.getKind().equals("OtherMethodDeclaration") && !node.getKind().equals("MainDeclaration"))
            node = node.getJmmParent();

        if(node.getKind().equals("OtherMethodDeclaration"))
            return node.get("name");

        return "main";
    }
    public static String buildCondition(){
        //TODO::
        StringBuilder condition = new StringBuilder();
        return condition.toString();
    }

}
