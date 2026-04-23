import com.example.standtrain.util.UtilMethods;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Класс, описывающий модульные тесты проверки работы вспомогательных методов
 */
public class Tests {

    // Проверка на корректное преобразование ip в одно целое число
    @Test
    void testCreateIp() {
        int ip = UtilMethods.createIp(192, 168, 1, 10);
        Assertions.assertEquals(0xC0A8010A, ip);
    }

    // Проверка на корректное вычисление температуры по сопротивлению (точное табличное значение)
    @Test
    void testResistanceToTemperature1() {
        double temp = UtilMethods.resistanceToTemperature(53.5);
        Assertions.assertEquals(0.0, temp, 0.001);
    }

    // Проверка на корректное вычисление температуры по сопротивлению (между табличными значениями)
    @Test
    void testResistanceToTemperature2() {
        double temp = UtilMethods.resistanceToTemperature(54.5);
        Assertions.assertTrue(temp > 0 && temp < 10);
    }

    // Проверка на корректное вычисление температуры по сопротивлению (ниже минимального)
    @Test
    void testResistanceToTemperature_belowRange() {
        double temp = UtilMethods.resistanceToTemperature(1.0);
        Assertions.assertEquals(-200, temp);
    }

    // Проверка на корректное вычисление температуры по сопротивлению (выше максимального)
    @Test
    void testResistanceToTemperature_aboveRange() {
        double temp = UtilMethods.resistanceToTemperature(1000.0);
        Assertions.assertEquals(200, temp);
    }
}