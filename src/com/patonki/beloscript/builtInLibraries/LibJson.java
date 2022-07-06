package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.BeloLibrary;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloList;
import com.patonki.beloscript.datatypes.basicTypes.BeloObject;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
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
    public static BeloObject readJson(Settings settings) throws BeloScriptException {
        return ReadJsonCommand.readJson(settings);
    }
    public static class ReadJsonCommand extends BeloScriptFunction {
        public ReadJsonCommand() {
            super("json.read");
        }
        private static BeloObject readJson(Settings settings) throws BeloScriptException {
            String json = settings.getJsondata();
            JSONParser jsonParser = new JSONParser();

            try {
                Object obj = jsonParser.parse(json);
                BeloClass value = readJsonData(obj);

                if (value == null) throw new BeloScriptException(new BeloScriptError("Json error","Can't read json string"));
                return (BeloObject) value;
            } catch (ParseException e) {
                e.printStackTrace();
                throw new BeloScriptException(new BeloScriptError("Json error","Can't read json string\n"+e.getLocalizedMessage() ));
            }
        }
        @Override
        public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
            if (args.size() != 0) return throwParameterSizeError(res,context,0,args.size());
            try {
                BeloObject object = readJson(context.getSettings());
                return res.success(object);
            } catch (BeloScriptException e) {
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
                return new BeloString((String) obj);
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
            return null;
        }
        private static BeloList readJsonArray(JSONArray array) {
            ArrayList<BeloClass> list = new ArrayList<>();
            for (Object o : array) {
                BeloClass value = readJsonData(o);
                if (value == null) return null;
                list.add(value);
            }
            return new BeloList(list);
        }
        private static BeloObject readJsonObject(JSONObject object) {
            BeloObject obj = new BeloObject();
            for (Object o : object.keySet()) {
                BeloClass value = readJsonData(object.get(o));
                if (value == null)  return null;
                obj.setClassValue(o.toString(),value);
            }
            return obj;
        }
    }
    @Override
    public void addToSymbolTable(SymbolTable symbolTable) {
        symbolTable.defineFunction("readJsonInput", new ReadJsonCommand());
    }

    @Override
    public void close() {

    }
}
