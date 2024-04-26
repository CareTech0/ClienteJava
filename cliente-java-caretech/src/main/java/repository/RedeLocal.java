package repository;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.Rede;

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
