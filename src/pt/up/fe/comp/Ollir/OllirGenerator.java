package pt.up.fe.comp.Ollir;

import org.specs.comp.ollir.BinaryOpInstruction;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmNode;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;


import java.util.stream.Collectors;

public class OllirGenerator extends AJmmVisitor<Integer, Code> {

    private final StringBuilder ollirCode;
    public final SymbolTable symbolTable;
    private int cont;
    private int labelCountWhile;
    private int labelCountIf;

    public OllirGenerator(SymbolTable symbolTable) {
        cont = 0;
        labelCountWhile = 0;
        labelCountIf=0;
        this.ollirCode = new StringBuilder();
        this.symbolTable = symbolTable;
        addVisit("Program", this::programVisit);
        addVisit("ClassDeclaration", this::classDeclarationVisit);
        addVisit("MainDeclaration", this::mainDeclarationVisit);
        addVisit("OtherMethodDeclaration", this::otherMethodDeclarationVisit);
        addVisit("MethodBody", this::methodBodyVisit);
        addVisit("ReturnValue", this::returnVisit);
        addVisit("VarDeclaration", this::varDeclarationVisit);
        addVisit("Assignment", this::assignmentVisit);
        addVisit("Call", this::callVisit);
        addVisit("BinOp", this::binOpVisit);
        addVisit("Not", this::notVisit);
        addVisit("EEInt", this::intVisit);
        addVisit("EEFalse", this::falseVisit);
        addVisit("EEIdentifier", this::identifierVisit);
        addVisit("EETrue", this::trueVisit);
        addVisit("EEThis", this::thisVisit);
        addVisit("EENew", this::newVisit);
        addVisit("CompoundStatement", this::compoundStatementVisit);
        addVisit("WhileStatement", this::whileStatementVisit);
        addVisit("WhileCondition", this::whileConditionVisit);
        addVisit("WhileBody", this::whileBodyVisit);
        addVisit("IfStatement", this::ifStatementVisit);
        addVisit("IfCondition", this::ifConditionVisit);
        addVisit("IfBody", this::ifBodyVisit);
        addVisit("ElseBody", this::elseBodyVisit);
        addVisit("Array", this::arrayVisit);
        addVisit("ArrayLength", this::arrayLengthVisit);
        addVisit("NewArray",this::newArrayVisit);

    }


    public String getCode() {
        return ollirCode.toString();
    }

    private Code programVisit(JmmNode program, Integer dummy) {
        for (var importString : symbolTable.getImports()) {
            ollirCode.append("import ").append(importString).append(";\n");
        }
        for (var child : program.getChildren())
            visit(child);

        return null;
    }

    private Code classDeclarationVisit(JmmNode classDecl, Integer dummy) {
        ollirCode.append("public ").append(symbolTable.getClassName());

        var superClass = symbolTable.getSuper();

        if (superClass != null)
            ollirCode.append(" extends ").append(superClass);

        ollirCode.append("{\n");

        for (var child : classDecl.getChildren()) {
            if (child.getKind().equals("VarDeclaration")) {
                visit(child);
            }
        }

        ollirCode.append(".construct ").append(symbolTable.getClassName());
        ollirCode.append("().V{\n");
        ollirCode.append("invokespecial(this, \"<init>\").V;\n");
        ollirCode.append("}\n");

        for (var child : classDecl.getChildren()) {
            if (!child.getKind().equals("VarDeclaration")) {
                visit(child);
            }
        }

        ollirCode.append("}\n");
        return null;
    }

    private Code mainDeclarationVisit(JmmNode mainMethodDecl, Integer dummy) {
        var main = "main";

        ollirCode.append(".method public ");
        ollirCode.append("static ");
        ollirCode.append("main(");

        var params = symbolTable.getParameters(main);

        var paramCode = params.stream()
                .map(symbol -> OllirUtils.getCode(symbol))
                .collect(Collectors.joining(", "));

        ollirCode.append(paramCode);
        ollirCode.append(").");
        ollirCode.append(OllirUtils.getCode(symbolTable.getReturnType(main)));

        ollirCode.append("{\n");

        for (JmmNode child : mainMethodDecl.getChildren()) {
            if (child.getKind().equals("MethodBody")) {
                visit(child);
            }
        }
        ollirCode.append("ret.V;\n");
        ollirCode.append("}\n");

        return null;
    }

