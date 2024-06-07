package ui;

import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import infraestrutura.RedeLocal;

import java.util.List;

public class TesteMain {
    public static void main(String[] args) {
        double download = RedeLocal.buscarVelocidadeRede();
        System.out.println(download);
    }
}
