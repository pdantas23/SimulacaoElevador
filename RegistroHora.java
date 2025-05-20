public class RegistroHora {
    private int hora;
    private int totalPessoasAtendidas;
    private int totalTempoEspera;
    private int totalTempoViagem;
    private int totalEnergia;
    private int totalPessoasAguardando;

    public RegistroHora(int hora) {
        this.hora = hora;
    }

    //Faz o registro das estatisticas
    public void adicionar(int pessoasAtendidas, int tempoEspera, int tempoViagem, int energia, int pessoasAguardando) {
        this.totalPessoasAtendidas += pessoasAtendidas;
        this.totalTempoEspera += tempoEspera;
        this.totalTempoViagem += tempoViagem;
        this.totalEnergia += energia;
        this.totalPessoasAguardando += pessoasAguardando;
    }

    //Getters
    public int getHora() { return hora; }

    public int getTotalPessoasAtendidas() { return totalPessoasAtendidas; }

    public int getTotalTempoEspera() { return totalTempoEspera; }

    public int getTotalTempoViagem() { return totalTempoViagem; }

    public int getTotalEnergia() { return totalEnergia; }

    public int getTotalPessoasAguardando() { return totalPessoasAguardando; }
}