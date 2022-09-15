package pt.up.fe.comp.SymbolTable;

import java.util.Collections;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.Visitor.*;
import pt.up.fe.comp.jmm.analysis.JmmAnalysis;

import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;

import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

public class JmmAnalyser implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        if (TestUtils.getNumReports(parserResult.getReports(), ReportType.ERROR) > 0) {
            var errorReport = new Report(ReportType.ERROR, Stage.SEMANTIC, -1,
                    "Started semantic analysis but there are errors from previous stage");
            return new JmmSemanticsResult(parserResult, null, Collections.singletonList(errorReport));
        }

        if (parserResult.getRootNode() == null) {
            var errorReport = new Report(ReportType.ERROR, Stage.SEMANTIC, -1,
                    "Started semantic analysis but AST root node is null");
            return new JmmSemanticsResult(parserResult, null, Collections.singletonList(errorReport));
        }

        Analysis analysis = new Analysis();
        JmmNode node= parserResult.getRootNode();

        SymbolTableVisitor visitor = new SymbolTableVisitor();
        visitor.visit(node, analysis);

        new SemanticAnalysisVisitor().visit(node, analysis);

        System.out.println(analysis.getSymbolTable().print());
        for(int i = 0; i < analysis.getReports().size(); i++){
            System.out.println(analysis.getReports().get(i));
        }

        return new JmmSemanticsResult(parserResult, analysis.getSymbolTable(), analysis.getReports());

    }

}


