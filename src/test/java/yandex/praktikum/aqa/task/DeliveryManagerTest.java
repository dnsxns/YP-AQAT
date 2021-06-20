package yandex.praktikum.aqa.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import yandex.praktikum.aqa.task.exceptions.InvalidDeliveryException;
import yandex.praktikum.aqa.task.utils.Utils;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_FLOOR;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DeliveryManagerTest {

    private enum DistanceType {
        SHORT, MEDIUM, LONG, LONGEST;
    }

    private enum DSWType {
        LOW, MEDIUM, HIGH, HIGHEST;
    }

    private static final double GREATEST_DISTANCE = 5000;

    private static double generateDistance(DistanceType distanceType) {
        switch (distanceType) {
            case SHORT:
                return Utils.generateNumberInRange(0, 2);
            case MEDIUM:
                return Utils.generateNumberInRange(2, 10);
            case LONG:
                return Utils.generateNumberInRange(10, 30);
            case LONGEST:
                return Utils.generateNumberInRange(30, GREATEST_DISTANCE);
        }
        return 0;
    }

    private static int generateDeliveryServiceWorkload(DSWType deliveryServiceWorkloadType) {
        switch (deliveryServiceWorkloadType) {
            case LOW:
                return (int)Utils.generateNumberInRange(0, 40);
            case MEDIUM:
                return (int)Utils.generateNumberInRange(40, 60);
            case HIGH:
                return (int)Utils.generateNumberInRange(60, 80);
            case HIGHEST:
                return (int)Utils.generateNumberInRange(80, 100);
        }
        return 0;
    }

    private BigDecimal getCostByDistance(double distance) {
        distance = distance < 0 ? 0 : distance;
        boolean isShortDistance = distance >= 0 && distance < 2;
        boolean isMediumDistance = distance > 2 && distance < 10;
        boolean isLongDistance = distance > 10 && distance < 30;
        boolean isLongestDistance = distance > 30;
        double cost = isLongestDistance ? 300 : isLongDistance ? 200 : isMediumDistance ? 100 : isShortDistance ? 50 : 0;
        return BigDecimal.valueOf(cost);
    }

    private BigDecimal getCostByGoodsDimension(DeliveryManager.GoodsDimension goodsDimension) {
        double cost = goodsDimension == DeliveryManager.GoodsDimension.BIG ? 200 : goodsDimension == DeliveryManager.GoodsDimension.LITTLE ? 100 : 0;
        return BigDecimal.valueOf(cost);
    }

    private BigDecimal getCostByFragileGoods(boolean isFragileGoods) {
        double cost = isFragileGoods ? 300 : 0;
        return BigDecimal.valueOf(cost);
    }

    private double getDeliveryServiceWorkloadFactor(int deliveryServiceWorkload) {
        boolean isMediumWorkload = deliveryServiceWorkload >= 40 && deliveryServiceWorkload < 60;
        boolean isHighWorkload = deliveryServiceWorkload >= 60 && deliveryServiceWorkload < 80;
        boolean isHighestWorkload = deliveryServiceWorkload >= 80 && deliveryServiceWorkload < 100;
        return isHighestWorkload ? 1.6 : isHighWorkload ? 1.4 : isMediumWorkload ? 1.2 : 1.0;
    }

    private static Stream<Arguments> deliveryGeneratedTestData() {
        return Stream.of(
                // ------ Distance,                             G.dimension,            G.frg.,  Delivery service workload
                //        < 2km                                 Big                                     -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //        < 2km                                 Big                                     +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //        < 2km                                 Little                                  -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //        < 2km                                 Little                                  +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.SHORT), DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //        2km - 10km                            Big                                     -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //        2km - 10km                            Big                                     +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          2km - 10km                          Little                                  -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          2km - 10km                          Little                                  +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.MEDIUM),DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          10km - 30km                         Big                                     -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          10km - 30km                         Big                                     +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.BIG,     true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          10km - 30km                         Little                                  -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          10km - 30km                         Little                                  +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONG),  DeliveryManager.GoodsDimension.LITTLE,  true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          > 30km                               Big                                    -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          > 30km                               Big                                    +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.BIG,    true,    generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          > 30km                               Little                                 -        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, false,   generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, false,   generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, false,   generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, false,   generateDeliveryServiceWorkload(DSWType.HIGHEST)),
                //          > 30km                               Little                                 +        (Low / Medium / High / Highest)
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, true,    generateDeliveryServiceWorkload(DSWType.LOW)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, true,    generateDeliveryServiceWorkload(DSWType.MEDIUM)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, true,    generateDeliveryServiceWorkload(DSWType.HIGH)),
                arguments(generateDistance(DistanceType.LONGEST),DeliveryManager.GoodsDimension.LITTLE, true,    generateDeliveryServiceWorkload(DSWType.HIGHEST))
        );
    }

    @ParameterizedTest
    @DisplayName("Check delivery cost for all cases")
    @MethodSource("deliveryGeneratedTestData")
    void deliveryCostTest(double distance, DeliveryManager.GoodsDimension goodsDimension,
                          boolean isFragileGoods, int deliveryServiceWorkload) throws InvalidDeliveryException {
        BigDecimal expectedCost = new BigDecimal(0)
                .add(getCostByDistance(distance))
                .add(getCostByGoodsDimension(goodsDimension))
                .add(getCostByFragileGoods(isFragileGoods))
                .multiply(BigDecimal.valueOf(getDeliveryServiceWorkloadFactor(deliveryServiceWorkload)));
        expectedCost = expectedCost.max(BigDecimal.valueOf(400));

        if (distance > 30 && isFragileGoods) {
            Assertions.assertThrows(InvalidDeliveryException.class, () -> {
                new DeliveryManager().getCost(distance, goodsDimension, isFragileGoods, deliveryServiceWorkload);
            });
        } else {
            BigDecimal deliveryCost = new DeliveryManager()
                    .getCost(distance, goodsDimension, isFragileGoods, deliveryServiceWorkload);
            Assertions.assertEquals(expectedCost.setScale(2, ROUND_FLOOR), deliveryCost);
        }
    }

}