public class Elevador extends EntidadeSimulavel {
    private int id;
    private boolean inicializado;
    private int andarDestino;
    private boolean subindo;
    private final int capacidadeMaxima;
    private int tempoParaLocomover;
    private int embarcados;
    private int energiaGasta;
    private NodeAndar andarAtual;
    private CentralDeControle centralDeControle;
    private ListaPassageiros listaPassageiros;
    private ListaChamadas chamadasExclusivas;
    private ListaPassageiros passageirosDesembarcados;


    public Elevador(int id, ListaAndares listaAndares, CentralDeControle centralDeControle) {
        this.id = id;
        this.inicializado = false;
        this.capacidadeMaxima = 4;
        this.embarcados = 0;
        this.tempoParaLocomover = 0;
        this.subindo = true;
        this.andarDestino = 0;
        this.energiaGasta = 0;
        this.centralDeControle = centralDeControle;
        this.andarAtual = listaAndares.getInicio();
        this.listaPassageiros = new ListaPassageiros();
        this.chamadasExclusivas = new ListaChamadas();
        this.passageirosDesembarcados = new ListaPassageiros();
    }

    //Atualiza o estado atual do elevador
    @Override
    public void atualizar(int minutoSimulado, int tempoViagem) {
        simular(tempoViagem);
    }

    public void simular(int tempoViagem) {
        System.out.println("--------------------------------------------");
        System.out.println("Elevador:  " + id);
        System.out.println("Andar atual:  " + andarAtual.getValor().getNumero());
        chamadasExclusivas.exibirChamadas();

        // Desembarcar passageiros
        desembarcarPessoas(andarAtual.getValor());

        // Embarcar todos que for possível
        embarcarPessoas(this, andarAtual.getValor());

        // Encontrar próximo destino
        andarDestino = encontrarDestinoMaisProximo();

        //Verifica se há um andar de destino
        if (andarDestino != -1) {
            System.out.println("Andar destino: " + andarDestino);
            mover(tempoViagem);
        } else {
            // Se ainda houver chamadas na lista, busque a mais próxima (mesmo vazio)
            NodeChamadas chamada = chamadasExclusivas.getPrimeiro();
            if (chamada != null) {
                andarDestino = chamada.getAndarOrigem().getNumero();
                System.out.println("Redirecionando para chamada livre no andar: " + andarDestino);
                mover(tempoViagem);
            } else {
                System.out.println("Elevador sem destino e sem chamadas pendentes.");
            }
        }

        System.out.println("-----------------------------");
        System.out.println("Passageiros no elevador " + getId() + ":");
        getListaPassageiros().mostrarPassageiros();
        System.out.println("Gasto de energia: " + energiaGasta);
    }

    //Inicializa o elevador
    public void inicializar(){
        this.inicializado = true;
        System.out.println("Elevador:  " + id + "  Inicializado");
    }


    //Move o elevador entre os andares
    public void mover(int tempoViagem) {
        ajustarDirecaoSeNecessario();

        int energia = centralDeControle.isHorarioPico() ? 1 : 2;

        if (andarDestino > andarAtual.getValor().getNumero()) {
            subindo = true;
            if (andarAtual.getProximo() != null) {
                andarAtual = andarAtual.getProximo();
                System.out.println("Elevador subindo para o andar: " + andarAtual.getValor().getNumero() + " | Tempo de viagem: " + tempoViagem + " segundos");

                //Incrementa o tempo embarcado dos passageiros
                incrementarTempoEmbarcado(tempoViagem);

                //Incrementa a energia gasta pelo elevador
                energiaGasta += energia + embarcados;
            }
        } else if (andarDestino < andarAtual.getValor().getNumero()) {
            subindo = false;
            if (andarAtual.getAnterior() != null) {
                andarAtual = andarAtual.getAnterior();
                System.out.println("Elevador descendo para o andar: " + andarAtual.getValor().getNumero() + " | Tempo de viagem: " + tempoViagem + " segundos");
                incrementarTempoEmbarcado(tempoViagem);
                energiaGasta += energia + embarcados;
            }
        } else {
            System.out.println("Elevador sem andar de destino.");
        }
    }

    //Percorre a lista de passageiros e incrementa o tempo embarcado conforme o tempo da viagem do elevador
    private void incrementarTempoEmbarcado(int tempoViagem) {
        NodePassageiro atual = listaPassageiros.getInicio();
        while (atual != null) {
            System.out.println("Incrementando tempo de passageiro " + atual.getPassageiro().getId() + " em " + tempoViagem);
            atual.getPassageiro().incrementarTempoEmbarcado(tempoViagem);
            atual = atual.getProximo();
        }
    }

    //Encontra o destino mais proximo
    public int encontrarDestinoMaisProximo() {
        int destino = procurarDestinoPassageirosNaDirecao(subindo);

        // Se não há mais passageiros na direção, verifica chamadas externas
        if (destino == -1) {
            destino = procurarDestinoChamadasNaDirecao(subindo);
        }

        // Se não há chamdas na direção atual, inverte a direção
        if (destino == -1) {
            boolean descendo = !subindo;

            destino = procurarDestinoPassageirosNaDirecao(descendo);
            if (destino == -1) {
                destino = procurarDestinoChamadasNaDirecao(descendo);
            }
        }
        return destino;
    }

