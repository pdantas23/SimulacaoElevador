public abstract class EntidadeSimulavel implements Simulavel {
    private boolean ativo = true;

    public void simular() {
        if (ativo) {
            atualizar(0, 0);
        }
    }
    public void atualizar(int minutoSimuladom, int tempoViagem){
    }
}