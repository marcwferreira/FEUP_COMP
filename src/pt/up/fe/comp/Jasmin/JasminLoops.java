package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminLoops {

    public static String gotoInstruction(GotoInstruction instruction, Method method){
        return "goto " + instruction.getLabel() + "\n";
    }

    public static String branchInstruction(CondBranchInstruction instruction, Method method){

        StringBuilder jasminCode= new StringBuilder();
        var varTable= method.getVarTable();

        Element firstElement = instruction.getOperands().get(0);
        String label = instruction.getLabel();
        String firstInstruction = JasminLoadStore.loadElement(firstElement,method.getVarTable());

        jasminCode.append(firstInstruction);
        jasminCode.append("ifne ").append(label);
        jasminCode.append("\n");

        JasminUtils.limitStack(-1);

        return jasminCode.toString();
    }
}
