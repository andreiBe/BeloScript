package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.oop.ClassDefinition;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;

import java.util.List;

public class TryNode extends Node{
    public static class CatchBlock extends Node{
        private final String errorVarName;
        private final Node catchBody;
        private final VarAccessNode errorType;

        public CatchBlock(String errorVarName,Node catchBody, VarAccessNode errorType) {
            this.errorVarName = errorVarName;
            this.catchBody = catchBody;
            this.errorType = errorType;
            this.start = catchBody.getStart();
            this.end = catchBody.getEnd();
        }
    }
    private final Node body;
    private final List<CatchBlock> catchBlocks;

    public TryNode(Node body, List<CatchBlock> catchBlocks, Position start, Position end) {
        this.body = body;
        this.catchBlocks = catchBlocks;
        this.start = start;
        this.end = end;
        if (catchBlocks == null) {
            this.setVisitMethod(this::noCatch);
        } else {
            this.setVisitMethod(this::visit);
        }
    }

    private RunTimeResult noCatch(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            return res.success(new Null(),getStart(),getEnd());
        }
        if (res.shouldReturn()) return res;
        //result.setPos(body.getStart(),body.getEnd());
        return res.success(result, getStart(), getEnd());
    }

    private RunTimeResult visit(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass result = res.register(body.execute(context,interpreter));
        if (res.hasError()) {
            BeloClass error = res.getError().getErrorObject() == null
                    ? new BeloError(res.getError())
                    : res.getError().getErrorObject();
            for (CatchBlock catchBlock : catchBlocks) {
                RunTimeResult catchBlockRes = new RunTimeResult();
                BeloClass type = catchBlock.errorType == null ? null
                        : catchBlockRes.register(catchBlock.errorType.execute(context, interpreter));
                if (catchBlockRes.shouldReturn()) return catchBlockRes;
                Context newContext = new Context("catch block", context, catchBlock.getStart());
                if (type != null && !(type instanceof ClassDefinition)) {
                    return res.failure(new RunTimeError(
                            catchBlock.start, catchBlock.end, "Expected class definition", newContext
                    ));
                }
                if (type == null || ((ClassDefinition) type).isInstanceOf(error)) {
                    newContext.setSymboltable(new SymbolTable(newContext.getParent().getSymboltable()));
                    newContext.getSymboltable().set(catchBlock.errorVarName, error);

                    result = res.register(catchBlock.catchBody.execute(newContext,interpreter));
                    if (res.shouldReturn()) return res;
                    break;
                }
            }
        }
        if (res.shouldReturn()) return res;
        return res.success(result, getStart(), getEnd());
    }

    @Override
    public String toString() {
        return "{try body:"+body+" catches: "+catchBlocks+"}";
    }
}
