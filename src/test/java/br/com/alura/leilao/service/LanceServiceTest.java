package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LanceDao;
import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.dao.UsuarioDao;
import br.com.alura.leilao.dto.NovoLanceDto;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanceServiceTest {

    @Mock
    private LanceDao lances;
    @Mock
    private UsuarioDao usuarios;
    @Mock
    private LeilaoDao leiloes;

    LanceService service;

    @BeforeEach
    void Initialize() {
        MockitoAnnotations.initMocks(this);
        this.service = new LanceService(lances, usuarios, leiloes);

        setMocks();
    }

    @Test
    void DeveriaSalvarOLance() {
        NovoLanceDto lanceDto = novoLance();
        Boolean value = this.service.propoeLance(lanceDto, "Gabriel Duarte");

        assertTrue(value);
        verify(lances).salvar(any());
    }

    @Test
    void DeveriaRetornarFalseENaoSalvarOlance() {
        NovoLanceDto lanceDto = novoLance();
        lanceDto.setValor(new BigDecimal(20));
        boolean value = this.service.propoeLance(lanceDto, "Gabriel Duarte");

        assertFalse(value);
        verifyNoInteractions(lances);
    }

    void setMocks() {
        Usuario usuario = new Usuario("Gabriel Duarte", "gabriel@email.com", "123");
        Leilao leilao = new Leilao("Carro Leilao", new BigDecimal(200), LocalDate.now(), usuario);

        when(usuarios.buscarPorUsername(any())).thenReturn(usuario);

        when(leiloes.buscarPorId(any())).thenReturn(leiloes());
    }

    private Leilao leiloes() {
        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(lance);

        return leilao;
    }

    private NovoLanceDto novoLance() {
        NovoLanceDto lanceDto = new NovoLanceDto();
        lanceDto.setValor(new BigDecimal(1000));
        lanceDto.setLeilaoId(1L);

        return lanceDto;
    }
}