package model;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;

import java.util.ArrayList;
import java.util.List;

public class Computador {
    private Integer id_Computador;
    private String estacao_de_trabalho;
    private String login;
    private String senha;
    private Integer fk_empresa;
    private ArrayList<DiscoModel> listaDiscos = new ArrayList<DiscoModel>();

    public Computador(Integer id_Computador, String estacao_de_trabalho, String login, String senha, Integer fk_empresa, ArrayList<DiscoModel> listaDiscos) {
        this.id_Computador = id_Computador;
        this.estacao_de_trabalho = estacao_de_trabalho;
        this.login = login;
        this.senha = senha;
        this.fk_empresa = fk_empresa;
        this.listaDiscos = listaDiscos;
    }

    public Computador() {

    }

    public void adicionarDisco(DiscoModel discoModel){
        listaDiscos.add(discoModel);
    }



    public List<Computador> autenticadorComputador(String login, String senha){
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        List<Computador> computadores = con.query(
            "SELECT * FROM computador WHERE login = '%s' AND senha = '%s'".formatted(login, senha),
            new BeanPropertyRowMapper<>(Computador.class)
        );

        System.out.println("Inserindo...");

        return computadores;
    }

    public Integer getId_Computador() {
        return id_Computador;
    }

    public void setId_Computador(Integer id_Computador) {
        this.id_Computador = id_Computador;
    }

    public String getEstacao_de_trabalho() {
        return estacao_de_trabalho;
    }

    public void setEstacao_de_trabalho(String estacao_de_trabalho) {
        this.estacao_de_trabalho = estacao_de_trabalho;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getFk_empresa() {
        return fk_empresa;
    }

    public void setFk_empresa(Integer fk_empresa) {
        this.fk_empresa = fk_empresa;
    }

    public List<DiscoModel> getListaDiscos() {
        return listaDiscos;
    }

    public void setListaDiscos(ArrayList<DiscoModel> listaDiscos) {
        this.listaDiscos = listaDiscos;
    }

    @Override
    public String toString() {
        return "Computador{" +
                "id_Computador=" + id_Computador +
                ", estacao_de_trabalho='" + estacao_de_trabalho + '\'' +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", fk_empresa=" + fk_empresa +
                '}';
    }
}
