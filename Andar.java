public class Andar extends EntidadeSimulavel {
    private int numero;
    private Andar proximo;
    private FilaPessoas pessoasAguardando;
    private PainelExterno painel;

    public Andar(int numero, ListaChamadas listaChamadasGlobal) {
        this.numero = numero;
        this.pessoasAguardando = new FilaPessoas();
        this.painel = new PainelExterno(this, listaChamadasGlobal);
        this.proximo = null;
    }

    @Override
    public void atualizar(int minutoSimulado, int tempoViagem) {
        verificarChamadaNecessaria();
        incrementarTempoFila(tempoViagem);
    }

    //Verifica se há pessoas esperando no andar
    public void verificarChamadaNecessaria() {
        if (!pessoasAguardando.isEmpty() && !painel.isBotaoPressionado()) {
            Pessoa pessoa = pessoasAguardando.getInicio().getPessoa();
            painel.digitarAndar(pessoa.getAndarDestino(), pessoa);
        }else{
            painel.setBotaoPressionado(false);
        }
    }

    //Incrementa o tempo esperado na fila por cada pessoa
    public void incrementarTempoFila(int tempoViagem) {
        NodePessoa atual = pessoasAguardando.getInicio();
        while (atual != null) {
            atual.getPessoa().incrementarTempoEsperando(tempoViagem);
            atual = atual.getProximo();
        }
    }



    //Getters
    public FilaPessoas getFilaPessoas() {
        return pessoasAguardando;
    }

    public int getNumero() {
        return numero;
    }

    public FilaPessoas getPessoasAguardando() {
        return pessoasAguardando;
    }

    public Andar getProximo() {
        return proximo;
    }

    public PainelExterno getPainelExterno() {
        return painel;
    }
}

