public class NodeRegistroHora {
    private RegistroHora dado;
    private NodeRegistroHora proximo;

    public NodeRegistroHora(RegistroHora dado) {
        this.dado = dado;
    }

    //Getters e Setters
    public RegistroHora getDado() { return dado; }

    public NodeRegistroHora getProximo() { return proximo; }

    public void setProximo(NodeRegistroHora proximo) { this.proximo = proximo; }
}