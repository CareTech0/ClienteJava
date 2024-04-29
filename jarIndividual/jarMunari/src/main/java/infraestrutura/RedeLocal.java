package infraestrutura;

import com.github.britooo.looca.api.core.Looca;

public class RedeLocal {

    private Double  valocidadeRede;


    public RedeLocal(){

    }

    public void buscarVelocidadeRede(){
        Looca looca = new Looca();
        System.out.println(looca.getRede().getGrupoDeInterfaces());
        System.out.println(looca.getRede().getParametros());
    }
}
