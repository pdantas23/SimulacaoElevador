import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class Estatisticas {
    private ListaRegistroHora lista;

    public Estatisticas() {
        lista = new ListaRegistroHora();
    }

    //Atualiza as estatisticas a cada hora
    public void atualizarEstatisticas(int minuto, int pessoasAtendidas, int tempoEspera, int tempoViagem, int energia, int pessoasAguardando) {
        int hora = minuto / 60;
        lista.adicionarOuAtualizar(hora, pessoasAtendidas, tempoEspera, tempoViagem, energia, pessoasAguardando);
    }

    //Exporta as estatisticas para um arquivo XLS
    public void exportarParaXLS(String caminho) throws IOException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estatísticas");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Hora");
        header.createCell(1).setCellValue("Pessoas Atendidas: ");
        header.createCell(2).setCellValue("Tempo total de espera (s): ");
        header.createCell(3).setCellValue("Tempo total de viagem (s): ");
        header.createCell(4).setCellValue("Energia total gasta: ");
        header.createCell(5).setCellValue("Pessoas geradas: ");

        int linha = 1;
        NodeRegistroHora atual = lista.getInicio();
        while (atual != null) {
            RegistroHora r = atual.getDado();
            Row row = sheet.createRow(linha++);

            row.createCell(0).setCellValue(r.getHora());
            row.createCell(1).setCellValue(r.getTotalPessoasAtendidas());
            row.createCell(2).setCellValue( r.getTotalTempoEspera());
            row.createCell(3).setCellValue( r.getTotalTempoViagem());
            row.createCell(4).setCellValue(r.getTotalEnergia());
            row.createCell(5).setCellValue((double) r.getTotalPessoasAguardando() /3);

            atual = atual.getProximo();
        }
        FileOutputStream out = new FileOutputStream(caminho);
        workbook.write(out);
        out.close();
        workbook.close();
    }
}
