package pt.up.fe.comp.SymbolTable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;

public class JmmMethod {
    private String name;
    private Type returnType;
    private List<Symbol> parameters;
    private final HashMap<String, Symbol> localVariables = new HashMap<>();

    public JmmMethod(String name, Type returnType, List<Symbol> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        for (Symbol parameter : parameters)
            localVariables.put(parameter.getName(), parameter);
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public List<Symbol> getLocalVariables() {
        return new ArrayList<>(localVariables.values());
    }

    public String getIdentifier() {

        List<String> parameter_types = new ArrayList<>();
        for (Symbol i : parameters) {
            String parameterId = i.getType().getName() + (i.getType().isArray() ? "[]" : "");
            parameter_types.add(parameterId);
        }

        return String.join("-", name, String.join("-", parameter_types));
    }

    public boolean addLocalVariable(Type type, String name) {
        if(localVariables.containsKey(name)){
            return false;
        }
        localVariables.put(name, new Symbol(type, name));
        return true;
    }

    public Symbol getLocalVariable(String varName) {
        return localVariables.getOrDefault(varName, null);
    }

}