    private Code otherMethodDeclarationVisit(JmmNode otherMethodDecl, Integer dummy) {
        var methodName = otherMethodDecl.get("name");

        ollirCode.append(".method public ");
        ollirCode.append(methodName);
        ollirCode.append("(");

        var params = symbolTable.getParameters(methodName);

        var paramCode = params.stream()
                .map(symbol -> OllirUtils.getCode(symbol))
                .collect(Collectors.joining(", "));

        ollirCode.append(paramCode);
        ollirCode.append(").");
        ollirCode.append(OllirUtils.getCode(symbolTable.getReturnType(methodName)));

        ollirCode.append("{\n");

        for (JmmNode child : otherMethodDecl.getChildren()) {
            if (child.getKind().equals("MethodBody") || child.getKind().equals("ReturnValue")) {
                visit(child);
            }
        }


        ollirCode.append("}\n");


        return null;
    }

    private Code varDeclarationVisit(JmmNode varDecl, Integer dummy) {
        String name = varDecl.get("name");
        String type = varDecl.getJmmChild(0).get("name");
        if (varDecl.getJmmParent().getKind().equals("ClassDeclaration")) {
            ollirCode.append(".field ");
            ollirCode.append(name).append(".");

            if (varDecl.getJmmChild(0).get("isArray").equals("True")) {
                ollirCode.append("array.");
                ollirCode.append(OllirUtils.getOllirType(type));
            } else {
                ollirCode.append(OllirUtils.getOllirType(type));
            }
            ollirCode.append(";\n");
        }

        return null;
    }

    private Code methodBodyVisit(JmmNode methodBody, Integer dummy) {
        for (var child : methodBody.getChildren()) {
            Code vis = visit(child);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }
        return null;
    }

    private Code returnVisit(JmmNode returnValue, Integer integer) {
        JmmNode child = returnValue.getJmmChild(0);
        Type returnType = symbolTable.getReturnType(returnValue.getJmmParent().get("name"));

        if (child.getKind().equals("EEIdentifier")) {
            ollirCode.append("ret.");
            ollirCode.append(OllirUtils.getCode(returnType));
            ollirCode.append(" ");
            ollirCode.append(child.get("name"));
            ollirCode.append(".");
            ollirCode.append(OllirUtils.getCode(returnType));
            ollirCode.append(";\n");

        } else if (child.getKind().equals("EEInt")) {
            ollirCode.append("ret.");
            ollirCode.append(OllirUtils.getCode(returnType));
            ollirCode.append(" ");
            ollirCode.append(child.get("value"));
            ollirCode.append(".");
            ollirCode.append(OllirUtils.getCode(returnType));
            ollirCode.append(";\n");
        } else{
            Code vis = visit(child);
            if (vis != null) {
                ollirCode.append(vis.prefix);
                ollirCode.append("ret.");
                ollirCode.append(OllirUtils.getCode(returnType));
                ollirCode.append(" ");
                ollirCode.append(vis.code);
                ollirCode.append(";\n");
            }
        }

        return null;
    }

