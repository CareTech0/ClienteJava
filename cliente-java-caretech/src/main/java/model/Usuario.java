package model;

public class Usuario {
    private Integer id_responsavel;
    private String nome;
    private String telefone;
    private String email;
    private String permissao;
    private Integer fk_empresa;

    public Usuario(){

    }

    public Usuario(Integer id_responsavel, String nome, String telefone, String email, String permissao, Integer fk_empresa) {
        this.id_responsavel = id_responsavel;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.permissao = permissao;
        this.fk_empresa = fk_empresa;
    }

    public Integer getId_responsavel() {
        return id_responsavel;
    }

    public void setId_responsavel(Integer id_responsavel) {
        this.id_responsavel = id_responsavel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }

    public Integer getFk_empresa() {
        return fk_empresa;
    }

    public void setFk_empresa(Integer fk_empresa) {
        this.fk_empresa = fk_empresa;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id_responsavel=" + id_responsavel +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", permissao='" + permissao + '\'' +
                ", fk_empresa=" + fk_empresa +
                '}';
    }
}
