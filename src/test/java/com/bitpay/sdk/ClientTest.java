package com.bitpay.sdk;

import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ClientTest {

    @Test
    public void testConstructor() {
        // null config
        assertThrows(NullPointerException.class, () -> new Client((Config) null, null));

        // null environment
        assertThrows(NullPointerException.class, () -> new Client(new Config(), null));
    }

    @Test
    public void testBaseUrl() throws IOException, BitPayException {
        final URL configJsonUrl = getClass().getResource("/test_config.json");
        final String configJson = Resources.toString(configJsonUrl, StandardCharsets.UTF_8);
        final Config config = Config.parseFromJson(configJson);

        final Client client = new Client(config, null);

        assertEquals("https://test.bitpay.com/", client.getBaseUrl());
    }
}