    private Code callVisit(JmmNode call, Integer dummy) {
        String prefixCode = "";
        Code thisCode = new Code();
        Code target = visit(call.getJmmChild(0));

        if(call.getJmmChild(1).getKind().equals("ArrayLength")){
            Code vis= visit(call.getJmmChild(1));
            thisCode.prefix=vis.prefix;
            thisCode.code=vis.code;
        }

        else {
            prefixCode += target.prefix;
            String methodName = call.getJmmChild(1).get("name");
            String finalcode;
            if (target.code == null) {
                finalcode = "invokestatic(" + call.getJmmChild(0).get("name") + "," + '"' + methodName + '"';
            } else {
                finalcode = "invokevirtual(" + target.code + "," + '"' + methodName + '"';
            }


            for (JmmNode arg : call.getJmmChild(1).getChildren()) {
                Code argCode = visit(arg);
                prefixCode += argCode.prefix;
                finalcode += "," + argCode.code;
            }
            Type type;
            String returnType;

            if (symbolTable.getMethods().contains(methodName)) {
                returnType = OllirUtils.getCode(symbolTable.getReturnType(methodName));
            } else {
                try {
                    type = new Type(call.get("typeValue"), Boolean.valueOf(call.get("isArray")));
                    returnType = OllirUtils.getCode(type);
                } catch (Exception e) {
                    type = new Type("void", false);
                    returnType = OllirUtils.getCode(type);
                }
            }

            finalcode += ")." + returnType;



            if (!call.getJmmParent().getKind().equals("MethodBody") && !call.getJmmParent().getKind().equals("CompoundStatement") && !call.getJmmParent().getKind().equals("ElseBody") &&!call.getJmmParent().getKind().equals("IfBody") && !call.getJmmParent().getKind().equals("WhileBody") ) {
                String temp = createTemp("." + returnType);
                finalcode = temp + " :=." + returnType + " " + finalcode;
                thisCode.code = temp;
                prefixCode += finalcode + ";\n";
                thisCode.prefix = prefixCode;
            } else {
                thisCode.code = finalcode;
                thisCode.prefix = prefixCode;
            }


        }
        return thisCode;
    }

    private Code assignmentVisit(JmmNode assignment, Integer dummy) {
        String methodSignature = OllirUtils.getParentMethod(assignment);
        String type = "";

        Code thisCode = new Code();
        Code lhs = visit(assignment.getJmmChild(0));
        Code rhs = visit(assignment.getJmmChild(1));

            thisCode.prefix = lhs.prefix;
            thisCode.prefix += rhs.prefix;

        if(assignment.getJmmChild(0).getKind().equals("Array")){
            for (Symbol symbol : symbolTable.getFields()) {
                if (symbol.getName().equals(assignment.getJmmChild(0).getJmmChild(0).get("name")))
                    type = OllirUtils.getCode(symbol.getType());
            }

            for (Symbol symbol : symbolTable.getLocalVariables(methodSignature)) {
                if (symbol.getName().equals(assignment.getJmmChild(0).getJmmChild(0).get("name")))
                    type = OllirUtils.getCode(symbol.getType());
            }
        }else {

            for (Symbol symbol : symbolTable.getFields()) {
                if (symbol.getName().equals(assignment.getJmmChild(0).get("name")))
                    type = OllirUtils.getCode(symbol.getType());
            }

            for (Symbol symbol : symbolTable.getLocalVariables(methodSignature)) {
                if (symbol.getName().equals(assignment.getJmmChild(0).get("name")))
                    type = OllirUtils.getCode(symbol.getType());
            }
        }
            if (!assignment.getJmmChild(1).getKind().equals("EENew"))
                thisCode.code = lhs.code + " :=." + type + " " + rhs.code;
            else if(assignment.getJmmChild(1).getJmmChild(0).getKind().equals("EEObject"))
                thisCode.code = lhs.code + " :=." + type + " " + rhs.code + ";\ninvokespecial(" + assignment.getJmmChild(0).get("name") + "." + type + ",\"<init>\").V";
            else
                thisCode.code = lhs.code + " :=.i32" + rhs.code;

        return thisCode;
    }

    private Code binOpVisit(JmmNode binOp, Integer integer) {
        Code lhs = visit(binOp.getJmmChild(0));
        Code rhs = visit(binOp.getJmmChild(1));

        String op = OllirUtils.getOllirOperator(binOp);
        String typeOp = OllirUtils.getTypeOperator(binOp);

        Code thisCode = new Code();
        thisCode.prefix = lhs.prefix;
        thisCode.prefix += rhs.prefix;

        if (!binOp.getJmmParent().getKind().equals("Assignment")) {
            String temp = createTemp(typeOp);
            thisCode.prefix += temp + ":=" + typeOp + lhs.code + " " + op + rhs.code + ";\n";
            thisCode.code = temp;
        } else {

            thisCode.code = lhs.code + " " + op + " " + rhs.code;
        }

        return thisCode;
    }

