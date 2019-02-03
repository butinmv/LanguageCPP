package objects;

public class ProgramTree {
    public Node node;
    private ProgramTree up;
    public ProgramTree left;
    public ProgramTree right;

    public ProgramTree(Node node, ProgramTree up, ProgramTree left, ProgramTree right) {
        this.node = node;
        this.up = up;
        this.left = left;
        this.right = right;
    }

    public void setLeft(Node node) {
        left = new ProgramTree(node, this, null, null);
    }

    public void setRight(Node node) {
        right = new ProgramTree(node, this, null, null);
    }

    public ProgramTree findUpFunction(String name) {
        ProgramTree i = this;
        while (i != null && !(name.equals(i.node.name) && i.node.typeObject == TypeObject.FUNCTION)) {
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
