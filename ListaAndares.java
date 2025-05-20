public class ListaAndares {
    private NodeAndar primeiro;
    private NodeAndar ultimo;
    private int tamanho;

    public ListaAndares() {
        primeiro = null;
        ultimo = null;
    }

    // Insere os andares no fim da lista
    public void inserirFim(Andar novo) {
        NodeAndar novoAndar = new NodeAndar(novo);
        if (primeiro == null) {
            primeiro = novoAndar;
            ultimo = novoAndar;
        } else {
            ultimo.setProximo(novoAndar);
            novoAndar.setAnterior(ultimo);
            ultimo = novoAndar;
        }
        tamanho++;
    }

    //Verifica se há um andar de acordo com o número
    public boolean contemAndar(int numero) {
        NodeAndar atual = primeiro;
        while (atual != null) {
            if (atual.getValor().getNumero() == numero) {
                return true;
            }
            atual = atual.getProximo();
        }
        return false;
    }

    // Getters
    public NodeAndar getInicio() {
        return primeiro;
    }

    public int getTamanho() {return tamanho;}
}
