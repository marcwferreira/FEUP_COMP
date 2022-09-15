package pt.up.fe.comp.Visitor;

import pt.up.fe.comp.SymbolTable.Analysis;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.SymbolTable.JmmMethod;
import pt.up.fe.specs.util.exceptions.NotImplementedException;


import java.util.ArrayList;
import java.util.List;

public class SemanticAnalysisUtils {

    public static boolean evaluatesToBoolean(JmmMethod method, JmmNode node, Analysis analysis) {

        Type type = new Type("boolean", false);
        switch (node.getKind()) {
            case "EETrue":
            case "EEFalse":
                return true;
            case "EEIdentifier":
                String typeVar = node.get("name");
                List<Symbol> checkExist = analysis.getSymbolTable().getLocalVariables(method.getName());
                for(Symbol symbol: checkExist){
                    if(symbol.getName().equals(typeVar)) {
                        if (symbol.getType().getName().equals("boolean") && !symbol.getType().isArray())
                            return true;
                    }
                    for(Symbol symbol2 :analysis.getSymbolTable().getFields()){
                        if(symbol2.getName().equals(typeVar))
                            if (symbol2.getType().getName().equals("boolean") && !symbol2.getType().isArray())
                                return true;
                    }
                }
                break;
            case "Call":
                if (type.equals(evaluateCall(method, node, analysis))) return true;
                break;
            case "ExpressionMethodCall":
            case "IfExpression":
                if (type.equals(evaluateExpression(method, node, analysis))) return true;
                break;
            case "Not":
                if (evaluateNotOperation(method, node, analysis)) return true;
                break;
            case "BinOp":
                String op = node.get("op");
                if(op.equals("and")) {
                    if (evaluateOperationWithBooleans(method, node, analysis)) return true;
                }
                else if(op.equals("less"))
                    if (evaluateOperationWithIntegers(method, node, analysis)) return true;
                break;
            default:
                break;
        }

        analysis.newReport(node,"Expression should return a boolean.");
        return false;
    }

    public static boolean evaluatesToInteger(JmmMethod method, JmmNode node, Analysis analysis) {

        Type type = new Type("int", false);
        switch (node.getKind()) {
            case "EEInt":
                return true;
            case "EEIdentifier":
                String typeVar = node.get("name");
                List<Symbol> checkExist = analysis.getSymbolTable().getLocalVariables(method.getName());
                for(Symbol symbol: checkExist){
                    if(symbol.getName().equals(typeVar))
                        if (symbol.getType().getName().equals("int") && !symbol.getType().isArray())
                            return true;
                }
                for(Symbol symbol :analysis.getSymbolTable().getFields()){
                    if(symbol.getName().equals(typeVar))
                        if (symbol.getType().getName().equals("int") && !symbol.getType().isArray())
                            return true;
                }
                break;
            case "Call":
                if (type.equals(evaluateCall(method, node, analysis))) return true;
                break;
            case "ExpressionMethodCall":
                if (type.equals(evaluateExpression(method, node, analysis))) return true;
                break;
            case "Array":
                if (evaluateArrayAccess(method, node, analysis)) return true;
                break;
            case "BinOp":
                String op = node.get("op");
                if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult"))
                    if (evaluateOperationWithIntegers(method, node, analysis)) return true;
                break;
            default:
                break;
        }

        analysis.newReport(node,"Expression should return an int.");
        return false;
    }

    private static boolean evaluateArrayAccess(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        if (!children.get(0).getKind().equals("EEIdentifier")) {


            analysis.newReport(children.get(0), "\""+children.get(0)+" is not an array");
            return false;
        } else {
            if (!isIdentifier(method, children.get(0), analysis, true, true)) {
                analysis.newReport(children.get(0),"\""+children.get(0)+" is not an array");
                return false;
            }
        }
        if (!evaluatesToInteger(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"Bad array access: expected int.");
            return false;
        }
        return true;
    }

