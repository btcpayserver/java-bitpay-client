package com.bitpay.sdk;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void setEnvironmentValidation() {
        final Config config = new Config();
        config.setEnvironment(Env.Test); // no exception
        config.setEnvironment(Env.Prod); // no exception
        assertThrows(RuntimeException.class, () -> config.setEnvironment(null));
        assertThrows(RuntimeException.class, () -> config.setEnvironment(""));
        assertThrows(RuntimeException.class, () -> config.setEnvironment("foo"));
    }

    @Test
    public void getEnvConfigIsParsedCorrectly() throws IOException, BitPayException {
        final URL configJsonUrl = getClass().getResource("/test_config.json");
        final String configJson = Resources.toString(configJsonUrl, StandardCharsets.UTF_8);
        final Config config = Config.parseFromJson(configJson);

        assertEquals("Test", config.getEnvironment());

        final Config.Environment testEnv = config.getEnvConfig().getTest();
        assertEquals("https://test.bitpay.com/", testEnv.getBaseUrl());
        assertEquals("", testEnv.getPrivateKeyPath());
        assertEquals("308201310201010420602280e12e7b4442dd36a62d23c2fdf4b0ce728ed41dfa3179ac02da0b0b4737a081e33081e0020101302c06072a8648ce3d0101022100fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f3044042000000000000000000000000000000000000000000000000000000000000000000420000000000000000000000000000000000000000000000000000000000000000704410479be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8022100fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141020101a124032200038f2f37f8fcffb65831f2a8ba4e9027951672ac62adc50b2f612fc6deebd64a8b", testEnv.getPrivateKey());
        assertEquals("JyW16qpmtyV7otNj9xknoAYonHX90vbsRxRxwl1vV6uP", testEnv.getTokens().getMerchant());
        assertEquals("GNpJ59XHAsVcHMZRNRGRlMizwx6UJn5rXycHk0B1rNsb", testEnv.getTokens().getPayroll());
        assertNull(testEnv.getTokens().getPointOfSale());

        final Config.Environment prodEnv = config.getEnvConfig().getProd();
        assertEquals("https://btcpayserver.org/", prodEnv.getBaseUrl());
        assertEquals("qux", prodEnv.getPrivateKeyPath());
        assertEquals("", prodEnv.getPrivateKey());
        assertEquals("bar", prodEnv.getTokens().getMerchant());
        assertEquals("baz", prodEnv.getTokens().getPayroll());
        assertEquals("foo", prodEnv.getTokens().getPointOfSale());
    }
}