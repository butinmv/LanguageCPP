package objects;

public class Node {
    String name;
    TypeObject typeObject;
    public TypeData typeData;

    public boolean isInit;
    public int count;

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

    public static Node createEmptyNode() {
        Node node = new Node();
        node.typeObject = TypeObject.EMPTY;
        return node;
    }

    public static Node createConst(TypeData typeData) {
        Node node = new Node();
        node.typeObject = TypeObject.CONST;
        node.typeData = TypeData.UNKNOWN;
        return node;
    }

}
