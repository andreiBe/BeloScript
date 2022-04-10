package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.BeloLibrary;
import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.BeloList;
import com.patonki.beloscript.datatypes.basicTypes.BeloObject;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;
import com.patonki.beloscript.interpreter.SymbolTable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class LibJson implements BeloLibrary {
    public static class ReadJsonCommand extends BeloScriptFunction {
        public ReadJsonCommand(String name) {
            super(name);
        }

        @Override
        public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
            if (args.size() != 0) return throwParameterSizeError(res,context,0,args.size());
            String json = context.getSettings().getJsondata();
            JSONParser jsonParser = new JSONParser();

            try {
                Object obj = jsonParser.parse(json);
                BeloClass value = readJsonData(obj);

                if (value == null) return throwError(res,context,"Can't read json string");
                return res.success(value);
            } catch (ParseException e) {
                return throwError(res,context,"Can't read json string");
            }
        }
        private BeloClass readJsonData(Object obj) {
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
        private BeloList readJsonArray(JSONArray array) {
            ArrayList<BeloClass> list = new ArrayList<>();
            for (Object o : array) {
                BeloClass value = readJsonData(o);
                if (value == null) return null;
                list.add(value);
            }
            return new BeloList(list);
        }
        private BeloObject readJsonObject(JSONObject object) {
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
        symbolTable.defineFunction("readJsonInput", new ReadJsonCommand("readJsonInput"));
    }

    @Override
    public void close() {

    }
}
