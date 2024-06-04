package infraestrutura;

import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;

import java.util.ArrayList;
import java.util.List;

public class DiscoRigido {

    private List<Double> totalDeEspacoNosDiscos;
    private List<Double> disponivel;
    private List<Double> ocupado;

    public DiscoRigido(){
        totalDeEspacoNosDiscos = new ArrayList<>();
        disponivel = new ArrayList<>();
        ocupado = new ArrayList<>();
    }

    public List<Double> buscarTotalDeEspaco(){
        if(totalDeEspacoNosDiscos.size() == 0){
            DiscoGrupo grupoDeDiscos = new DiscoGrupo();
            List<Volume> listaDeVolumes = grupoDeDiscos.getVolumes();
            for(Volume volume : listaDeVolumes){
                totalDeEspacoNosDiscos.add(volume.getTotal()/(Math.pow(1024.0, 3)));
            }
        }
        return totalDeEspacoNosDiscos;
    }

    public List<Double> buscarEspacoLivre(){
        disponivel.clear();
        DiscoGrupo grupoDeDiscos = new DiscoGrupo();
        List<Volume> listaDeVolumes = grupoDeDiscos.getVolumes();
        for(Volume volume : listaDeVolumes){
            disponivel.add(volume.getDisponivel()/(Math.pow(1024.0, 3)));
        }
        return disponivel;
    }

    public List<Double> buscarEspacoOcupado(){
        ocupado.clear();
        for (int i = 0; i < totalDeEspacoNosDiscos.size(); i++){
            ocupado.add(totalDeEspacoNosDiscos.get(i) - disponivel.get(i));
        }
        return ocupado;
    }

}