    private Code notVisit(JmmNode jmmNode, Integer integer) {
        Code vis =visit(jmmNode.getJmmChild(0));
        Code thisCode= new Code();

        thisCode.prefix=vis.prefix;
        if(!jmmNode.getJmmParent().getKind().equals("Assignment")){
            String temp=createTemp(".bool ");
            thisCode.prefix+=temp + ":=.bool !.bool "+vis.code+";\n";
            thisCode.code= temp;
        }else {
            thisCode.code=  "!.bool "+vis.code;
        }
        return thisCode;
    }

    private String createTemp(String typeOp) {
        cont++;
        return "temp" + cont + typeOp;
    }

    private Code identifierVisit(JmmNode jmmNode, Integer integer) {
        String identifierName = jmmNode.get("name");

        String methodSignature = OllirUtils.getParentMethod(jmmNode);
        Code code = new Code();
        code.prefix = "";

        for (Symbol symbol : symbolTable.getFields()) {
            if (symbol.getName().equals(identifierName))
                code.code = OllirUtils.getCode(symbol);
        }

        for (Symbol symbol : symbolTable.getLocalVariables(methodSignature)) {
            if (symbol.getName().equals(identifierName))
                code.code = OllirUtils.getCode(symbol);
        }
        return code;
    }

    private Code trueVisit(JmmNode jmmNode, Integer integer) {
        Code code = new Code();
        code.prefix = "";
        code.code = OllirUtils.getOllirType(jmmNode.getKind());
        return code;
    }

    private Code falseVisit(JmmNode jmmNode, Integer integer) {
        Code code = new Code();
        code.prefix = "";
        code.code = OllirUtils.getOllirType(jmmNode.getKind());
        return code;
    }

    private Code intVisit(JmmNode jmmNode, Integer integer) {
        Code code = new Code();
        if(jmmNode.getJmmParent().getKind().equals("Array")){
            String temp= createTemp(".i32");
            code.prefix = temp + " :=.i32 " + jmmNode.get("value") + ".i32;\n";
            code.code = temp;
        }else{
            code.prefix = "";
            code.code = jmmNode.get("value") + ".i32";
        }

        return code;
    }
    private Code thisVisit(JmmNode jmmNode, Integer integer) {
        Code code = new Code();
        code.prefix = "";
        code.code = "this";
        return code;
    }
    private Code newVisit(JmmNode jmmNode, Integer integer) {
        Code code = new Code();
        if(jmmNode.getJmmChild(0).getKind().equals("NewArray")){
                Code vis=visit(jmmNode.getJmmChild(0));
                if(vis!=null){
                    code.prefix=vis.prefix;
                    code.code=vis.code;
                }
        }

        else{
            String name=jmmNode.getJmmChild(0).get("name");
        if(!jmmNode.getJmmParent().getKind().equals("Assignment")){
            String temp=createTemp("."+name);
            code.code=temp;
            code.prefix=temp +" :=." + name+ " new("+name+")."+name+";\n";
            code.prefix+="invokespecial("+temp+",\"<init>\").V;\n";
        }
        else
        {
            code.prefix="";
            code.code=" new("+name+")."+name;
        }
        }

        return code;
    }
    private Code newArrayVisit(JmmNode jmmNode, Integer dummy) {
        Code code = new Code();
        Code vis = visit(jmmNode.getJmmChild(0));
        code.prefix = vis.prefix;
        code.code = " new(array, ";
        code.code += vis.code + ").array.i32";
        return code;
    }

