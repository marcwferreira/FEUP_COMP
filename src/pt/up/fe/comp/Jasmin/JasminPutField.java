package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.Operand;
import org.specs.comp.ollir.PutFieldInstruction;

public class JasminPutField {
    public static String addPutField(PutFieldInstruction instruction, Method method) {
        StringBuilder jasminCode= new StringBuilder();
        Element first =instruction.getFirstOperand();
        Element second = instruction.getSecondOperand();
        Element third= instruction.getThirdOperand();
        var table = method.getVarTable();
        String name;
        String className=method.getOllirClass().getClassName();
        String type= JasminUtils.getJasminType(second.getType());
        if(((Operand) first).getName().equals("this"))
            name=className;
        else
            name=((Operand) first).getName();
        jasminCode.append(JasminLoadStore.loadElement(first,table));
        jasminCode.append(JasminLoadStore.loadElement(third,table)).append("putfield ");
        jasminCode.append(name).append("/").append(((Operand) second).getName()).append(" ").append(type);
        jasminCode.append("\n");

        JasminUtils.limitStack(-2);

        return jasminCode.toString();
    }
}
