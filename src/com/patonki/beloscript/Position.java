package com.patonki.beloscript;

public class Position {
    public int index;
    public int lineNumber;
    public int columnNumber;
    public String fileName;
    public String fileContent;

    public Position(int index, int lineNumber, int columnNumber, String fileName, String fileContent) {
        this.index = index;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public Position advance(char curChar) {
        index++;
        columnNumber++;
        if (curChar == '\n') {
            lineNumber++;
            columnNumber = 0;
        }
        return this;
    }
    @Override
    public String toString() {
        return "Position{" +
                "index=" + index +
                ", lineNumber=" + lineNumber +
                ", columnNumber=" + columnNumber +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public Position copy() {
        return new Position(index,lineNumber,columnNumber,fileName,fileContent);
    }
}
