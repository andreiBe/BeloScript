package com.patonki.beloscript.interpreter;


import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.parser.nodes.ImportNode;
import com.patonki.beloscript.parser.nodes.Node;

import java.io.File;

public class Interpreter {
    private BeloClass exported;

    public RunTimeResult execute(Node node, Context context) {
        return node.getVisit().visit(context,this);
    }
    public RunTimeResult importFile(ImportNode node, Context context) {
        String path = node.getPath();
        if (!new File(path).isAbsolute()) {
            path = context.getSettings().getRootPath()+path;
        }
        RunTimeResult res = new RunTimeResult();
        try {
            BeloScript script = new BeloScript();
            script.executeFile(path,context.getSettings().getArgs());
            Interpreter interpreter = script.getInterpreter();
            BeloClass exported = interpreter.exported;
            if (exported == null) {
                return res.failure(new RunTimeError(node.getStart(),node.getEnd(),
                        "File doesn't import anything",context));
            }
            return res.success(exported);
        } catch (BeloScriptException e) {
            return res.failure(new RunTimeError(e.getError(),context));
        }
    }

    public void setExported(BeloClass exported) {
        this.exported = exported;
    }
}
