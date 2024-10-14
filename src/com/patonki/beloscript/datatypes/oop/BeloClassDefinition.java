package com.patonki.beloscript.datatypes.oop;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.basicTypes.Obj;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.Interpreter;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;
import com.patonki.beloscript.parser.nodes.Node;
import com.patonki.beloscript.parser.nodes.VarAccessNode;

import java.util.ArrayList;
import java.util.List;

public class BeloClassDefinition extends BeloClass {
    private final List<String> parameters;
    private final PropertiesAccess properties;
    private final String className;
    private final BeloClassDefinition parent;
    private final List<Node> parentParameters;
    private final AccessModifier accessModifier;

    public BeloClassDefinition(List<String> parameters,
                               PropertiesAccess properties,
                               String className,
                               BeloClassDefinition parent,
                               List<Node> parentParameters,
                               AccessModifier accessModifier) {
        this.parameters = parameters;
        this.properties = properties;
        this.className = className;
        this.parent = parent;
        this.parentParameters = parentParameters;
        this.accessModifier = accessModifier;
    }
    private List<BeloClass> getParametersForSuperConstructor(List<BeloClass> args,
                                                             RunTimeResult res,
                                                             Context context,
                                                             Interpreter interpreter) {
        if (this.parentParameters == null) {
            return args.subList(0, this.parent.parameters.size());
        }
        ArrayList<BeloClass> params = new ArrayList<>();

        for (Node parentParameter : this.parentParameters) {
            if (parentParameter instanceof VarAccessNode) {
                String varName = ((VarAccessNode) parentParameter).getVarName();
                int indexInParameters = parameters.indexOf(varName);
                if (indexInParameters != -1) {
                    params.add(args.get(indexInParameters));
                    continue;
                }
            }
            BeloClass value = res.register(parentParameter.execute(context, interpreter));
            if (res.shouldReturn()) return null;
            params.add(value);
        }
        return params;
    }

    private RunTimeResult createObject(RunTimeResult res, List<BeloClass> args, AccessModifier accessModifier) {
        Interpreter interpreter = new Interpreter();
        Context newContext = new Context(className, getContext(), this.getStart());
        newContext.setSymboltable(new SymbolTable(getContext().getSymboltable()));

        RuntimeProperties runtimeProperties = this.properties.getRuntimeProperties(
                res, interpreter, newContext, className
        );

        Node constructor = this.properties.getPropertyValue(this.className);
        this.setConstructorValues(runtimeProperties, args);
        BeloClassObject parent = null;
        if (this.parent != null) {
            List<BeloClass> params = getParametersForSuperConstructor(args, res, getContext(), interpreter);
            if (res.shouldReturn()) return res;
            parent = (BeloClassObject) res.register(
                    this.parent.getAsParent(res, params)
            );
            if (res.hasError()) return res;
            newContext.getSymboltable().set("super", parent);
            BeloClassDefinition parentCopy = this.parent;
            while (parentCopy != null) {
                newContext.getSymboltable().set(
                        parentCopy.className,
                        parentCopy.copyWithAccessModifier(AccessModifier.PROTECTED)
                );
                parentCopy = parentCopy.parent;
            }
        }

        BeloClassObject obj = new BeloClassObject(
                runtimeProperties, accessModifier ,parent, className
        );
        BeloClassObject privateObj = new BeloClassObject(
                runtimeProperties, AccessModifier.PRIVATE, parent, className
        );

        newContext.getSymboltable().set(className, this.copyWithAccessModifier(AccessModifier.PRIVATE));
        newContext.getSymboltable().set("self", privateObj);

        this.executeConstructor(res, constructor, interpreter, newContext);
        if (res.shouldReturn()) return res;

        return res.success(obj);
    }

    private BeloClassDefinition copyWithAccessModifier(AccessModifier modifier) {
        BeloClassDefinition definition =  new BeloClassDefinition(
                this.parameters,
                this.properties,
                this.className,
                this.parent,
                this.parentParameters,
                modifier
        );
        definition.setContext(getContext());
        return definition;
    }

