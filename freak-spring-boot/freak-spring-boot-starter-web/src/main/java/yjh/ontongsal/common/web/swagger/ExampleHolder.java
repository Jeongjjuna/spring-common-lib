package yjh.ontongsal.common.web.swagger;

import io.swagger.v3.oas.models.examples.Example;

public record ExampleHolder(Example holder, String name, int code) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Example holder;
        private String name;
        private int code;

        public Builder holder(Example holder) {
            this.holder = holder;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public ExampleHolder build() {
            return new ExampleHolder(holder, name, code);
        }
    }
}
