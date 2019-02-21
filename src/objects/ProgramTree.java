package objects;

import lexer.TokenType;

import java.util.ArrayList;

public class ProgramTree {
    public Node node;
    private ProgramTree up;
    public ProgramTree left;
    public ProgramTree right;

    public ProgramTree(ProgramTree left, ProgramTree right, ProgramTree up, Node node) {
        this.node = node;
        this.up = up;
        this.left = left;
        this.right = right;
    }

    public ProgramTree() {
        node = Node.createEmptyNode();
        up = null;
        left = null;
        right = null;
    }

    public void setLeft(Node node) {
        left = new ProgramTree(null, null, this, node);
    }

    public void setRight(Node node) {
        right = new ProgramTree(null, null, this, node);
    }

    public ProgramTree findUpFunction(String name, TypeData typeData) {
        ProgramTree i = this;
        while  (i != null && !(name.equals(i.node.name) && i.node.typeObject == TypeObject.FUNCTION &&
                i.node.typeData == typeData)) {
            i = i.up;
        }
        return i;
    }

    public ProgramTree findUpVar(String name) {
        ProgramTree i = this;
        while (i != null && !(name.equals(i.node.name) && i.node.typeObject == TypeObject.VAR)) {
            i = i.up;
        }
        return i;
    }

    public ProgramTree findUpVarLevel(String name) {
        ProgramTree i = this.left;
        while (i != this.right && !(name.equals(i.node.name) && i.node.typeObject == TypeObject.VAR)) {
            i = i.up;
        }
        return i;
    }

    public void print(int n) {
        for (int i = 0; i < n; i++)
            System.out.print("\t");

        System.out.println(node);

        if (right != null)
            right.print(n + 1);

        if (left != null)
            left.print(n);
    }

    public ProgramTree findUp(String name) {
        ProgramTree i = this;
        while (i != null && !name.equals(i.node.name)) {
            i = i.up;
        }
        return i;
    }

}
