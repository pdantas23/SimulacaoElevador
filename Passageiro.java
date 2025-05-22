public class Passageiro extends Pessoa {
    private double tempoEsperando;
    private double tempoEmbarcado;

    public Passageiro(Pessoa pessoa) {
        super(pessoa.getId(), pessoa.getAndarOrigem(), pessoa.getAndarDestino(), pessoa.getPrioridade());
        this.tempoEsperando = pessoa.getTempoEsperando();
        this.tempoEmbarcado = 0;
    }

    //Exibe as informações do passageiro
    @Override
    public String toString() {
        return "Passageiro ID: " + getId() +
                ", Origem: " + getAndarOrigem() +
                ", Destino: " + getAndarDestino() +
                ", Prioridade: " + getPrioridade();
    }

    //Incrementa o tempo embarcado do passageiro conforme o tempo de viagem
    public void incrementarTempoEmbarcado(int tempoViagem) {
        this.tempoEmbarcado += tempoViagem;
    }

    //Getters
    public double getTempoEsperando() {
        return tempoEsperando;
    }

    @Override
    public double getTempoEmbarcado() {return tempoEmbarcado;}
}
