package com.patonki.beloscript.errors;


import com.patonki.beloscript.Position;

public class StringWithArrows {
    public static String stringWithArrows(String text, Position start, Position end) {
        StringBuilder result = new StringBuilder();
        int idxStart = Math.max(text.lastIndexOf('\n',start.index-1),0);
        int idxEnd = text.indexOf('\n',idxStart+1);
        if (idxEnd < 0) idxEnd = text.length();

        int lineCount = end.lineNumber - start.lineNumber + 1;
        for (int i = 0; i < lineCount; i++) {
            String line = text.substring(idxStart,idxEnd);
            int colStart = i== 0 ? start.columnNumber : 0;
            int colEnd = i == lineCount-1 ? end.columnNumber : line.length()-1;
            result.append(line).append(System.lineSeparator());
            String spaces = new String(new char[colStart]).replace("\0", " ");
            String arrows = new String(new char[colEnd-colStart]).replace("\0","^");
            result.append(spaces).append(arrows);

            idxStart = idxEnd;
            idxEnd = text.indexOf('\n',idxStart+1);
            if (idxEnd < 0) {
                idxEnd = text.length();
            }
        }
        return result.toString();
        //return result.toString().replace("\t","");
    }
}
