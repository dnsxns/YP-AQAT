package yandex.praktikum.aqa.task.utils;

import java.util.Random;

public class Utils {

    public static double generateNumberInRange(double min, double max) {
        return Math.round((min + new Random().nextDouble() * (max - min)) * 100) / 100.0;
    }

}