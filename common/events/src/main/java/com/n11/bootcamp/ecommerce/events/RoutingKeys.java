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
    public static final String EXCHANGE_PROMOTION_COMMANDS  = "promotion.commands";
    public static final String EXCHANGE_PROMOTION_EVENTS    = "promotion.events";
    public static final String EXCHANGE_PAYMENT_COMMANDS    = "payment.commands";
    public static final String EXCHANGE_PAYMENT_EVENTS      = "payment.events";



    // ---- product.events ----
    public static final String PRODUCT_CREATED = "product.created";



    // ---- stock.commands ----
    public static final String STOCK_RESERVE = "stock.commands.reserve";
    public static final String STOCK_COMMIT  = "stock.commands.commit";
    public static final String STOCK_RELEASE = "stock.commands.release";

    // ---- stock.events ----
    public static final String STOCK_RESERVED            = "stock.reserved";
    public static final String STOCK_RESERVATION_FAILED  = "stock.reservation-failed";
    public static final String STOCK_COMMITTED           = "stock.committed";
    public static final String STOCK_COMMIT_FAILED       = "stock.commit-failed";
    public static final String STOCK_RELEASED            = "stock.released";



    // ---- promotion.commands ----
    public static final String PROMOTION_APPLY  = "promotion.commands.apply";
    public static final String PROMOTION_REVERT = "promotion.commands.revert";

    // ---- promotion.events ----
    public static final String PROMOTION_APPLIED  = "promotion.applied";
    public static final String PROMOTION_FAILED   = "promotion.failed";
    public static final String PROMOTION_REVERTED = "promotion.reverted";



    // ---- payment.commands ----
    public static final String PAYMENT_CHARGE = "payment.commands.charge";

    // ---- payment.events ----
    public static final String PAYMENT_INITIATED = "payment.initiated";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED    = "payment.failed";
}