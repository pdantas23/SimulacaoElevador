import java.util.Random;

public class Predio extends EntidadeSimulavel {
    private int quantidadeAndares;
    private int quantidadeElevadores;
    private int quantidadePessoas;
    private int pessoasGeradas;
    private CentralDeControle centralDeControle;
    private ListaAndares listaAndares;
    private ListaChamadas listaChamadasGlobal;
    private Estatisticas estatisticas;

    public Predio(int quantidadeAndares, int quantidadeElevadores) {
        this.quantidadeAndares = quantidadeAndares;
        this.quantidadePessoas = 0;
        this.quantidadeElevadores = quantidadeElevadores;
        this.listaAndares = new ListaAndares();
        this.listaChamadasGlobal = new ListaChamadas();
        criarAndares(quantidadeAndares);
        this.estatisticas = new Estatisticas();// Criando andares
        this.centralDeControle = new CentralDeControle(this, listaAndares, estatisticas, listaChamadasGlobal);
        criarElevadores();
        this.estatisticas = new Estatisticas();
    }

    @Override
    public void atualizar(int minutoSimulado, int tempoViagem) {
        exbirHoraDoDia(minutoSimulado);
        atualizarAndares(minutoSimulado, tempoViagem);
        centralDeControle.atualizar(minutoSimulado, tempoViagem);
    }

    public void gerarPessoasPorHora(int horaSimulada) {
        // Define a quantidade de pessoas com base na hora simulada
        if (horaSimulada >= 9 && horaSimulada < 17) {
            pessoasGeradas = quantidadePessoas = 40; // horário comercial
        } else if ((horaSimulada >= 6 && horaSimulada < 9) || (horaSimulada >= 17 && horaSimulada < 20)) {
            pessoasGeradas = quantidadePessoas = 25; // manhã e fim de tarde
        } else {
            pessoasGeradas = quantidadePessoas = 10; // madrugada
        }


        Random random = new Random();
        int totalAndares = listaAndares.getTamanho();

        for (int i = 0; i < quantidadePessoas; i++) {
            // Escolhe um andar aleatório
            int andarIndex = random.nextInt(totalAndares);
            NodeAndar andarNode = listaAndares.getInicio();

            for (int j = 0; j < andarIndex; j++) {
                andarNode = andarNode.getProximo();
            }

            Andar andarSelecionado = andarNode.getValor();

            // Adiciona pessoa se houver espaço na fila
            if (andarSelecionado.getPessoasAguardando().getTamanho() < 10) {
                Pessoa pessoa = Pessoa.gerarPessoaAleatoria(andarSelecionado);
                andarSelecionado.getPessoasAguardando().adicionarPessoa(pessoa);
            }
        }
    }

    //Atualiza todos os andares em cada iteração
    private void atualizarAndares(int minutoSimulado, int tempoViagem) {
        NodeAndar atual = listaAndares.getInicio();

        while (atual != null) {
            atual.getValor().atualizar(minutoSimulado, tempoViagem);
            atual = atual.getProximo();
        }
    }

    //Cria os andares ao iniciar o predio
    private void criarAndares(int quantidadeAndares) {
        for (int i = 0; i < quantidadeAndares; i++) {
            listaAndares.inserirFim(new Andar(i, getListaChamadasGlobal()));
        }
    }

    //Cria os elevadores ao iniciar o predio
    private void criarElevadores() {
        for (int i = 0; i < quantidadeElevadores; i++) {
            Elevador elevador = new Elevador(i + 1, listaAndares, centralDeControle);
            centralDeControle.adicionarElevador(elevador);
        }

    }

    //Exibe a hora do dia com base no minuto simulado
    private void exbirHoraDoDia(int minutoSimulado){
        int horaSimulada = minutoSimulado / 60;
        System.out.println("Hora do dia: " + horaSimulada);
    }


    //Getters
    public ListaChamadas getListaChamadasGlobal() {
        return listaChamadasGlobal;
    }

    public int getQuantidadePessoas(){ return quantidadePessoas; }

    public Estatisticas getEstatisticas() { return estatisticas; }

    public CentralDeControle getCentralDeControle() {
        return centralDeControle;
    }

    public ListaAndares getListaAndares() {
        return listaAndares;
    }

    public int getPessoasGeradas(){
        return pessoasGeradas;
    }

}