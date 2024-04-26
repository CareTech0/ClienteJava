package repository;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.temperatura.Temperatura;

public class Cpu {

    private Double usoCpu;

    public Cpu(){

    }

    public Double buscarUsoCpu(){
        Looca looca = new Looca();
        Processador processador = looca.getProcessador();
        usoCpu = processador.getUso();
        return usoCpu;
    }

    public Double buscarTemperatura() {
        Looca looca = new Looca();
        Temperatura processador = looca.getTemperatura();
        usoCpu = processador.getTemperatura();
        return usoCpu;
    }
}
