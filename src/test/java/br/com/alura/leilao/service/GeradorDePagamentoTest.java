package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeradorDePagamentoTest {

    private GeradorDePagamento service;
    @Mock
    private PagamentoDao pagamentoDao;
    @Mock
    private Clock clock;
    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    void Initialize() {
        MockitoAnnotations.initMocks(this);

        this.service = new GeradorDePagamento(pagamentoDao, clock);
    }

    @Test
    void DeveriaCriarPagamentoParaVencedorDoLeilao() {
        LocalDate data = LocalDate.of(2023, 7, 10);
        Leilao leilao = leiloes();
        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(
                data.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );

        service.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        assertFalse(pagamento.getPago());

        assertEquals(data.plusDays(1), pagamento.getVencimento());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(lanceVencedor.getLeilao(), pagamento.getLeilao());
    }

    @Test
    void DeveriaCriarPagamentoComVencimentoParaSegundaFeiraEstandoNoSabado() {
        LocalDate data = LocalDate.of(2023, 7, 8);
        Leilao leilao = leiloes();
        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(
                data.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        service.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        assertEquals(data.plusDays(2), pagamento.getVencimento());
    }

    @Test
    void DeveriaCriarPagamentoComVencimentoParaSegundaFeiraEstandoNaSexta() {
        LocalDate data = LocalDate.of(2023, 7, 7);
        Leilao leilao = leiloes();
        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        Mockito.when(clock.instant()).thenReturn(
                data.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        service.gerarPagamento(lanceVencedor);

        Mockito.verify(pagamentoDao).salvar(captor.capture());
        Pagamento pagamento = captor.getValue();

        assertEquals(data.plusDays(3), pagamento.getVencimento());
    }

    private Leilao leiloes() {
        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(segundo);
        leilao.setLanceVencedor(segundo);

        return leilao;
    }
}