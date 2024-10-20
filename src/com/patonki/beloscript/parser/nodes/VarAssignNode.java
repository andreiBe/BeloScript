package com.patonki.beloscript.parser.nodes;

import com.patonki.beloscript.Calculation;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.lexer.Token;
import com.patonki.beloscript.lexer.TokenType;

public class VarAssignNode extends Node {
    private final Node value;
    private final Setter setter;
    private final Calculation calculation;
    private final Node var;

    private interface Setter {
        RunTimeResult set(BeloClass value,Context context,Interpreter interpreter,RunTimeResult res);
    }
    public VarAssignNode(Node var, Token token, Node expression) {
        this.var = var;
        this.value = expression;
        this.start = var.getStart();
        this.end = expression.getEnd();
        this.calculation = TokenType.getMatchingCalculation(token.getType());

        //normaali arvon asetus. Esim: muuttuja = 9
        if (var instanceof VarAccessNode) {
            VarAccessNode variableName = (VarAccessNode) var;
            if (variableName.isFinal()) {
                setter = (value, context, interpreter, res) -> {
                    if (context.getSymboltable().isFinal(variableName.getVarName())) {
                        return res.failure(new RunTimeError(
                                getStart(), getEnd(), "Can't assign to a final variable",
                                context
                        ));
                    }
                    context.getSymboltable().set(variableName.getVarName(), value);
                    context.getSymboltable().makeFinal(variableName.getVarName());
                    return res.success(value, getStart(), getEnd());
                };
            } else {
                setter = (value, context,interpreter,res) -> {
                    if (context.getSymboltable().isFinal(variableName.getVarName())) {
                        return res.failure(new RunTimeError(
                                getStart(), getEnd(), "Can't assign to a final variable",
                                context
                        ));
                    }
                    if (calculation != null) context.getSymboltable().change(variableName.getVarName(),value);
                    else context.getSymboltable().set(variableName.getVarName(),value);
                    return res.success(value, getStart(), getEnd());
                };
            }
        }
        //arvon asettaminen indeksissä n. Esim: muuttuja[8] = 9
        else if (var instanceof IndexAccessNode) {
            IndexAccessNode indexNode = (IndexAccessNode) var;
            setter = (value,context,interpreter, res) -> {
                BeloClass target = res.register(indexNode.getTarget().execute(context,interpreter));
                if (res.shouldReturn()) return res;

                BeloClass index = res.register(indexNode.getIndex().execute(context,interpreter));
                if (res.shouldReturn()) return res;

                BeloClass result = target.setIndex(index,value);
                if (result.hasError()) {
                    return res.failure(result.getError());
                } else {
                    return res.success(result, getStart(), getEnd());
                }
            };
        }
        //objektin muuttujan asettaminen. Esim: muuttuja.ika = 9
        else if (var instanceof DotNode) {
            DotNode dotNode = (DotNode) var;
            StringNode memberNode = dotNode.getMemberString();
            BeloString member = BeloString.create(memberNode.getToken().getValue());
            member.setPos(memberNode.getStart(), memberNode.getEnd());
            this.setter = (value, context, interpreter, res) -> {
                BeloClass object = res.register(dotNode.executeLeft(context,interpreter));
                if (res.shouldReturn()) return res;
                BeloClass result = object.setClassValue(member,value);
                if (result.hasError()) {
                    return res.failure(result.getError());
                } else {
                    return res.success(result, getStart(), getEnd());
                }
             };
        }
        //Ei pitäisi tapahtua, koska parametrit tarkistetaan jo ennen tänne syöttämistä
        else {
            throw new IllegalArgumentException("Type of variable not supported: "+var.getClass().getSimpleName());
        }
    }
    @Override
    public RunTimeResult execute(Context context, Interpreter interpreter) {
        RunTimeResult res = new RunTimeResult();
        BeloClass value = res.register(this.value.execute(context, interpreter));
        if (res.shouldReturn()) return res;
        if (this.calculation != null) {
            BeloClass orgVarValue = res.register(this.var.execute(context,interpreter));
            if (res.shouldReturn()) return res;

            value = calculation.calculate(orgVarValue,value);
            if (value.hasError()) return res.failure(value.getError());
        }
        value.setPos(getStart(),getEnd());
        //value.setContext(context);
        return this.setter.set(value,context,interpreter,res);
    }

    @Override
    public String toString() {
        return "{var-assign: "+var+" = "+value+"}";
    }
}
