package pt.up.fe.comp.Jasmin;

import org.specs.comp.ollir.*;

import java.util.ArrayList;
import java.util.HashMap;

public class JasminBuilder {

    private StringBuilder jasminCode;
    public static ClassUnit classUnit;
    public static int currentStack;
    public static int maxStack;

    public static int conditionals;

    public JasminBuilder(ClassUnit classUnit) {
        this.jasminCode = new StringBuilder();
        JasminBuilder.classUnit = classUnit;
    }

    public String build() {
        addClassName();
        addSuperClass();
        addFields();
        addMethods();

        return jasminCode.toString();
    }

    private void addClassName() {
        jasminCode.append(".class public ").append(classUnit.getClassName()).append("\n");
    }

    private void addSuperClass() {
        String superName;
        if (classUnit.getSuperClass() == null)
            superName = "java/lang/Object";
        else
            superName = classUnit.getSuperClass();
        jasminCode.append(".super ").append(superName).append("\n");
    }

    private void addFields() {
        for (var field : classUnit.getFields()) {
            jasminCode.append(".field ");
            String accessModifiers = JasminUtils.getAccessModifiers(field.getFieldAccessModifier());
            jasminCode.append(accessModifiers);
            if (field.isStaticField())
                jasminCode.append(" static");
            if (field.isFinalField())
                jasminCode.append(" final");
            jasminCode.append(" ").append(field.getFieldName()).append(" ");
            String jasminType = JasminUtils.getJasminType(field.getFieldType());
            jasminCode.append(jasminType);
            if (field.isInitialized()) {
                jasminCode.append(" = ").append(field.getInitialValue());
            }
            jasminCode.append("\n");
        }

    }

    private void addMethods() {

        for (var method : classUnit.getMethods()) {
            currentStack=0;
            maxStack=0;
            conditionals=0;
            jasminCode.append("\n.method public ");
            if (method.isStaticMethod())
                jasminCode.append("static ");
            if (method.isFinalMethod())
                jasminCode.append("final ");

            if (method.isConstructMethod())
                jasminCode.append("<init>");
            else
                jasminCode.append(method.getMethodName());

            jasminCode.append("(");
            for (var param : method.getParams())
                jasminCode.append(JasminUtils.getJasminType(param.getType()));
            jasminCode.append(")").append(JasminUtils.getJasminType(method.getReturnType())).append("\n");

            StringBuilder instructionCode= new StringBuilder();

            for(var instruction : method.getInstructions()){
                for(var label : method.getLabels(instruction))
                    instructionCode.append(label).append(":\n");
                instructionCode.append(JasminUtils.addInstructions(instruction,method));

                if(instruction.getInstType()==InstructionType.CALL){
                    if(((CallInstruction)instruction).getReturnType().getTypeOfElement()!=ElementType.VOID){
                        instructionCode.append("pop\n");
                        JasminUtils.limitStack(-1);
                    }
                }
            }

            ArrayList<Integer> variables = new ArrayList<>();
            for (Descriptor d : method.getVarTable().values()) {
                if (!variables.contains(d.getVirtualReg()))
                    variables.add(d.getVirtualReg());
            }
            if (!variables.contains(0) && !method.isStaticMethod())
                variables.add(0);

            jasminCode.append(".limit stack ").append(maxStack).append("\n");
            jasminCode.append(".limit locals ").append(variables.size()).append("\n");

            if(currentStack!=0){
                System.out.println("Error: the current stack is different from 0!");
            }
            jasminCode.append(instructionCode);

            if(method.getReturnType().getTypeOfElement()==ElementType.VOID)
                jasminCode.append("return\n");
            jasminCode.append(".end method\n");
        }
    }


}
