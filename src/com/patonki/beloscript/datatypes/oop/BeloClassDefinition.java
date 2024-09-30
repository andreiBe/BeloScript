package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.basicTypes.Obj;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;
import com.patonki.beloscript.parser.nodes.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class BeloClassDefinition extends BeloClass {
    private final List<String> parameters;
    private final LinkedHashMap<Node, Node> properties;
    private final HashMap<BeloClass, BeloClass> staticProperties;
    private final String className;
    public BeloClassDefinition(List<String> parameters, LinkedHashMap<Node, Node> properties,
                               HashMap<BeloClass,BeloClass> staticProperties, String className) {
        this.parameters = parameters;
        this.properties = properties;
        this.staticProperties = staticProperties;
        this.className = className;
    }
    private RunTimeResult createObject(RunTimeResult res, List<BeloClass> args) {
        Obj obj = Obj.create();
        Interpreter interpreter = new Interpreter();
        Context newContext = new Context(className, getContext(), this.getStart());
        newContext.setSymboltable(new SymbolTable(getContext().getSymboltable()));
        newContext.getSymboltable().set("self", obj);
        Node constructor = null;
        for (Node keyNode : properties.keySet()) {
            BeloClass key = res.register(interpreter.execute(keyNode, newContext));
            if (res.shouldReturn()) return res;

            if (key instanceof BeloString && key.toString().equals(className)) {
                constructor = properties.get(keyNode);
                continue;
            }
            Node propertyValue = properties.get(keyNode);

            BeloClass value = propertyValue == null ? new Null() : res.register(interpreter.execute(propertyValue, newContext));
            if (res.shouldReturn()) return res;

            obj.put(key, value);
        }
        for (int i = 0; i < this.parameters.size(); i++) {
            obj.put(BeloString.create(parameters.get(i)), args.get(i));
        }
        if (constructor != null) {
            res.register(interpreter.execute(constructor, newContext));
            if (res.shouldReturn()) return res;
        }
        return res.success(obj);
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        BeloClass val = this.staticProperties.get(name);
        return val == null ? new Null() : val;
    }

    @Override
    public RunTimeResult execute(List<BeloClass> args) {
        RunTimeResult res = new RunTimeResult();
        if (parameters.size() != args.size()) {
            return res.failure(new RunTimeError(getStart(), getEnd(),
                    "Argument count does not match. Should be " + parameters.size()
                            + " but was " + args.size()
                    , this.getContext()));
        }

        return createObject(res, args);
    }
}
