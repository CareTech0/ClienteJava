package infraestrutura;

import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;

import java.util.ArrayList;
import java.util.List;

public class DiscoRigido {

    private List<Double> totalDeEspacoNosDiscos;
    private List<Double> disponivel;

    public DiscoRigido(){
        totalDeEspacoNosDiscos = new ArrayList<>();
        disponivel = new ArrayList<>();
    }

    public List<Double> buscarTotalDeEspaco(){
        DiscoGrupo grupoDeDiscos = new DiscoGrupo();
        List<Volume> listaDeVolumes = grupoDeDiscos.getVolumes();
        for(Volume volume : listaDeVolumes){
            totalDeEspacoNosDiscos.add(volume.getTotal()/(Math.pow(1024.0, 3)));
        }
        return totalDeEspacoNosDiscos;
    }

    public List<Double> buscarEspacoLivre(){
        DiscoGrupo grupoDeDiscos = new DiscoGrupo();
        List<Volume> listaDeVolumes = grupoDeDiscos.getVolumes();
        for(Volume volume : listaDeVolumes){
            disponivel.add(volume.getDisponivel()/(Math.pow(1024.0, 3)));
        }
        return disponivel;
    }
}
