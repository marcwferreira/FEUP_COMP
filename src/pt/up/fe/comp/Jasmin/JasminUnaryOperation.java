package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.UnaryOpInstruction;

public class JasminUnaryOperation {

    public static String addInstructionCode(UnaryOpInstruction instruction, Method method){
        StringBuilder jasminCode=new StringBuilder();

        jasminCode.append(JasminLoadStore.loadElement(instruction.getOperand(),method.getVarTable()));
        jasminCode.append(JasminUtils.getJasminOperationType(instruction.getOperation().getOpType()));

        JasminBuilder.conditionals++;

        jasminCode.append(" True").append(JasminBuilder.conditionals);
        jasminCode.append("\niconst_0\ngoto FinalCond").append(JasminBuilder.conditionals);
        jasminCode.append("\nTrue").append(JasminBuilder.conditionals);
        jasminCode.append(":\niconst_1\nFinalCond").append(JasminBuilder.conditionals).append(":");

        jasminCode.append("\n");

        return jasminCode.toString();
    }
}
