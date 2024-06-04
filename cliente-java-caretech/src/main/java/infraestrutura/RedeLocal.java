package infraestrutura;

import com.github.britooo.looca.api.core.Looca;

public class RedeLocal {
    private Looca looca = new Looca();
    private Double  valocidadeRede;
    private Long download = looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getBytesRecebidos();


    public RedeLocal(){

    }

    public void buscarVelocidadeRede(){
        System.out.println("Taxa da download: " + download);
    }

    public Long getDownload() {
        return download;
    }
}
