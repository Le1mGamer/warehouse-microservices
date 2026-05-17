package com.warehouse.supply.model.enums;

public enum OrderStatus {
    CREATED("Створено"),
    CONFIRMED("Підтверджено"),
    SHIPPED("Відправлено"),
    DELIVERED("Доставлено"),
    CANCELLED("Скасовано");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
