package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

public class JasminMethodAssignment extends JasminBuilder{

    public JasminMethodAssignment (ClassUnit classUnit) {
        super(classUnit);
    }
    public static String getInstructionsAssign(AssignInstruction assignInstruction, Method method){
        Instruction rhs =assignInstruction.getRhs();
        Element lhs = assignInstruction.getDest();
        var table = method.getVarTable();

        StringBuilder jasminCode = new StringBuilder();

        if(lhs instanceof ArrayOperand){

            Element index=((ArrayOperand)lhs).getIndexOperands().get(0);
            int reg= table.get(((Operand) lhs).getName()).getVirtualReg();
            int indexReg=table.get(((Operand) index).getName()).getVirtualReg();

            jasminCode.append("aload ").append(reg).append("\n");
            jasminCode.append("iload ").append(indexReg).append("\n");
            jasminCode.append(JasminUtils.addInstructions(rhs,method));
            jasminCode.append("iastore\n");
            JasminUtils.limitStack(3);
            JasminUtils.limitStack(-3);

        }else{
            jasminCode.append(JasminUtils.addInstructions(rhs,method));
            jasminCode.append(JasminLoadStore.storeElement(lhs,table));
        }



    return jasminCode.toString();
    }

}
