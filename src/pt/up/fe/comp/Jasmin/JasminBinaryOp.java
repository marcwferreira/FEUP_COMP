package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.BinaryOpInstruction;
import org.specs.comp.ollir.Element;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.OperationType;

public class JasminBinaryOp {
    public static String addBinaryOper(BinaryOpInstruction instruction, Method method) {
        Element left=instruction.getLeftOperand();
        Element right= instruction.getRightOperand();
        var varTable= method.getVarTable();
        StringBuilder jasminCode= new StringBuilder();
        OperationType opType=instruction.getOperation().getOpType();
        jasminCode.append(JasminLoadStore.loadElement(left,varTable));
        String loadRight = JasminLoadStore.loadElement(right,varTable);

        if (opType == OperationType.LTH) {
            jasminCode.append(loadRight);
            jasminCode.append(JasminUtils.getJasminOperationType(opType));
            JasminBuilder.conditionals++;
            jasminCode.append(" True");
            jasminCode.append(JasminBuilder.conditionals);
            jasminCode.append("\niconst_0\ngoto FinalCond");
            jasminCode.append(JasminBuilder.conditionals);
            jasminCode.append("\nTrue");
            jasminCode.append(JasminBuilder.conditionals);
            jasminCode.append(":\niconst_1\nFinalCond");
            jasminCode.append(JasminBuilder.conditionals).append(":");
        }
        else if (opType == OperationType.ANDB) {
           JasminBuilder.conditionals++;
           jasminCode.append(JasminUtils.getJasminOperationType(opType));
           jasminCode.append(" False");
           jasminCode.append(JasminBuilder.conditionals);
           jasminCode.append("\n");
           jasminCode.append(loadRight);
           jasminCode.append(JasminUtils.getJasminOperationType(opType));
           jasminCode.append(" False");
           jasminCode.append(JasminBuilder.conditionals);
           jasminCode.append("\niconst_1\ngoto FinalCond");
           jasminCode.append(JasminBuilder.conditionals);
           jasminCode.append("\nFalse");
           jasminCode.append(JasminBuilder.conditionals);
           jasminCode.append(":\niconst_0\nFinalCond");
           jasminCode.append(JasminBuilder.conditionals).append(":");
        } else  {
            jasminCode.append(loadRight);
            jasminCode.append(JasminUtils.getJasminOperationType(opType));
        }
        JasminUtils.limitStack(-1);
        jasminCode.append("\n");

        return jasminCode.toString();
    }
}
