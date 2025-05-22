import java.io.IOException;

public class Simulador {
    private int tempoSimulado = 0;
    private Predio predio;
    private CentralDeControle centralDeControle;

    public Simulador(int totalAndares, int totalElevadores) {
        this.predio = new Predio(totalAndares, totalElevadores);
        this.centralDeControle = predio.getCentralDeControle();
    }

    public void iniciar() {
        System.out.println("Iniciando simulação...\n");

        while (tempoSimulado < 1440) {
            int horaSimulada = tempoSimulado / 60;

            // Gera pessoas apenas uma vez por hora
            if (tempoSimulado % 60 == 0) {
                predio.gerarPessoasPorHora(horaSimulada);
            }

            //Verifica horário de pico
            centralDeControle.verificarHorarioPico(tempoSimulado);

            //Atualiza a velocidade do elevador
            int tempoViagemElevador = centralDeControle.getTempoParaLocomoverElevador();

            //Numero de iteracoes por minuto com base na velocidade do elevador
            int iteracoesPorMinuto = 60 / tempoViagemElevador;

            for (int i = 0; i < iteracoesPorMinuto; i++) {
                predio.atualizar(tempoSimulado, tempoViagemElevador);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Só aqui avança o tempo em MINUTOS
            tempoSimulado++;
        }

        System.out.println("\nSimulação finalizada.");

        try {
            predio.getEstatisticas().exportarParaXLS("estatisticas.xls");
        } catch (IOException e) {
            System.out.println("Erro ao exportar arquivo XLS: " + e.getMessage());
        }
    }
}