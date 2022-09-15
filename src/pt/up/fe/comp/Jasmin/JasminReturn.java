package pt.up.fe.comp.Jasmin;


import org.specs.comp.ollir.ElementType;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.ReturnInstruction;

public class JasminReturn {
    public static String returnInstructions(ReturnInstruction instruction, Method method) {
        if(!instruction.hasReturnValue())
            return "return\n";
        StringBuilder jasminCode = new StringBuilder();
        ElementType returnType = instruction.getOperand().getType().getTypeOfElement();
        jasminCode.append(JasminLoadStore.loadElement(instruction.getOperand(),method.getVarTable()));
        if(returnType==ElementType.INT32 || returnType==ElementType.BOOLEAN)
            jasminCode.append("ireturn\n");
        else
            jasminCode.append("areturn\n");

        JasminUtils.limitStack(-1);

        return jasminCode.toString();
    }
}
