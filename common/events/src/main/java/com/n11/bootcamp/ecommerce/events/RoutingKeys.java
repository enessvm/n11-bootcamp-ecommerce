package com.n11.bootcamp.ecommerce.events;

/**
 * Routing keys and exchange names for all events on the platform.
 */
public final class RoutingKeys {

    private RoutingKeys() {}

    // ---- Exchanges ----
    public static final String EXCHANGE_PRODUCT_EVENTS = "product.events";
    public static final String EXCHANGE_STOCK_EVENTS   = "stock.events";
    public static final String EXCHANGE_STOCK_COMMANDS = "stock.commands";
    public static final String EXCHANGE_ORDER_EVENTS   = "order.events";



    // ---- product.events ----
    public static final String PRODUCT_CREATED = "product.created";



    // ---- stock.commands ----
    public static final String STOCK_RESERVE = "stock.commands.reserve";

    // ---- stock.events ----
    public static final String STOCK_RESERVED            = "stock.reserved";
    public static final String STOCK_RESERVATION_FAILED  = "stock.reservation-failed";
    public static final String STOCK_COMMITTED           = "stock.committed";
    public static final String STOCK_RELEASED            = "stock.released";
}