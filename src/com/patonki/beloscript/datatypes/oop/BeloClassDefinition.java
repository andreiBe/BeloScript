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

import java.util.List;

public class BeloClassDefinition extends BeloClass {
    private final List<String> parameters;
    private final PropertiesAccess properties;
    private final String className;
    private final BeloClassDefinition parent;
    public BeloClassDefinition(List<String> parameters,
                               PropertiesAccess properties,
                               String className,
                               BeloClassDefinition parent) {
        this.parameters = parameters;
        this.properties = properties;
        this.className = className;
        this.parent = parent;
    }

    private RunTimeResult createObject(RunTimeResult res, List<BeloClass> args) {
        Obj obj = Obj.create();

        Interpreter interpreter = new Interpreter();
        Context newContext = new Context(className, getContext(), this.getStart());
        newContext.setSymboltable(new SymbolTable(getContext().getSymboltable()));
        newContext.getSymboltable().set("self", obj);

        Node constructor = this.findConstructorAndSetDefaultValuesOfProperties(
                res, interpreter, newContext, obj
        );
        if (res.shouldReturn()) return res;

        this.setConstructorValues(obj, args);
        this.executeConstructor(res, constructor, interpreter, newContext);
        if (res.shouldReturn()) return res;

        return res.success(obj);
    }
    private Node findConstructorAndSetDefaultValuesOfProperties(
            RunTimeResult res, Interpreter interpreter, Context context, Obj obj
    ) {
        Node constructor = null;
        for (String key : properties.getPropertyKeys()) {
            if (key.equals(className)) {
                constructor = properties.getPropertyValue(key);
                continue;
            }
            Node propertyValue = properties.getPropertyValue(key);

            BeloClass value = propertyValue == null
                    ? new Null()
                    : res.register(interpreter.execute(propertyValue, context));
            if (res.shouldReturn()) return null;

            obj.put(BeloString.create(key), value);
        }
        return constructor;
    }
    private void setConstructorValues(Obj obj, List<BeloClass> args) {
        for (int i = 0; i < this.parameters.size(); i++) {
            obj.put(BeloString.create(parameters.get(i)), args.get(i));
        }
    }
    private void executeConstructor(RunTimeResult res, Node constructor,
                                    Interpreter interpreter, Context context) {
        if (constructor == null) return;
        res.register(interpreter.execute(constructor, context));
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        return this.properties.getStaticProperty(name.toString());
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
