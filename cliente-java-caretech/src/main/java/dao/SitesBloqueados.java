package dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.Conexao;
import repository.ConexaoSqlServer;

import java.util.List;

public class SitesBloqueados {
    private Integer idSites;
    private String nome;
    private String url;
    private Integer fkEmpresa;
    
    public SitesBloqueados(){

    }

    public List<SitesBloqueados> getSitesBloqueados(){

        JdbcTemplate con = ConexaoSqlServer.conexaoSqlServer;

        List<SitesBloqueados> sitesBloqueados = con.query(
                "SELECT * FROM sites_bloqueados WHERE fk_empresa = %d".formatted(fkEmpresa),
                new BeanPropertyRowMapper<>(SitesBloqueados.class)
        );

        return sitesBloqueados;
    }

    public Integer getIdSites() {
        return idSites;
    }

    public void setIdSites(Integer idSites) {
        this.idSites = idSites;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    public void setFkEmpresa(Integer fkEmpresa) {
        this.fkEmpresa = fkEmpresa;
    }
}
