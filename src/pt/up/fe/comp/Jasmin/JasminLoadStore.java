package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

import java.util.HashMap;

public class JasminLoadStore {
    public static String loadElement(Element operand, HashMap<String, Descriptor> varTable) {
        if(operand.isLiteral())
            return  loadLiteral((LiteralElement) operand);
        int reg =varTable.get(((Operand) operand).getName()).getVirtualReg();
        StringBuilder jasminCode= new StringBuilder();
        ElementType elementType = operand.getType().getTypeOfElement();
        if(elementType==ElementType.INT32 || elementType==ElementType.BOOLEAN){
            if(varTable.get(((Operand) operand).getName()).getVarType().getTypeOfElement()==ElementType.ARRAYREF){
               return loadArray(operand,varTable);
            }else
                jasminCode.append("iload ").append(reg).append("\n");
        }
        else{
            jasminCode.append("aload ").append(reg).append("\n");
        }
        JasminUtils.limitStack(1);
        return jasminCode.toString();
    }

    private static String loadArray(Element operand, HashMap<String, Descriptor> varTable) {
        StringBuilder jasminCode= new StringBuilder();
        Element index=((ArrayOperand)operand).getIndexOperands().get(0);
        int reg= varTable.get(((Operand) operand).getName()).getVirtualReg();
        int indexReg=varTable.get(((Operand) index).getName()).getVirtualReg();
        jasminCode.append("aload ").append(reg).append("\n");
        jasminCode.append("iload ").append(indexReg).append("\n");
        jasminCode.append("iaload\n");

        JasminUtils.limitStack(2);
        JasminUtils.limitStack(-1);

        return jasminCode.toString();
    }

    private static String loadLiteral(LiteralElement operand) {
        StringBuilder jasminCode= new StringBuilder();
        String literal= operand.getLiteral();

        jasminCode.append("ldc ").append(literal).append("\n");
        JasminUtils.limitStack(1);

        return jasminCode.toString();
    }

    public static String storeElement(Element operand, HashMap<String, Descriptor> varTable) {
        int reg =varTable.get(((Operand) operand).getName()).getVirtualReg();
        StringBuilder jasminCode= new StringBuilder();
        ElementType elementType = operand.getType().getTypeOfElement();
        if(elementType==ElementType.INT32 || elementType==ElementType.BOOLEAN){
            if(varTable.get(((Operand) operand).getName()).getVarType().getTypeOfElement()==ElementType.ARRAYREF){
                    storeArray(operand,varTable);
            }else
                jasminCode.append("istore ").append(reg).append("\n");
        }
        else{
            jasminCode.append("astore ").append(reg).append("\n");
        }

        JasminUtils.limitStack(-1);

        return jasminCode.toString();
    }

    private static String storeArray(Element operand, HashMap<String, Descriptor> varTable) {
        StringBuilder jasminCode= new StringBuilder();
        Element index=((ArrayOperand)operand).getIndexOperands().get(0);
        int reg= varTable.get(((Operand) operand).getName()).getVirtualReg();
        int indexReg=varTable.get(((Operand) index).getName()).getVirtualReg();
        jasminCode.append("aload ").append(reg).append("\n");
        jasminCode.append("iload ").append(indexReg).append("\n");
        jasminCode.append("iastore\n");

        JasminUtils.limitStack(-3);

        return jasminCode.toString();
    }
}
