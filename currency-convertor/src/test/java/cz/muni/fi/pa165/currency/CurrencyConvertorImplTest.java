package cz.muni.fi.pa165.currency;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyConvertorImplTest {

    @Mock
    private ExchangeRateTable rateTable;

    private CurrencyConvertor converter;

    private Currency eur = Currency.getInstance("EUR");
    private Currency czk = Currency.getInstance("EUR");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        converter = new CurrencyConvertorImpl(rateTable);
    }

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(rateTable.getExchangeRate(eur, czk)).thenReturn(new BigDecimal("10"));

        // general cases
        assertThat(converter.convert(eur, czk, new BigDecimal("3.0124"))).isEqualTo(new BigDecimal("30.12"));
        assertThat(converter.convert(eur, czk, new BigDecimal("3.0126"))).isEqualTo(new BigDecimal("30.13"));

        // rounding corner cases
        assertThat(converter.convert(eur, czk, new BigDecimal("5.1135"))).isEqualTo(new BigDecimal("51.14"));
        assertThat(converter.convert(eur, czk, new BigDecimal("5.1145"))).isEqualTo(new BigDecimal("51.14"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullSourceCurrency() {
        converter.convert(null, czk, new BigDecimal("1000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullTargetCurrency() {
        converter.convert(eur, null, new BigDecimal("1000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullSourceAmount() {
        converter.convert(eur, czk, null);
    }

    @Test(expected = UnknownExchangeRateException.class)
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(rateTable.getExchangeRate(eur, czk)).thenReturn(null);
        converter.convert(eur, czk, new BigDecimal("200"));
    }

    @Test(expected = UnknownExchangeRateException.class)
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(rateTable.getExchangeRate(eur, czk)).thenThrow(ExternalServiceFailureException.class);
        converter.convert(eur, czk, new BigDecimal("200"));
    }

}
