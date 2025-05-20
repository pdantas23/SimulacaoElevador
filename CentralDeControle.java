public class CentralDeControle extends EntidadeSimulavel {
    private boolean horarioPico;
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
    }

    @Override
    public void atualizar(int minutoSimulado) {
        inicializarElevadores();
        distribuirChamadasDinamicamente();
        verificarChamadasAtendidas();
        verificarHorarioPico(minutoSimulado);
        iniciarElevadores(minutoSimulado);
        coletarEstatisticas(minutoSimulado);
    }

    public void verificarHorarioPico(int minutoSimulado) {
        int horaAtual = (minutoSimulado / 60) % 24; // converte segundos para hora do dia (0 a 23)

        // Se estiver entre 7h e 18h59 é horário comercial
        if (horaAtual >= 7 && horaAtual < 19) {
            horarioPico = true;
        }
    }

    //Coleta as estatísticas
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
                    totalTempoViagem += (int) atual.getPassageiro().getTempoEmbarcado();
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

        // Pessoas aguardando são medidas por minuto e acumuladas por hora
        NodeAndar nodeAndar = predio.getListaAndares().getInicio();
        while (nodeAndar != null) {
            Andar andar = nodeAndar.getValor();
            totalPessoasAguardando = predio.getQuantidadePessoas();
            nodeAndar = nodeAndar.getProximo();
        }

        // Só registra estatísticas completas no fim da hora
        if (fimDaHora) {
            predio.getEstatisticas().atualizarEstatisticas(minutoSimulado, totalPessoasAtendidas, totalTempoEspera, totalTempoViagem, totalConsumoEnergia, totalPessoasAguardando);
        }
    }

    //Chama a simulação do elevador a cada ciclo
    public void iniciarElevadores(int minutoSimulado) {
        NodeElevador atual = listaElevadores.getInicio();
        while (atual != null) {
            Elevador elevador = atual.getElevador();

            if (!elevador.isInicializado()) {
                elevador.atualizar(minutoSimulado);
            }
            elevador.simular();
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
}
