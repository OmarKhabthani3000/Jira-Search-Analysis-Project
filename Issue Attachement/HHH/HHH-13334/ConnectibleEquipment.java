package com.tekview.transview.sensornetwork.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.MappedSuperclass;

/**
 * User: Tiger
 * Date: 2017-11-10
 */
@MappedSuperclass
public class ConnectibleEquipment<T> extends BaseEquipment<T> implements Connectible {
    @Column(nullable = false, columnDefinition = "TINYINT")
    private ConnectionState connectionState;
    private String ip;
    private int port;
    private String user;
    private String password;

    @Override
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
