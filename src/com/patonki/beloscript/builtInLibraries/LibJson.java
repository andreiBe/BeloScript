package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.BeloLibrary;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.errors.LocalizedBeloException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.basicTypes.Obj;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.errors.BeloScriptError;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.Settings;
import com.patonki.beloscript.interpreter.SymbolTable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class LibJson implements BeloLibrary {
    public static class ReadJsonCommand extends BeloScriptFunction {
        public ReadJsonCommand() {
            super("json.read");
        }
        private BeloClass readJson(Settings settings) {
            String json = settings.getJsondata();
            if (json == null) {
                return new Null();
            }
            JSONParser jsonParser = new JSONParser();

            try {
                Object obj = jsonParser.parse(json);
                BeloClass value = readJsonData(obj);

                if (value == null)
                    throw new LocalizedBeloException(
                            new BeloScriptError( getStart(), getEnd(), "Json error",
                                    "Can't read json string"));
                return value;
            } catch (ParseException e) {
                throw new LocalizedBeloException(
                        new BeloScriptError(getStart(), getEnd(), "Json error","Can't read json string\n"+e.getLocalizedMessage() ));
            }
        }
        @Override
        public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
            if (!args.isEmpty()) return throwParameterSizeError(res,context,0,args.size());
            try {
                BeloClass object = readJson(context.getSettings());
                return res.success(object);
            } catch (LocalizedBeloException e) {
                return res.failure(new RunTimeError(e.getError(),context));
            }
        }
        private static BeloClass readJsonData(Object obj) {
            if (obj instanceof JSONArray) {
                return readJsonArray((JSONArray) obj);
            }
            if (obj instanceof JSONObject) {
                return readJsonObject((JSONObject) obj);
            }
            if (obj instanceof String) {
                return BeloString.create((String) obj);
            }
            if (obj instanceof Double) {
                return new BeloDouble((Double) obj);
            }
            if (obj instanceof Integer) {
                return new BeloDouble((Integer)obj);
            }
            if (obj instanceof Long) {
                return new BeloDouble((Long)obj);
            }
            if (obj == null) {
                return new Null();
            }
            return null;
        }
        private static com.patonki.beloscript.datatypes.basicTypes.List readJsonArray(JSONArray array) {
            ArrayList<BeloClass> list = new ArrayList<>();
            for (Object o : array) {
                BeloClass value = readJsonData(o);
                if (value == null) return null;
                list.add(value);
            }

            return com.patonki.beloscript.datatypes.basicTypes.List.create(list);
        }
        private static Obj readJsonObject(JSONObject object) {
            Obj obj = Obj.create();
            for (Object o : object.keySet()) {
                BeloClass value = readJsonData(object.get(o));
                if (value == null)  return null;
                obj.setClassValue(BeloString.create(o.toString()),value);
            }
            return obj;
        }
    }
    @Override
    public void addToSymbolTable(SymbolTable symbolTable) {
        symbolTable.defineFunction("readJsonInput", new ReadJsonCommand());
    }

}
