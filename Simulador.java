import java.io.IOException;

public class Simulador {
    private int tempoSimulado = 0;
    private int ultimaHoraGerada = -1;
    private Predio predio;
    private CentralDeControle centralDeControle;

    public Simulador(int totalAndares, int totalElevadores) {
        this.predio = new Predio(totalAndares, totalElevadores);
        this.centralDeControle = predio.getCentralDeControle();
    }

    public void iniciar() {
        System.out.println("Iniciando simulação...\n");

        //Simula o comportamente do predio em um dia inteiro
        for (tempoSimulado = 0; tempoSimulado < 1440; tempoSimulado++) {
            int horaAtual = tempoSimulado / 60;

            //Gera novas pessoas a cada hora
            if (horaAtual != ultimaHoraGerada) {
                predio.gerarPessoasPorHora(horaAtual);
                ultimaHoraGerada = horaAtual;
            }

            System.out.println("\n--- Tempo simulado " + (tempoSimulado + 1) + " ---");

            //Atualiza o funcionamento de todo o predio a cada iteração
            predio.atualizar(tempoSimulado);

            //Tempo entre cada iteração
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Erro ao pausar o ciclo.");
            }
        }

        System.out.println("\nSimulação finalizada.");

        //Gera o arquivo XLS ao fim da simulação completa
        try {
            predio.getEstatisticas().exportarParaXLS("estatisticas.xls");
        } catch (IOException e) {
            System.out.println("Erro ao exportar arquivo XLS: " + e.getMessage());
        }
    }
}