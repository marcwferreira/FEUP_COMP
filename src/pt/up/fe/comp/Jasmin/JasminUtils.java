package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class JasminUtils {
    static ClassUnit classUnit;

    public static void limitStack(int s){
        JasminBuilder.currentStack+=s;
        if(JasminBuilder.currentStack>JasminBuilder.maxStack)
            JasminBuilder.maxStack=JasminBuilder.currentStack;
    }

    public static String getAccessModifiers(AccessModifiers accessModifier) {
        switch (accessModifier) {
            case PUBLIC:
                return "public";
            case PRIVATE:
                return "private";
            case DEFAULT:
                return "private";
            case PROTECTED:
                return "protected";
        }
        throw new NotImplementedException(accessModifier);
    }

    public static String getJasminType(Type type) {

        switch (type.getTypeOfElement()) {
            case ARRAYREF:
                return getJasminArrayType((ArrayType) type);
            case INT32:
                return "I";
            case OBJECTREF:
                return getJasminObjectType((ClassType) type);
            case BOOLEAN:
                return "Z";
            case VOID:
                return "V";
            case STRING:
                return "Ljava/lang/String;";

        }
       throw new NotImplementedException(type.getTypeOfElement());
    }

    private static String getJasminObjectType(ClassType type) {
        StringBuilder jasminCode = new StringBuilder();
        String className = type.getName();
        for (var imported : JasminBuilder.classUnit.getImports()) {

            if (imported.endsWith("." + className))
                jasminCode.append("L").append(imported.replace('.', '/')).append(";");
        }
        jasminCode.append("L").append(className).append(";");
        return jasminCode.toString();
    }

    private static String getJasminArrayType(ArrayType type) {
        StringBuilder jasminCode = new StringBuilder();
        if(type.getArrayType()==ElementType.INT32)

            jasminCode.append("[I");
        else
            jasminCode.append("[Ljava/lang/String;");
        return jasminCode.toString();
    }

    public static String addInstructions(Instruction instruction, Method method){
        switch (instruction.getInstType()) {
            case ASSIGN:
                return JasminMethodAssignment.getInstructionsAssign((AssignInstruction) instruction, method);
            case PUTFIELD:
                return JasminPutField.addPutField((PutFieldInstruction) instruction, method);
            case RETURN:
                return JasminReturn.returnInstructions((ReturnInstruction) instruction, method);
            case BINARYOPER:
                return JasminBinaryOp.addBinaryOper((BinaryOpInstruction) instruction, method);
            case NOPER:
                return addNoOper((SingleOpInstruction) instruction, method);
            case GETFIELD:
                return JasminGetField.addGetField((GetFieldInstruction) instruction, method);
            case CALL:
                return JasminCall.addCall((CallInstruction) instruction, method);
            case UNARYOPER:
                return JasminUnaryOperation.addInstructionCode((UnaryOpInstruction) instruction, method);
            case GOTO:
                return JasminLoops.gotoInstruction((GotoInstruction) instruction, method);
            case BRANCH:
                return JasminLoops.branchInstruction((CondBranchInstruction) instruction, method);
        }
        throw new NotImplementedException(instruction.getInstType());
    }

    private static String addNoOper(SingleOpInstruction instruction,Method method) {
        return JasminLoadStore.loadElement(instruction.getSingleOperand(),method.getVarTable());
    }

    public static String getJasminOperationType(OperationType opType) {
        switch (opType){
            case ADD:
                return "iadd";
            case MUL:
                return "imul";
            case SUB:
                return "isub";
            case DIV:
                return "idiv";
            case ANDB:
            case NOTB:
                return "ifeq";
            case LTH:
                return "if_icmplt";
        }
            throw new NotImplementedException(opType);
    }
    public static String getJasminEspecialType(Type type) {
        switch (type.getTypeOfElement()) {
            case THIS:
                return JasminBuilder.classUnit.getClassName();
            case CLASS:
            case OBJECTREF:
                return ((ClassType) type).getName();
            default:
                throw new NotImplementedException(type.getTypeOfElement());

        }
    }

}
