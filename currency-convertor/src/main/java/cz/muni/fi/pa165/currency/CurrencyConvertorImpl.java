package cz.muni.fi.pa165.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;


/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        logger.trace("Convert from {} to {} the amount {}.", sourceCurrency, targetCurrency, sourceAmount);
        if (sourceCurrency == null) {
            throw new IllegalArgumentException("sourceCurrency is null");
        }
        if (targetCurrency == null) {
            throw new IllegalArgumentException("targetCurrency is null");
        }
        if (sourceAmount == null) {
            throw new IllegalArgumentException("sourceAmount is null");
        }

        BigDecimal rate = null;
        try {
            rate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
        } catch(ExternalServiceFailureException e) {
            logger.error("ExternalServiceFailure when looking up exchange rate from {} to {}.", sourceCurrency, targetCurrency);
            throw new UnknownExchangeRateException("Exchange rate lookup failed due to external failure", e);
        }

        if (rate == null) {
            logger.warn("Missing exchange rate from {} to {}.", sourceCurrency, targetCurrency);
            throw new UnknownExchangeRateException("Exchange rate is not knows");
        }

        return sourceAmount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

}