    private Code whileStatementVisit(JmmNode jmmNode, Integer integer){
        int labelAux=++labelCountWhile;

        ollirCode.append("Loop" + labelCountWhile + ":");
        ollirCode.append("\n");

        for (JmmNode child : jmmNode.getChildren()) {
            Code vis = visit(child,labelAux);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }

        ollirCode.append("EndLoop" + labelAux + ":");
        ollirCode.append("\n");

        return null;
    }


    private Code whileConditionVisit(JmmNode jmmNode, Integer integer) {
        Code condition = visit(jmmNode.getJmmChild(0));

        ollirCode.append(condition.prefix).append("\n");
        ollirCode.append("if (" + condition.code + ") goto Body" + integer + ";\n");
        ollirCode.append("goto EndLoop" + integer + ";\n");

        return null;
    }

    private Code whileBodyVisit(JmmNode jmmNode, Integer integer) {
        ollirCode.append("Body" + integer + ":");
        ollirCode.append("\n");

        for (JmmNode child : jmmNode.getChildren()) {
            Code vis = visit(child);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }

        ollirCode.append("goto Loop" + integer + ";");
        ollirCode.append("\n");
        return null;
    }

    private Code compoundStatementVisit(JmmNode jmmNode, Integer integer) {
        Code vis;
        for(JmmNode child: jmmNode.getChildren()){
            vis = visit(child);
            if(vis!=null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }


        return null;
    }

    private Code ifStatementVisit(JmmNode jmmNode, Integer integer) {
       int labelAux= ++labelCountIf;

        for(JmmNode child : jmmNode.getChildren()){
            Code vis = visit(child,labelAux);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }

        ollirCode.append("Endif" + labelAux + ":\n");

        return null;
    }

    private Code ifConditionVisit(JmmNode jmmNode, Integer integer) {
        Code condition=visit(jmmNode.getJmmChild(0));

        ollirCode.append(condition.prefix).append("\n");
        ollirCode.append("if (" + condition.code+ ") goto Then" + integer + ";\n");
        ollirCode.append("goto Else" + integer +";\n");


        return null;
    }

    private Code ifBodyVisit(JmmNode jmmNode, Integer integer) {
        ollirCode.append("Then" + integer + ":\n");

        for(JmmNode child : jmmNode.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }

        ollirCode.append("goto Endif" + integer +";\n");


        return null;
    }

    private Code elseBodyVisit(JmmNode jmmNode, Integer integer) {
        ollirCode.append("Else" + integer + ":\n");

        for(JmmNode child : jmmNode.getChildren()){
            Code vis = visit(child);
            if (vis != null)
                ollirCode.append(vis.prefix).append(vis.code).append(";\n");
        }

        return null;
    }

    private Code arrayLengthVisit(JmmNode jmmNode, Integer integer) {
        Code code=new Code();
        Code vis=visit(jmmNode.getJmmParent().getJmmChild(0));
        if(!jmmNode.getJmmParent().getJmmParent().equals("Assignment")){
            String temp= createTemp(".i32");
            code.prefix=vis.prefix;
            code.prefix += temp + " :=.i32 " + "arraylength(";
            code.prefix+=vis.code+").i32;\n";
            code.code = temp;
        }else{
            code.prefix=vis.prefix;
            code.code = "arraylength(";
            code.code+=vis.code+").i32;\n";
        }

        return code;
    }

    private Code arrayVisit(JmmNode jmmNode, Integer integer) {
        JmmNode identifier=jmmNode.getJmmChild(0);
        JmmNode index=jmmNode.getJmmChild(1);
        Code code=new Code();
        Code vis=visit(index);

        if(!jmmNode.getJmmParent().getKind().equals("Assignment")){
            String temp= createTemp(".i32");
            code.prefix=vis.prefix;
            code.prefix += temp + " :=.i32 " + identifier.get("name")+"[";
            code.prefix +=vis.code+"].i32;\n";
            code.code = temp;
      }else{
            code.prefix=vis.prefix;
            code.code = identifier.get("name")+"[";
            code.code+=vis.code+"].i32";
      }

        return code;
    }

}


