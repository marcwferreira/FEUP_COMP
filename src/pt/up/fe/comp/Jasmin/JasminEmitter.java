package pt.up.fe.comp.Jasmin;

import java.util.Collections;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.OllirErrorException;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;

public class JasminEmitter implements JasminBackend {

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        ClassUnit classUnit=ollirResult.getOllirClass();
        try {
            classUnit.checkMethodLabels();
        } catch (OllirErrorException e) {
        }
        classUnit.buildCFGs();
        classUnit.buildVarTables();
        classUnit.show();
        JasminBuilder jasminBuilder = new JasminBuilder(classUnit);
        String jasminCode=jasminBuilder.build();
        System.out.println(jasminCode);
        return new JasminResult(ollirResult, jasminCode, Collections.emptyList());
    }
    
}


