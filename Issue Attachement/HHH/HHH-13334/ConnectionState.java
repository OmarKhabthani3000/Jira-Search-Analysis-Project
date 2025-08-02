package com.tekview.transview.sensornetwork.model;

/**
 * User: Tiger
 * Date: 2017-10-26
 */
public enum ConnectionState {
    Offline((short) 0), Online((short) 1), OfflineByMaintenance((short) -1);

    private final short value;

    ConnectionState(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public static ConnectionState valueOf(short value) {
        for (ConnectionState connectionState : ConnectionState.values()) {
            if (connectionState.value == value) return connectionState;
        }
        throw new EnumConstantNotPresentException(ConnectionState.class, String.valueOf(value));
    }
}
