package yjh.ontongsal.common.db.jpa;

import com.zaxxer.hikari.HikariDataSource;

public class CommonDataSourceProperties {

    private HikariProperties write;
    private HikariProperties read;

    public HikariProperties getWrite() {
        return write;
    }

    public HikariProperties getRead() {
        return read;
    }

    public void setWrite(HikariProperties write) {
        this.write = write;
    }

    public void setRead(HikariProperties read) {
        this.read = read;
    }

    public static class HikariProperties {
        private String jdbcUrl;
        private String username;
        private String password;
        private int maximumPoolSize = 30;
        private long connectionTimeout = 30000;
        private long keepaliveTime = 0;

        public HikariDataSource toDataSource() {
            var datasource = new HikariDataSource();
            datasource.setJdbcUrl(jdbcUrl);
            datasource.setUsername(username);
            datasource.setPassword(password);
            datasource.setMaximumPoolSize(maximumPoolSize);
            datasource.setConnectionTimeout(connectionTimeout);
            datasource.setKeepaliveTime(keepaliveTime);
            return datasource;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public long getKeepaliveTime() {
            return keepaliveTime;
        }

        public void setKeepaliveTime(long keepaliveTime) {
            this.keepaliveTime = keepaliveTime;
        }
    }
}