    //Verifica se o destino do passageiro está na direção
    public int procurarDestinoPassageirosNaDirecao(boolean direcao) {
        int melhorDestino = -1;
        NodePassageiro node = listaPassageiros.getInicio();

        while (node != null) {
            int destino = node.getPessoa().getAndarDestino();
            boolean naDirecao = direcao ? destino > andarAtual.getValor().getNumero() : destino < andarAtual.getValor().getNumero();

            if (naDirecao) {
                if (melhorDestino == -1 || (direcao && destino < melhorDestino) || (!direcao && destino > melhorDestino)) {
                    melhorDestino = destino;
                }
            }

            node = node.getProximo();
        }

        return melhorDestino;
    }

   //Procura um destino conforme a lista de chamadas
    private int procurarDestinoChamadasNaDirecao(boolean direcao) {
        int melhorDestino = -1;
        NodeChamadas node = chamadasExclusivas.getPrimeiro();

        while (node != null) {
            int andar = node.getAndarOrigem().getNumero();
            boolean naDirecao = direcao ? andar > andarAtual.getValor().getNumero() : andar < andarAtual.getValor().getNumero();

            if (naDirecao) {
                if (melhorDestino == -1 || (direcao && andar < melhorDestino) || (!direcao && andar > melhorDestino)) {
                    melhorDestino = andar;
                }
            }
            node = node.getProximo();
        }
        return melhorDestino;
    }

    //Metodo para embarcar pessoas
    public void embarcarPessoas(Elevador elevador, Andar andarAtual) {
        // Verifica se o andar atual está na lista de chamadas do elevador
        NodeChamadas nodeChamada = elevador.getListaChamadas().getPrimeiro();
        boolean chamadaEncontrada = false;

        while (nodeChamada != null) {
            if (nodeChamada.getAndarOrigem().getNumero() == andarAtual.getNumero()) {
                chamadaEncontrada = true;
                break;
            }
            nodeChamada = nodeChamada.getProximo();
        }

        if (!chamadaEncontrada) {
            return; // Se o andar atual não estiver na lista de chamadas, não embarca ninguém
        }

        FilaPessoas fila = andarAtual.getPessoasAguardando();

        while (fila.getInicio() != null && elevador.getEmbarcados() < elevador.getCapacidadeMaxima()) {
            Pessoa pessoa = fila.getInicio().getPessoa();

            Passageiro passageiro = new Passageiro(pessoa);  // converte para Passageiro no momento do embarque
            elevador.getListaPassageiros().adicionarNoFim(passageiro);

            fila.remover(pessoa);

            embarcados++;
            System.out.println("Passageiro " + passageiro.getId() + " embarcado com sucesso!");
        }
        chamadasExclusivas.removerChamada(andarAtual.getNumero());
        centralDeControle.getChamadasGlobais().removerChamada(andarAtual.getNumero());
    }

    //Desembarca os passageiros
    public void desembarcarPessoas(Andar andarAtual) {
        NodePassageiro atual = listaPassageiros.getInicio();
        int desembarcados = 0;

        if (atual == null) {
            return;
        }

        //Percorre a lista e verifica se o andar de destino é condizente com o andar atual
        while (atual != null) {
            NodePassageiro proximo = atual.getProximo();
            Passageiro passageiro = atual.getPassageiro();
            if (passageiro.getAndarDestino() == andarAtual.getNumero()) {
                passageirosDesembarcados.adicionarNoFim(passageiro);
                listaPassageiros.remover(atual);
                desembarcados++;
                embarcados--;
                System.out.println("Passageiro " + passageiro.getId() + " desembarcado com sucesso!");
            }
            atual = proximo;
        }
    }

    //Ajusta a direção se necessario
    private void ajustarDirecaoSeNecessario() {
        int destinoMaisProximo = encontrarDestinoMaisProximo();

        if (destinoMaisProximo == -1) {
            return;
        }
        if (destinoMaisProximo > andarAtual.getValor().getNumero()) {
            subindo = true;
        } else if (destinoMaisProximo < andarAtual.getValor().getNumero()) {
            subindo = false;
        }
    }


    //Verifica se o elevador está parado
    public boolean isParado(){
        boolean semPassageiros = this.getListaPassageiros().isEmpty();
        boolean semChamadas = this.getListaChamadas().isEmpty();
        return semPassageiros && semChamadas;
    }

    //Zera a energia gasta pelo elevador
    public void zerarEnergiaGasta() {
        energiaGasta = 0;
    }

    //Getters
    public boolean isInicializado() { return inicializado; }

    public boolean isSubindo() { return subindo; }

    public ListaPassageiros getPassageirosDesembarcados(){ return passageirosDesembarcados; }

    public int getEmbarcados() { return embarcados; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }

    public int getEnergiaGasta(){ return energiaGasta; }

    public int getId() { return id; }

    public ListaPassageiros getListaPassageiros() { return listaPassageiros; }

    public NodeAndar getAndarAtual() { return andarAtual; }

    public ListaChamadas getListaChamadas() { return this.chamadasExclusivas; }
}
