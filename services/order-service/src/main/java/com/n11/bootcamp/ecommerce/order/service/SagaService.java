package com.n11.bootcamp.ecommerce.order.service;

import com.n11.bootcamp.ecommerce.events.promotion.PromotionFailed;
import com.n11.bootcamp.ecommerce.events.promotion.PromotionApplied;
import com.n11.bootcamp.ecommerce.events.stock.StockCommitFailed;
import com.n11.bootcamp.ecommerce.events.stock.StockCommitted;
import com.n11.bootcamp.ecommerce.events.stock.StockReleased;
import com.n11.bootcamp.ecommerce.events.stock.StockReservationFailed;
import com.n11.bootcamp.ecommerce.events.stock.StockReserved;
import com.n11.bootcamp.ecommerce.order.entity.Order;

public interface SagaService {

    void start(Order order);

    void onStockReserved(StockReserved event);

    void onStockReservationFailed(StockReservationFailed event);

    void onPromotionApplied(PromotionApplied event);

    void onPromotionApplicationFailed(PromotionFailed event);

    void onStockCommitted(StockCommitted event);

    void onStockCommitFailed(StockCommitFailed event);

    void onStockReleased(StockReleased event);
}