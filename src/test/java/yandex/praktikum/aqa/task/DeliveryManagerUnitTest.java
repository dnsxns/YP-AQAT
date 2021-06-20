package yandex.praktikum.aqa.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import yandex.praktikum.aqa.task.exceptions.InvalidDeliveryException;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DeliveryManagerUnitTest {

    private DeliveryManager deliveryManager;

    @BeforeEach
    public void setUp() {
        deliveryManager = new DeliveryManager();
    }

    @Test
    @DisplayName("Delivery cost for simple case")
    void deliveryCostSimpleCaseTest() throws InvalidDeliveryException {
        BigDecimal expectedCost = new BigDecimal("840.00");
        BigDecimal actualCost = deliveryManager
                .getCost(5, DeliveryManager.GoodsDimension.BIG, true, 65);
        assertEquals(expectedCost, actualCost);
    }

    private static Stream<Arguments> positiveDistanceTestData() {
        return Stream.of(
                arguments(1.2, new BigDecimal("720.00")),
                arguments(6.5, new BigDecimal("800.00")),
                arguments(25.0, new BigDecimal("960.00")),
                arguments(82.7, new BigDecimal("1120.00"))
        );
    }

    private static Stream<Arguments> negativeDistanceTestData() {
        return Stream.of(
                arguments(0, new BigDecimal("720.00")),
                arguments(-52.76, new BigDecimal("720.00"))
        );
    }

    @ParameterizedTest
    @MethodSource({"positiveDistanceTestData", "negativeDistanceTestData"})
    @DisplayName("Delivery cost for different distance")
    void deliveryCostDifferentDistanceTest(double distance, BigDecimal expectedCost) throws InvalidDeliveryException {
        if (distance > 30) {
            InvalidDeliveryException invalidDeliveryException = assertThrows(InvalidDeliveryException.class, () -> {
                deliveryManager.getCost(distance, DeliveryManager.GoodsDimension.LITTLE, true, 90);
            });
            assertEquals("Impossible to delivery fragile goods beyond 30km",
                    invalidDeliveryException.getMessage());
        } else {
            BigDecimal actualCost = deliveryManager
                    .getCost(distance, DeliveryManager.GoodsDimension.LITTLE, true, 90);
            assertEquals(expectedCost, actualCost);
        }
    }

    private static Stream<Arguments> goodsDimensionPositiveTestData() {
        return Stream.of(
                arguments(DeliveryManager.GoodsDimension.LITTLE, new BigDecimal("960.00")),
                arguments(DeliveryManager.GoodsDimension.BIG, new BigDecimal("1120.00"))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "goodsDimensionPositiveTestData")
    @DisplayName("Delivery cost for different goods dimension")
    void deliveryCostDifferentGoodsDimensionPositiveTest(DeliveryManager.GoodsDimension goodsDimension,
                                                         BigDecimal expectedCost) throws InvalidDeliveryException {
        BigDecimal actualCost = deliveryManager
                .getCost(25, goodsDimension, true, 90);
        assertEquals(expectedCost, actualCost);
    }

    @Test
    @DisplayName("Delivery cost for goods dimension that provided as null")
    void deliveryCostDifferentGoodsDimensionNegativeTest() {
        InvalidDeliveryException invalidDeliveryException = assertThrows(InvalidDeliveryException.class, () -> {
            deliveryManager.getCost(25, null, true, 90);
        });
        assertEquals("Goods dimension type is absent, please provide correct value",
                invalidDeliveryException.getMessage());
    }

    private static Stream<Arguments> fragilityGoodsTestData() {
        return Stream.of(
                arguments(false, new BigDecimal("640.00")),
                arguments(true, new BigDecimal("1120.00"))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "fragilityGoodsTestData")
    @DisplayName("Delivery cost for different goods fragility")
    void deliveryCostDifferentGoodsFragilityTest(boolean isFragileGoods,
                                                 BigDecimal expectedCost) throws InvalidDeliveryException {
        BigDecimal actualCost = deliveryManager
                .getCost(25, DeliveryManager.GoodsDimension.BIG, isFragileGoods, 90);
        assertEquals(expectedCost, actualCost);
    }

    private static Stream<Arguments> deliveryServiceWorkloadPositiveTestData() {
        return Stream.of(
                arguments(25, new BigDecimal("700.00")),
                arguments(45, new BigDecimal("840.00")),
                arguments(65, new BigDecimal("980.00")),
                arguments(85, new BigDecimal("1120.00"))
        );
    }

    private static Stream<Arguments> deliveryServiceWorkloadNegativeData() {
        return Stream.of(
                arguments(-250, new BigDecimal("700.00")),
                arguments(1530, new BigDecimal("1120.00"))
        );
    }

    @ParameterizedTest
    @MethodSource({"deliveryServiceWorkloadPositiveTestData", "deliveryServiceWorkloadNegativeData"})
    @DisplayName("Delivery cost for different delivery service workload")
    void deliveryCostDifferentDeliveryServiceWorkloadTest(int deliveryServiceWorkload,
                                                          BigDecimal expectedCost) throws InvalidDeliveryException {
        BigDecimal actualCost = deliveryManager
                .getCost(25, DeliveryManager.GoodsDimension.BIG, true, deliveryServiceWorkload);
        assertEquals(expectedCost, actualCost);
    }

}