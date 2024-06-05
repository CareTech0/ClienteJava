package model;

import java.util.List;

public abstract class Hardware {
    private Integer id_hardware;
    private String nome_hardware;
    private Double capacidade_total;

    private Integer fk_computador;

    public Hardware(Integer id_hardware, String nome_hardware, Double capacidade_total, Double min, Double max, Integer fk_computador) {
        this.id_hardware = id_hardware;
        this.nome_hardware = nome_hardware;
        this.capacidade_total = capacidade_total;
        this.fk_computador = fk_computador;
    }

    public Hardware(){

    }

    abstract public <T> T autenticarHardware(Integer fk_computador, String banco);

    abstract public void inserirHardware(Integer fkComputador, Double capacidadeTotal, String banco);

    public Integer getId_hardware() {
        return id_hardware;
    }

    public void setId_hardware(Integer id_hardware) {
        this.id_hardware = id_hardware;
    }

    public String getNome_hardware() {
        return nome_hardware;
    }

    public void setNome_hardware(String nome_hardware) {
        this.nome_hardware = nome_hardware;
    }

    public Double getCapacidade_total() {
        return capacidade_total;
    }

    public void setCapacidade_total(Double capacidade_total) {
        this.capacidade_total = capacidade_total;
    }

    public Integer getFk_computador() {
        return fk_computador;
    }

    public void setFk_computador(Integer fk_computador) {
        this.fk_computador = fk_computador;
    }



    @Override
    public String toString() {
        return "Hardware{" +
                "id_hardware=" + id_hardware +
                ", nome_hardware='" + nome_hardware + '\'' +
                ", capacidade_total=" + capacidade_total +
                ", fk_computador=" + fk_computador +
                '}';
    }
}
