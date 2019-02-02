package objects;

import java.util.ArrayList;

public class Node {
    private String name;
    private TypeObject typeObject;
    private TypeData typeData;

    private boolean isInit;
    private int count;
    private static ArrayList<TypeData> typeOfParameters;

    private Node() {
        isInit = false;
    }

    public static void setTypeOfParameters(ArrayList<TypeData> typeOfParameters) {
        Node.typeOfParameters = typeOfParameters;
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

    public static Node createFunction(String name, TypeData typeData, int count, ArrayList<TypeData> typeOfParameters) {
        Node node = new Node();
        node.typeObject = TypeObject.FUNCTION;
        node.name = name;
        node.count = count;
        node.typeData = typeData;
        Node.typeOfParameters.addAll(typeOfParameters);
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


}
