public class ListaRegistroHora {
    private NodeRegistroHora inicio;

    //Cria uma lista de registro de estatísticas conforme ahora
    public void adicionarOuAtualizar(int hora, int pessoasAtendidas, int tempoEspera, int tempoViagem, int energia, int pessoasAguardando) {
        NodeRegistroHora atual = inicio;

        while (atual != null) {
            if (atual.getDado().getHora() == hora) {
                atual.getDado().adicionar(pessoasAtendidas, tempoEspera, tempoViagem, energia, pessoasAguardando);
                return;
            }
            atual = atual.getProximo();
        }

        // Se não encontrou, cria novo
        RegistroHora novoRegistro = new RegistroHora(hora);
        novoRegistro.adicionar(pessoasAtendidas, tempoEspera, tempoViagem, energia, pessoasAguardando);
        NodeRegistroHora novoNode = new NodeRegistroHora(novoRegistro);

        if (inicio == null) {
            inicio = novoNode;
        } else {
            NodeRegistroHora aux = inicio;
            while (aux.getProximo() != null) {
                aux = aux.getProximo();
            }
            aux.setProximo(novoNode);
        }
    }

    //Getter
    public NodeRegistroHora getInicio() {return inicio;}
}
