package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;
    private List<Leilao> leiloes = leiloes();;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    void Initialize() {
        MockitoAnnotations.initMocks(this);
        when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    void DeveriaFinalizarUmLeilao() {
        Leilao leilao = leiloes.get(0);
        service.finalizarLeiloesExpirados();

        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());

        verify(leilaoDao).salvar(leilao);
    }

    @Test
    void DeveriaEnviarEmailParaOMaiorLance() {
        Leilao leilao = leiloes.get(0);
        service.finalizarLeiloesExpirados();

        verify(enviadorDeEmails)
                .enviarEmailVencedorLeilao(leilao.getLanceVencedor());
    }

    @Test
    void NaoDeveriaEnviarEmailParaOMaiorLanceEmCasoDeErrorAoEncerrarLeilao() {
        when(leilaoDao.salvar(any())).thenThrow(RuntimeException.class);

        try {
            service.finalizarLeiloesExpirados();
            verifyNoInteractions(enviadorDeEmails);
        } catch (Exception e) {}
    }

    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;

    }
}