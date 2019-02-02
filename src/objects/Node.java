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

}
