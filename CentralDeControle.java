public class CentralDeControle extends EntidadeSimulavel {
    private boolean horarioPico;
    private int tempoParaLocomoverElevador;
    private Estatisticas coletorEstatisticas;
    private ListaElevadores listaElevadores;
    private ListaAndares listaAndares;
    private ListaChamadas chamadasGlobais;
    private Predio predio;

    public CentralDeControle(Predio predio, ListaAndares listaAndares, Estatisticas coletorEstatisticas, ListaChamadas chamadasGlobais) {
        this.listaElevadores = new ListaElevadores();
        this.listaAndares = listaAndares;
        this.coletorEstatisticas = coletorEstatisticas;
        this.chamadasGlobais = chamadasGlobais;
        this.predio = predio;
        horarioPico = false;
        tempoParaLocomoverElevador = 20;
    }

    @Override
    public void atualizar(int minutoSimulado, int tempoViagem) {
        inicializarElevadores();
        distribuirChamadasDinamicamente();
        verificarChamadasAtendidas();
        verificarHorarioPico(minutoSimulado);
        iniciarElevadores(minutoSimulado, tempoViagem);
        coletarEstatisticas(minutoSimulado);
    }

    public void verificarHorarioPico(int minutoSimulado) {
        //Se houver 3 pessoas por andar (em média) considera-se horario de pico
        if (getTotalPessoasAguardando() >= (listaAndares.getTamanho()*3)) {
            horarioPico = true;
            tempoParaLocomoverElevador = 15;
        }else {
            horarioPico = false;
            tempoParaLocomoverElevador = 20;
        }
    }


    // Coleta as estatísticas
    public void coletarEstatisticas(int minutoSimulado) {
        int totalConsumoEnergia = 0;
        int totalTempoEspera = 0;
        int totalTempoViagem = 0;
        int totalPessoasAtendidas = 0;
        int totalPessoasAguardando = 0;

        boolean fimDaHora = minutoSimulado % 60 == 59;

        NodeElevador nodeElevador = listaElevadores.getInicio();
        while (nodeElevador != null) {
            Elevador elevador = nodeElevador.getElevador();

            // Soma energia total acumulada durante a hora
            totalConsumoEnergia += elevador.getEnergiaGasta();

            if (fimDaHora) {
                // Coleta dados dos passageiros desembarcados
                ListaPassageiros desembarcados = elevador.getPassageirosDesembarcados();
                NodePassageiro atual = desembarcados.getInicio();

                while (atual != null) {
                    Pessoa pessoa = atual.getPassageiro();
                    totalTempoEspera += (int) pessoa.getTempoEsperando();
                    totalTempoViagem += (int) pessoa.getTempoEmbarcado();
                    totalPessoasAtendidas++;
                    atual = atual.getProximo();
                }

                // Limpa lista de desembarcados no fim da hora
                desembarcados.limpar();

                // Zera energia do elevador no fim da hora
                elevador.zerarEnergiaGasta();
            }

            nodeElevador = nodeElevador.getProximo();
        }

        // Obtém o total de pessoas aguardando no prédio (fora do loop)
        totalPessoasAguardando = predio.getPessoasGeradas();

        // Só registra estatísticas completas no fim da hora
        if (fimDaHora) {
            predio.getEstatisticas().atualizarEstatisticas(minutoSimulado, totalPessoasAtendidas, totalTempoEspera, totalTempoViagem, totalConsumoEnergia, totalPessoasAguardando);
        }
    }

    //Chama a simulação do elevador a cada ciclo
    public void iniciarElevadores(int minutoSimulado, int tempoViagem) {
        NodeElevador atual = listaElevadores.getInicio();
        while (atual != null) {
            Elevador elevador = atual.getElevador();

            if (!elevador.isInicializado()) {
                elevador.atualizar(minutoSimulado, tempoViagem);
            }
            elevador.simular(tempoViagem);
            atual = atual.getProximo();
        }
    }

    //Metodo para inicializar os elevadores
    private void inicializarElevadores() {
        NodeElevador atual = listaElevadores.getInicio();

        while (atual != null) {
            Elevador elevador = atual.getElevador();

            if (!elevador.isInicializado()) {
                elevador.inicializar();
            }
            atual = atual.getProximo();
        }
    }

    //Distribui as chamadas dinamicamente
    public void distribuirChamadasDinamicamente() {
        NodeChamadas chamada = chamadasGlobais.getPrimeiro();
        ListaAndares andaresJaAtribuidos = new ListaAndares();

        while (chamada != null) {
            Andar origem = chamada.getAndarOrigem();
            int numeroOrigem = origem.getNumero();

            // Verifica se o andar já foi atribuído nesta rodada
            if (andaresJaAtribuidos.contemAndar(numeroOrigem)) {
                chamada = chamada.getProximo();
                continue;
            }

            // Verifica se o andar já está na lista de chamadas de algum elevador
            boolean jaEstaSendoAtendido = false;
            NodeElevador verificador = listaElevadores.getInicio();
            while (verificador != null) {
                if (verificador.getElevador().getListaChamadas().contemChamada(numeroOrigem)) {
                    jaEstaSendoAtendido = true;
                    break;
                }
                verificador = verificador.getProximo();
            }

            if (jaEstaSendoAtendido) {
                chamada = chamada.getProximo();
                continue;
            }

            //Escolhe o melhor elevador
            NodeElevador melhorElevador = null;
            int menorPontuacao = Integer.MAX_VALUE;

            NodeElevador nodeElevador = listaElevadores.getInicio();
            while (nodeElevador != null) {
                Elevador elevador = nodeElevador.getElevador();

                if (elevador.getEmbarcados() >= elevador.getCapacidadeMaxima() || elevador.getListaChamadas().getTamanho() >= 4) {
                    nodeElevador = nodeElevador.getProximo();
                    continue;
                }

                int andarAtual = elevador.getAndarAtual().getValor().getNumero();
                int distancia = Math.abs(andarAtual - numeroOrigem);

                boolean mesmaDirecao = (elevador.isSubindo() && numeroOrigem > andarAtual) || (!elevador.isSubindo() && numeroOrigem < andarAtual);
                boolean parado = elevador.isParado();
                boolean ocupado = elevador.getEmbarcados() > 0;

                int pontuacao = distancia;
                pontuacao += ocupado ? 30 : 0;
                pontuacao += mesmaDirecao ? 0 : 50;
                pontuacao += parado ? -20 : 10;

                if (pontuacao < menorPontuacao) {
                    menorPontuacao = pontuacao;
                    melhorElevador = nodeElevador;
                }

                nodeElevador = nodeElevador.getProximo();
            }

            if (melhorElevador != null) {
                Elevador e = melhorElevador.getElevador();
                e.getListaChamadas().adicionarChamada(origem, numeroOrigem);
                andaresJaAtribuidos.inserirFim(origem);
            }

            chamada = chamada.getProximo();
        }
    }

    //Verifica se uma chamada foi atendida e a retira da lista de chamadas global
    public void verificarChamadasAtendidas() {
        NodeChamadas chamada = chamadasGlobais.getPrimeiro();
        while (chamada != null) {
            Andar andar = chamada.getAndarOrigem();
            if (andar.getFilaPessoas().isEmpty()) {
                chamadasGlobais.removerChamada(andar.getNumero());
                chamada = chamadasGlobais.getPrimeiro();
            } else {
                chamada = chamada.getProximo();
            }
        }
    }

    //Verifica a quantidade de pessoas aguardando naquele momento
    public int getTotalPessoasAguardando() {
        int total = 0;
        NodeAndar atual = listaAndares.getInicio();
        while (atual != null) {
            total += atual.getValor().getFilaPessoas().getTamanho();
            atual = atual.getProximo();
        }
        return total;
    }

    //Adiciona o elevador a lista de elevadores
    public void adicionarElevador(Elevador elevador) {
        listaElevadores.inserir(elevador);
    }

    //Getters
    public boolean isHorarioPico() {
        return horarioPico;
    }

    public ListaElevadores getElevadores() {
        return listaElevadores;
    }

    public ListaChamadas getChamadasGlobais(){
        return chamadasGlobais;
    }

    public int getTempoParaLocomoverElevador() {
        return tempoParaLocomoverElevador;
    }
}
