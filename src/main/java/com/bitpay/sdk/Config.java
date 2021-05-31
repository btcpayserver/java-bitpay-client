package com.bitpay.sdk;

import com.bitpay.sdk.model.Facade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {
    private String _environment;
    private JsonNode _envConfig;

    /**
     * Only keeping the JsonNode to help avoid future merge conflicts.
     * Both should have the exact same data.
     */
    private EnvConfig envConfig;

    public Config() {
    }

    @JsonIgnore
    public String getEnvironment() {
        return _environment;
    }

    @JsonProperty("Environment")
    public void setEnvironment(String environment) {
        if (!Env.Test.equals(environment) && !Env.Prod.equals(environment)) {
            throw new RuntimeException("invalid environment: " + environment);
        }
        this._environment = environment;
    }

    @JsonIgnore
    public JsonNode getEnvConfig(String env) {
        return _envConfig.path(env);
    }

    @JsonIgnore
    public EnvConfig getEnvConfig() {
        return envConfig;
    }

    @JsonIgnore
    public Environment getEnv() {
        switch (getEnvironment()) {
            case Env.Prod:
                return getEnvConfig().getProd();
            case Env.Test:
                return getEnvConfig().getTest();
            default:
                throw new RuntimeException("not possible");
        }
    }

    @JsonProperty("EnvConfig")
    public void setEnvConfig(JsonNode envConfig) {
        this._envConfig = envConfig;
        this.envConfig = EnvConfig.parse(envConfig); // prevent merge conflicts in future
    }

    /**
     * Copied from com.bitpay.sdk.Client#GetConfig()
     *
     * @param json
     * @return
     * @throws BitPayException
     */
    public static Config parseFromJson(final String json) throws BitPayException {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(json);
            final JsonNode bitPayConfiguration = rootNode.path("BitPayConfiguration");
            return new ObjectMapper().readValue(bitPayConfiguration.toString(), Config.class);
        } catch (JsonProcessingException e) {
            throw new BitPayException("failed to read configuration file : " + e.getMessage());
        }
    }

    public static class EnvConfig {
        private Environment test;
        private Environment prod;

        @JsonProperty(Env.Test)
        public Environment getTest() {
            return test;
        }

        public void setTest(final Environment test) {
            this.test = test;
        }

        @JsonProperty(Env.Prod)
        public Environment getProd() {
            return prod;
        }

        public void setProd(final Environment prod) {
            this.prod = prod;
        }

        public static EnvConfig parse(final JsonNode envConfig) {
            try {
                return new ObjectMapper().readValue(envConfig.toString(), EnvConfig.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("invalid config file", e);
            }
        }
    }

    public static class Environment {
        private String baseUrl;
        private String privateKeyPath;
        private String privateKey;
        private Tokens tokens;

        @JsonProperty("baseUrl")
        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(final String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @JsonProperty("PrivateKeyPath")
        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(final String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        @JsonProperty("PrivateKey")
        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(final String privateKey) {
            this.privateKey = privateKey;
        }

        @JsonProperty("ApiTokens")
        public Tokens getTokens() {
            return tokens;
        }

        public void setTokens(final Tokens tokens) {
            this.tokens = tokens;
        }
    }

    public static class Tokens {
        private String pointOfSale;
        private String merchant;
        private String payroll;

        @JsonProperty(Facade.PointOfSale)
        public String getPointOfSale() {
            return pointOfSale;
        }

        public void setPointOfSale(final String pointOfSale) {
            this.pointOfSale = pointOfSale;
        }

        @JsonProperty(Facade.Merchant)
        public String getMerchant() {
            return merchant;
        }

        public void setMerchant(final String merchant) {
            this.merchant = merchant;
        }

        @JsonProperty(Facade.Payroll)
        public String getPayroll() {
            return payroll;
        }

        public void setPayroll(final String payroll) {
            this.payroll = payroll;
        }
    }
}
