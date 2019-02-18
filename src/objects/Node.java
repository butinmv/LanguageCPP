package objects;

import lexer.TokenType;

import java.util.ArrayList;

public class Node {
    String name;
    TypeObject typeObject;
    public TypeData typeData;

    public boolean isInit;
    public int count;
    public Object value = 0;


    private Node() {
        isInit = false;
    }

    public TypeObject getTypeObject() {
        return typeObject;
    }

    public static Node createVar(String name, TypeData typeData) {
        Node node = new Node();
        node.typeObject = TypeObject.VAR;
        node.name = name;
        node.typeData = typeData;
        return node;
    }

    public static Node createFunction(String name, TypeData typeData, int count) {
        Node node = new Node();
        node.typeObject = TypeObject.FUNCTION;
        node.name = name;
        node.count = count;
        node.typeData = typeData;
        return node;
    }

    public static Node createFunction(String name) {
        Node node = new Node();
        node.typeObject = TypeObject.FUNCTION;
        node.name = name;
        node.typeObject = TypeObject.EMPTY;
        return node;
    }

    public static Node createEmptyNode() {
        Node node = new Node();
        node.typeObject = TypeObject.EMPTY;
        return node;
    }

    public static Node createConst(TypeData typeData) {
        Node node = new Node();
        node.typeObject = TypeObject.CONST;
        node.typeData = typeData;
        return node;
    }

    public static Node createUnknowk() {
        Node node = new Node();
        node.typeObject = TypeObject.CONST;
        node.typeData = TypeData.UNKNOWN;
        return node;
    }

    @Override
    public String toString() {
        if (typeObject == TypeObject.EMPTY)
            return typeObject.toString();
        String str = typeObject.toString();
        if (typeObject == TypeObject.VAR) {
            if (typeData == TypeData.INTEGER)
                str += " Value=" + Integer.parseInt(String.valueOf(value));
            else
                str += " Value=" + Boolean.parseBoolean(String.valueOf(value));
        }

        return str;
    }
}
