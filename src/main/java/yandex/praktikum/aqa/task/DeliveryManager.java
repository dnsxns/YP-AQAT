package yandex.praktikum.aqa.task;

import yandex.praktikum.aqa.task.exceptions.InvalidDeliveryException;

import java.math.BigDecimal;

public class DeliveryManager {

    enum GoodsDimension {
        BIG, LITTLE;
    }

    /**
     * Get cost according to provided distance to destination
     *
     * @param distance - distance to destination (km)
     * @return cost (rub)
     */
    private BigDecimal getCostByDistance(double distance) {
        distance = distance < 0 ? 0 : distance;
        return BigDecimal.valueOf(distance > 30 ? 300 : distance > 10 ? 200 : distance > 2 ? 100: 50);
    }

    /**
     * Get cost according to provided goods dimension
     *
     * @param goodsDimension - goods dimension (little/big)
     * @return cost (rub)
     */
    private BigDecimal getCostByGoodsDimension(GoodsDimension goodsDimension) {
        return BigDecimal.valueOf(goodsDimension == GoodsDimension.BIG ? 200 : 100);
    }

    /**
     * Get cost according to provided goods fragility
     *
     * @param isFragileGoods - flag for fragile goods (true/false)
     * @return cost (rub)
     */
    private BigDecimal getCostByGoodsFragility(boolean isFragileGoods) {
        return BigDecimal.valueOf(isFragileGoods ? 300 : 0);
    }

    /**
     * Get factor according to provided delivery service workload
     *
     * @param deliveryServiceWorkload - delivery service workload in percents (0-100)
     * @return factor (number)
     */
    private double getDeliveryServiceWorkloadFactor(int deliveryServiceWorkload) {
        deliveryServiceWorkload = Math.max(deliveryServiceWorkload, 0);
        deliveryServiceWorkload = Math.min(deliveryServiceWorkload, 100);
        boolean isMediumLoad = deliveryServiceWorkload >= 40 && deliveryServiceWorkload < 60;
        boolean isHighLoad = deliveryServiceWorkload >= 60 && deliveryServiceWorkload < 80;
        boolean isCriticalLoad = deliveryServiceWorkload >= 80 && deliveryServiceWorkload <= 100;
        return isCriticalLoad ? 1.6 : isHighLoad ? 1.4 : isMediumLoad ? 1.2 : 1;
    }

    /**
     * Calculate and return delivery cost according to provided data
     *
     * @param distance                  - distance to destination (km)
     * @param goodsDimension            - goods dimension (little/big)
     * @param isFragileGoods            - flag for fragile goods (true/false)
     * @param deliveryServiceWorkload   - delivery service workload in percents (0-100)
     * @return cost (rub)
     */
    public BigDecimal getCost(double distance, GoodsDimension goodsDimension, boolean isFragileGoods,
                          int deliveryServiceWorkload) throws InvalidDeliveryException {
        if (distance > 30 && isFragileGoods) {
            throw new InvalidDeliveryException("Impossible to delivery fragile goods beyond 30km");
        }
        if (goodsDimension == null) {
            throw new InvalidDeliveryException("Goods dimension type is absent, please provide correct value");
        }
        BigDecimal minCost = BigDecimal.valueOf(400);
        BigDecimal resultCost = new BigDecimal(0)
                .add(getCostByDistance(distance))
                .add(getCostByGoodsDimension(goodsDimension))
                .add(getCostByGoodsFragility(isFragileGoods))
                .multiply(BigDecimal.valueOf(getDeliveryServiceWorkloadFactor(deliveryServiceWorkload)));
        resultCost = resultCost.max(minCost);
        System.out.println(String.format("Delivery cost for %s goods (fragile = '%s') on %skm with service workload %d%% is %sRUB",
                goodsDimension.toString().toLowerCase(), isFragileGoods, distance, deliveryServiceWorkload, resultCost));
        return resultCost.setScale(2, BigDecimal.ROUND_FLOOR);
    }

}