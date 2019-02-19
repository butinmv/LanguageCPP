package objects;

import lexer.TokenType;

import java.util.ArrayList;

public class Node {
    String name;
    TypeObject typeObject;
    public TypeData typeData;
    ArrayList<TypeData> parameters;

    public boolean isInit;
    public int count;
    public Object value = 0;

    public void setParameters(ArrayList<TypeData> parameters) {
        this.parameters = parameters;
    }

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

    public static Node createFunction(String name, TypeData typeData) {
        Node node = new Node();
        node.typeObject = TypeObject.FUNCTION;
        node.name = name;
        node.typeData = typeData;
        node.parameters = null;
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
                str += " " + this.name + " " + typeData;
            else
                str += " " + this.name + " " + typeData;
        }
        if (typeObject == TypeObject.FUNCTION) {
            str += " " + this.name;
            str += " Result=" + this.typeData;
        }

        return str;
    }
}
