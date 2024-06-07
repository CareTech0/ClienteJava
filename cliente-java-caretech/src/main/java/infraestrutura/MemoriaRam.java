package infraestrutura;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.processos.ProcessoGrupo;

import java.util.List;

public class MemoriaRam {
    private Double usoRam;
    private Double totalRam;
    private Integer qtdProcessos;
    private List<Janela> processos;

    public MemoriaRam() {

    }

    public Double buscarUsoDeRam() {
        Looca looca = new Looca();
        usoRam = looca.getMemoria().getEmUso() / (Math.pow(1024.0, 3));
        return usoRam;
    }

    public Double buscarTotalDeRam() {
        Looca looca = new Looca();
        totalRam = looca.getMemoria().getTotal() / (Math.pow(1024.0, 3));
        return totalRam;
    }

    public Integer buscarQtdProcessos() {
        Looca looca = new Looca();
        ProcessoGrupo grupoDeProcessos = looca.getGrupoDeProcessos();
        qtdProcessos = grupoDeProcessos.getTotalProcessos();
        return qtdProcessos;
    }

    public List<Janela> buscarProcessos(){
        Looca looca = new Looca();
        processos = looca.getGrupoDeJanelas().getJanelas();
        return processos;
    }
}