    private void setConstructorValues(RuntimeProperties obj, List<BeloClass> args) {
        for (int i = 0; i < this.parameters.size(); i++) {
            String key = parameters.get(i);
            if (this.parent != null && this.parent.parameters.contains(key)) {
                continue;
            }
            obj.setProperty(key, args.get(i));
        }
    }
    private void executeConstructor(RunTimeResult res, Node constructor,
                                    Interpreter interpreter, Context context) {
        if (constructor == null) return;
        res.register(interpreter.execute(constructor, context));
    }
    //TODO DUPLICATE CODE: SAME AS IN BeloClassObject
    private BeloClass tryToFindFromParent(BeloClass name) {
        if (this.parent == null) return createNotAMemberOfClassError(name);
        BeloClass resultOfParent = this.parent.classValue(name);
        if (resultOfParent instanceof NotMemberOfClassError) {
            return createNotAMemberOfClassError(name);
        }
        return resultOfParent;
    }
    private BeloClass tryToSetInParent(BeloClass name, BeloClass value) {
        if (this.parent == null) return createNotAMemberOfClassError(name);
        BeloClass resultOfParent = this.parent.setClassValue(name.toString(), value);
        if (resultOfParent instanceof NotMemberOfClassError) {
            return createNotAMemberOfClassError(name);
        }
        return resultOfParent;
    }
    @Override
    public BeloClass classValue(BeloClass name) {
        if (!this.properties.hasStaticProperty(name.toString())) {
            return this.tryToFindFromParent(name);
        }
        AccessModifier target = this.properties.getAccessModifierOfStaticProperty(name.toString());
        if (!this.accessModifier.canAccess(target))
            return createCannotAccessError(name, target);
        return this.properties.getStaticProperty(name.toString());
    }

    @Override
    public BeloClass setClassValue(String name, BeloClass newValue) {
        if (!this.properties.hasStaticProperty(name)) {
            return tryToSetInParent(BeloString.create(name), newValue);
        }
        AccessModifier target = this.properties.getAccessModifierOfStaticProperty(name);
        if (!this.accessModifier.canAccess(target))
            return createCannotAccessError(BeloString.create(name), target);
        if (this.properties.staticPropertyIsFinal(name)) {
            return createCannotAssignToFinalVariableError(name);
        }
        this.properties.setStaticProperty(name, newValue);
        return newValue;
    }

    private RunTimeResult getAsParent(RunTimeResult res, List<BeloClass> args) {
        return createObject(res, args, AccessModifier.PROTECTED);
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

        return createObject(res, args, AccessModifier.PUBLIC);
    }
    private BeloError createCannotAccessError(BeloClass name, AccessModifier target) {
        return new CannotAccessError(new RunTimeError(name.getStart(),name.getEnd(),
                "Cannot access class member: '"+ name+"' class: "+this.getClass().getSimpleName()
                        + " because it is " + target,this.context));
    }
    @Override
    protected BeloError createNotAMemberOfClassError(BeloClass name) {
        return new NotMemberOfClassError(new RunTimeError(name.getStart(),name.getEnd(),
                "Not a member of class: '"+ name+"' class: "+this.className,this.context));
    }
    private BeloError createCannotAssignToFinalVariableError(String name) {
        return new BeloError(new RunTimeError(getStart(), getEnd(),
                "Cannot asign to final variable: " + name, this.context));
    }

    @Override
    public String toString() {
        return this.properties.toString();
    }
    private static class CannotAccessError extends BeloError {
        public CannotAccessError(RunTimeError e) {
            super(e);
        }
    }
    private static class NotMemberOfClassError extends BeloError {
        public NotMemberOfClassError(RunTimeError e) {
            super(e);
        }
    }

    public List<String> getParameters() {
        return parameters;
    }
}