    public static boolean evaluateOperationWithBooleans(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        if (children.size() != 2) return false;

        if(node.getJmmChild(0).getKind().equals("Call")){
            node.getJmmChild(0).put("typeValue","boolean");
            node.getJmmChild(0).put("isArray","false");
        }
        if(node.getJmmChild(1).getKind().equals("Call")){
            node.getJmmChild(1).put("typeValue","boolean");
            node.getJmmChild(1).put("isArray","false");
        }

        boolean hasReport = false;

        if (!evaluatesToBoolean(method, node.getJmmChild(0), analysis)) {
            analysis.newReport(node.getJmmChild(0),"Left operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }

        if (!evaluatesToBoolean(method, node.getJmmChild(1), analysis)) {
            analysis.newReport(node.getJmmChild(1),"Right operand for binary operator '&&' is not a boolean");
            hasReport = true;
        }
        return !hasReport;
    }

    public static boolean evaluateOperationWithIntegers(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        if (children.size() != 2)
            return false;


        if(node.getJmmChild(0).getKind().equals("Call")){
            node.getJmmChild(0).put("typeValue","int");
            node.getJmmChild(0).put("isArray","false");
        }
        if(node.getJmmChild(1).getKind().equals("Call")){
            node.getJmmChild(1).put("typeValue","int");
            node.getJmmChild(1).put("isArray","false");
        }

        char operation = ' ';

        switch (node.get("op")) {
            case "add":
                operation = '+';
                break;
            case "sub":
                operation = '-';
                break;
            case "mult":
                operation = '*';
                break;
            case "div":
                operation = '/';
                break;
            case "less":
                operation = '<';
                break;
        }

        boolean hasReport = false;
        if (!evaluatesToInteger(method, children.get(0), analysis)) {
            analysis.newReport(children.get(0),"Left operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }
        if (!evaluatesToInteger(method, children.get(1), analysis)) {
            analysis.newReport(children.get(1),"Right operand type for binary operator '" + operation + "' is not an integer");
            hasReport = true;
        }

        return !hasReport;
    }

    public static boolean evaluateNotOperation(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();

        if(node.getJmmChild(0).getKind().equals("Call")){
            node.getJmmChild(0).put("typeValue","boolean");
            node.getJmmChild(0).put("isArray","false");
        }

        if (!evaluatesToBoolean( method, children.get(0), analysis)) {
            analysis.newReport(children.get(0), "bad operand type for binary operator '!': boolean expected");
            return false;
        }
        return true;
    }

    public static Type checkIfIdentifierExists(JmmMethod method, JmmNode node, Analysis analysis) {
        String typeVar=node.get("name");
        for(Symbol symbol :analysis.getSymbolTable().getLocalVariables(method.getName())){
            if(symbol.getName().equals(typeVar))
                return symbol.getType();
        }
        for(Symbol symbol :analysis.getSymbolTable().getFields()){
            if(symbol.getName().equals(typeVar))
                return symbol.getType();
        }
        return null;
    }

    public static boolean isIdentifier(JmmMethod method, JmmNode node, Analysis analysis, boolean isInt, boolean isArray) {
        Type typeIdentifier = checkIfIdentifierExists(method, node, analysis);
        if (typeIdentifier == null)
            return false;
        if(typeIdentifier.isArray() != isArray) return false;
        return true;
    }

    public static Type evaluateExpression(JmmMethod method, JmmNode node, Analysis analysis) {
        List<JmmNode> children = node.getChildren();
        if(node.getKind().equals("BinOp")){

            String op = node.get("op");
            if(op.equals("and")) {
                if (evaluateOperationWithBooleans(method, node, analysis)) {
                    return new Type("boolean", false);
                }
            }
            else if(op.equals("less")) {
                if (evaluateOperationWithIntegers(method, node, analysis))
                    return new Type("boolean", false);
            }
            else if(op.equals("add") || op.equals("sub") || op.equals("div") || op.equals("mult")) {
                if (evaluateOperationWithIntegers(method, node, analysis))
                    return new Type("int", false);
            }
        }
        else if(node.getKind().equals("Array")){
            if (evaluateArrayAccess(method, node, analysis))
                return new Type("int", false);
        }
        else if(node.getKind().equals("Call")){

            return evaluateCall(method, node, analysis);
        }
        else if(node.getKind().equals("EENew")){
            if(node.getJmmChild(0).getKind().equals("Array")){
                return new Type("int",false);
            }
            else if(node.getJmmChild(0).getKind().equals("EEObject")){ //&& SemanticAnalysisUtils.EEIdentifierExists(method, children.get(0),analysis)){

                    return new Type(node.getJmmChild(0).get("name"), false);
                
            }
        }

        return null;
    }

    public static Type evaluateCall(JmmMethod method, JmmNode node, Analysis analysis) {
       JmmNode leftChild=node.getJmmChild(0);
       JmmNode rightChild=node.getJmmChild(1);
       methodCall(method,node,analysis);
       if(rightChild.getKind().equals("ArrayLength")){
           return new Type("int",false);
       }else{
           Type childType= getNodeType(method,leftChild,analysis);
           if(childType==null)
               return null;
           if(analysis.getSymbolTable().getMethods().contains(rightChild.get("name"))){
               return analysis.getSymbolTable().getReturnType(rightChild.get("name"));
           } else{
               return null;
           }
       }
    }

    public static void methodCall(JmmMethod method,JmmNode node,Analysis analysis){
        JmmNode rightChild = node.getJmmChild(1);

        Type nodeType= SemanticAnalysisUtils.getNodeType(method,node.getJmmChild(0),analysis);

        if(nodeType==null)
            return;
        if(rightChild.getKind().equals("ArrayLength")){
            Type arrayType=new Type("int",true);
            if(SemanticAnalysisUtils.sameType(arrayType,nodeType)){
                return;
            }else{
                analysis.newReport(node,"First child is not an array!");
            }

        }else {
            String methodName=rightChild.get("name");
            if (nodeType.getName().equals(analysis.getSymbolTable().getClassName())) {
                if (analysis.getSymbolTable().getMethodById(methodName) != null) {
                    List<Symbol> params = analysis.getSymbolTable().getParameters(methodName);
                    if (params.size() != rightChild.getNumChildren()) {
                        analysis.newReport(node, "Wrong number of parameters on Method!");
                        return;
                    }

                    int index = 0;
                    for (var param : params) {
                        Type typeParam = SemanticAnalysisUtils.getNodeType(method, rightChild.getJmmChild(index), analysis);
                        if(typeParam==null)
                            continue;
                        if (SemanticAnalysisUtils.sameType(typeParam, param.getType()) == false) {
                            analysis.newReport(node, "Wrong type of parameter!");
                            return;
                        }
                        index++;
                    }

                } else if (analysis.getSymbolTable().getSuper() != null) {
                    return;
                } else {
                    analysis.newReport(node, "The method does not exist!");
                }
            } else if (analysis.getSymbolTable().getImports().contains(nodeType.getName()) || analysis.getSymbolTable().getImports().contains(node.getJmmChild(0).get("name"))) {
                return;
            } else {
                analysis.newReport(node, "The class does not exist!");
                return;
            }
        }
    }

    private static Type evaluateNew(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode child=node.getJmmChild(0);
        if(child.getKind().equals("NewArray")){
            if(evaluatesToInteger(method,child.getJmmChild(0),analysis))
                return new Type("int",true);
            else{
                analysis.newReport(child.getJmmChild(0),"Array length is not integer!");
            }

        }
        else if(child.getKind().equals("EEObject")){
            return new Type(child.get("name"),false);
        }
       return null;
    }
    private static Type evaluateEEIdentifier(JmmMethod method, JmmNode node, Analysis analysis) {
        for(Symbol symbol : method.getLocalVariables())
        {
            if(symbol.getName().equals(node.get("name")))
                return symbol.getType();
        }
        if(!method.getName().equals("main")){
            for(Symbol symbol : analysis.getSymbolTable().getFields())
            {
                if(symbol.getName().equals(node.get("name"))){
                    return symbol.getType();
                }

            }
        }
        if(analysis.getSymbolTable().getImports().contains(node.get("name"))){
          return null;
        }
        analysis.newReport(node,"Variable not declared!");
        return null;
    }

    public static Type getNodeType(JmmMethod method, JmmNode node, Analysis analysis){
        String kind=node.getKind();
        switch (kind){
            case "BinOp":
               return evaluateExpression(method,node,analysis);
            case "Array":
                return getArrayType(method,node,analysis);
            case "EEInt":
                return new Type("int",false);
            case "Call":
                return evaluateCall(method,node,analysis);
            case "EENew":
                return evaluateNew(method,node,analysis);
            case "EETrue":
            case "EEFalse":
                return new Type("boolean",false);
            case "EEThis":
                if(method.getName().equals("main"))
                    analysis.newReport(node,"Can not use this on main method!");
                return new Type(analysis.getSymbolTable().getClassName(),false);
            case "EEIdentifier":
                return evaluateEEIdentifier(method,node,analysis);
            case "Not":
               return new Type("boolean",false);
        }
        throw new NotImplementedException(kind);
    }


    private static Type getArrayType(JmmMethod method, JmmNode node, Analysis analysis) {
        JmmNode leftChild=node.getJmmChild(0);
        JmmNode rightChild=node.getJmmChild(1);
        Type leftType=getNodeType(method,leftChild,analysis);

        if(leftType==null)
            return null;
        if(!(leftType.isArray() && leftType.getName().equals("int"))){
            analysis.newReport(node,"This is not an array!");
        }
        if(!evaluatesToInteger(method,rightChild,analysis))
            analysis.newReport(node,"The array index is not integer!");

        return new Type("int",false);
    }


    public static boolean sameType(Type param, Type param2){
        boolean sameType=(param.isArray()==param2.isArray() && param.getName().equals(param2.getName()));

        return sameType;
    }


}