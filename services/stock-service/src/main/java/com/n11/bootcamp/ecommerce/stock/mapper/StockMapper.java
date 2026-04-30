package com.n11.bootcamp.ecommerce.stock.mapper;

import com.n11.bootcamp.ecommerce.stock.dto.StockBatchResponse;
import com.n11.bootcamp.ecommerce.stock.dto.StockStatus;
import com.n11.bootcamp.ecommerce.stock.entity.StockLevel;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    private static final int LOW_STOCK_THRESHOLD = 10;

    public StockBatchResponse.Item toBatchItem(StockLevel stockLevel) {
        return new StockBatchResponse.Item(
                stockLevel.getProductId(),
                deriveStatus(stockLevel.getAvailableQuantity()),
                stockLevel.getAvailableQuantity()
        );
    }

    private StockStatus deriveStatus(int availableQuantity) {
        if (availableQuantity == 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (availableQuantity <= LOW_STOCK_THRESHOLD) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }
}