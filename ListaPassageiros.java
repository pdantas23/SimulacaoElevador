public class ListaPassageiros {
    private NodePassageiro inicio;
    private NodePassageiro fim;
    private int tamanho;

    public ListaPassageiros() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    //Remove um passageiro da lista
    public void remover(NodePassageiro atual) {
        if (atual == null || inicio == null) return;

        if (inicio == fim && atual == inicio) {
            inicio = fim = null;
            tamanho--;
            return;
        }

        if (atual == inicio) {
            inicio = inicio.getProximo();
            inicio.setAnterior(null);
        }

        else if (atual == fim) {
            fim = fim.getAnterior();
            fim.setProximo(null);
        } else {
            NodePassageiro anterior = atual.getAnterior();
            NodePassageiro proximo = atual.getProximo();

            if (anterior != null) anterior.setProximo(proximo);
            if (proximo != null) proximo.setAnterior(anterior);
        }
        tamanho--;
    }

    //Adiciona um passageiro na lista
    public void adicionarNoFim(Passageiro passageiro) {
        if (passageiro == null) return;

        NodePassageiro novo = new NodePassageiro(passageiro);

        if (inicio == null) {
            inicio = fim = novo;
        } else {
            fim.setProximo(novo);
            novo.setAnterior(fim);
            fim = novo;
        }
        tamanho++;
    }

    // Metodo para mostrar os passageiros embarcados
    public void mostrarPassageiros() {
        if (inicio == null) {
            System.out.println("Nenhum passageiro no elevador.");
            return;
        }
        NodePassageiro atual = inicio;

        while (atual != null) {
            System.out.println("ID: " + atual.getPassageiro().getId() + " | Andar de destino: " + atual.getPassageiro().getAndarDestino() + " | Tempo embarcado: " + atual.getPassageiro().getTempoEmbarcado());
            atual = atual.getProximo();
        }
    }

    //Limpa a lista de passageiros
    public void limpar() {
        inicio = null;
        tamanho = 0;
    }

    //Getters
    public boolean isEmpty() {
        return inicio == null;
    }

    public NodePassageiro getInicio() {
        return inicio;
    }
}
