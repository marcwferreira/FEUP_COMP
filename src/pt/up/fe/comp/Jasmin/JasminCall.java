package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminCall {
    public static String addCall(CallInstruction instruction, Method method) {
        switch (instruction.getInvocationType()){
            case NEW:
                return getNew(instruction,method);
            case invokespecial:
                return getInvokeSpecial(instruction,method);
            case invokevirtual:
                return getInvokeVirtual(instruction,method);
            case invokestatic:
                return getInvokeStatic(instruction,method);
            case ldc:
                return getLdc(instruction,method);
            case arraylength:
                return getArrayLength(instruction,method);

        }
        throw new NotImplementedException(instruction.getInvocationType());
    }

    private static String getArrayLength(CallInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();
        jasminCode.append(JasminLoadStore.loadElement(instruction.getFirstArg(),varTable)).append("arraylength\n");
        return jasminCode.toString();
    }

    private static String getLdc(CallInstruction instruction, Method method) {
        var varTable= method.getVarTable();
        return JasminLoadStore.loadElement(instruction.getFirstArg(),varTable);
    }

    private static String getInvokeStatic(CallInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();
        String className=method.getOllirClass().getClassName();

        for(Element element : instruction.getListOfOperands())
            jasminCode.append(JasminLoadStore.loadElement(element,varTable));

        jasminCode.append("invokestatic ");

        if(((Operand) instruction.getFirstArg()).getName().equals("this"))
            jasminCode.append(className);
        else
            jasminCode.append(((Operand) instruction.getFirstArg()).getName());

        jasminCode.append("/");
        jasminCode.append(((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\"", ""));
        jasminCode.append("(");

        for(Element element : instruction.getListOfOperands()){
            jasminCode.append(JasminUtils.getJasminType(element.getType()));
            JasminUtils.limitStack(-1);
        }

        if(instruction.getReturnType().getTypeOfElement()!=ElementType.VOID){
            JasminUtils.limitStack(1);
        }

        jasminCode.append(")").append(JasminUtils.getJasminType(instruction.getReturnType()));
        jasminCode.append("\n");


        return jasminCode.toString();
    }

    private static String getInvokeVirtual(CallInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();
        String className=method.getOllirClass().getClassName();

        jasminCode.append(JasminLoadStore.loadElement(instruction.getFirstArg(), varTable));

        for(Element element : instruction.getListOfOperands())
            jasminCode.append(JasminLoadStore.loadElement(element,varTable));

        jasminCode.append("invokevirtual ");

        if(((ClassType) instruction.getFirstArg().getType()).getName().equals("this"))
            jasminCode.append(className);
        else
            jasminCode.append(((ClassType) instruction.getFirstArg().getType()).getName());

      jasminCode.append("/");
      jasminCode.append(((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\"", ""));
      jasminCode.append("(");

      for(Element element : instruction.getListOfOperands()){
          jasminCode.append(JasminUtils.getJasminType(element.getType()));
          JasminUtils.limitStack(-1);
      }


     jasminCode.append(")").append(JasminUtils.getJasminType(instruction.getReturnType()));
     jasminCode.append("\n");

     if(instruction.getReturnType().getTypeOfElement()==ElementType.VOID){
         JasminUtils.limitStack(-1);
     }

      return jasminCode.toString();
    }

    private static String getInvokeSpecial(CallInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();
        jasminCode.append(JasminLoadStore.loadElement(instruction.getFirstArg(),varTable));
        jasminCode.append("invokespecial ");
        if(instruction.getFirstArg().getType().getTypeOfElement()==ElementType.THIS){
            if(method.getOllirClass().getSuperClass()==null)
                jasminCode.append("java/lang/Object");
            else
                jasminCode.append(method.getOllirClass().getSuperClass());
        }


        else
            jasminCode.append(JasminUtils.getJasminEspecialType(instruction.getFirstArg().getType()));
        jasminCode.append(".<init>(");
        for(Element element : instruction.getListOfOperands()){
            jasminCode.append(JasminLoadStore.loadElement(element,varTable));
            JasminUtils.limitStack(-1);
        }

        jasminCode.append(")").append(JasminUtils.getJasminType(instruction.getReturnType()));
        jasminCode.append("\n");

        JasminUtils.limitStack(-1);

        return jasminCode.toString();
    }

    private static String getNew(CallInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();
        for(Element element : instruction.getListOfOperands())
            jasminCode.append(JasminLoadStore.loadElement(element,varTable));
        if(instruction.getReturnType().getTypeOfElement()== ElementType.OBJECTREF){
            jasminCode.append("new ");
            jasminCode.append(((Operand) instruction.getFirstArg()).getName()).append("\n");
            JasminUtils.limitStack(1);
        }
        else if(instruction.getReturnType().getTypeOfElement()== ElementType.ARRAYREF){
            jasminCode.append("newarray int\n");
        }else{
            throw new NotImplementedException(instruction.getReturnType().getTypeOfElement());
        }

        return jasminCode.toString();
    }
}
