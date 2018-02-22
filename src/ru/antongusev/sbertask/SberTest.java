package ru.antongusev.sbertask;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class SberTest {

        private final static Map<LocalDate, BigDecimal> paymentByWeek = new HashMap<>();

        private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        private static BigDecimal jobDaysCount = new BigDecimal("5");

        static {
            paymentByWeek.put(LocalDate.parse("26.02.2013", dateTimeFormatter), new BigDecimal("312.00"));
            paymentByWeek.put(LocalDate.parse("05.03.2013", dateTimeFormatter), new BigDecimal("833.00"));
            paymentByWeek.put(LocalDate.parse("12.03.2013", dateTimeFormatter), new BigDecimal("225.00"));
            paymentByWeek.put(LocalDate.parse("19.03.2013", dateTimeFormatter), new BigDecimal("453.00"));
            paymentByWeek.put(LocalDate.parse("26.03.2013", dateTimeFormatter), new BigDecimal("774.00"));
            paymentByWeek.put(LocalDate.parse("02.04.2013", dateTimeFormatter), new BigDecimal("719.00"));
            paymentByWeek.put(LocalDate.parse("09.04.2013", dateTimeFormatter), new BigDecimal("136.00"));
            paymentByWeek.put(LocalDate.parse("16.04.2013", dateTimeFormatter), new BigDecimal("133.00"));
            paymentByWeek.put(LocalDate.parse("23.04.2013", dateTimeFormatter), new BigDecimal("157.00"));
            paymentByWeek.put(LocalDate.parse("30.04.2013", dateTimeFormatter), new BigDecimal("850.00"));
            paymentByWeek.put(LocalDate.parse("07.05.2013", dateTimeFormatter), new BigDecimal("940.00"));
            paymentByWeek.put(LocalDate.parse("14.05.2013", dateTimeFormatter), new BigDecimal("933.00"));
            paymentByWeek.put(LocalDate.parse("21.05.2013", dateTimeFormatter), new BigDecimal("422.00"));
            paymentByWeek.put(LocalDate.parse("28.05.2013", dateTimeFormatter), new BigDecimal("952.00"));
            paymentByWeek.put(LocalDate.parse("04.06.2013", dateTimeFormatter), new BigDecimal("136.00"));
            paymentByWeek.put(LocalDate.parse("11.06.2013", dateTimeFormatter), new BigDecimal("701.00"));
        }

        public static Map<Integer, BigDecimal> convertFromPaymentByWeekToPaymentByMonth(int month) {
            Map<Integer, BigDecimal> paymentByMonth = new HashMap();
            BigDecimal bigDecimal;
            for (Map.Entry<LocalDate, BigDecimal> entry : paymentByWeek.entrySet()) {
                LocalDate localDate = entry.getKey();
                if (month == 0 || localDate.getMonthValue() == month) {
                    bigDecimal = evaluateValue(localDate, entry.getValue());
                    paymentByMonth.merge(localDate.getMonthValue(), bigDecimal, (v1, v2) -> v1.add(v2));
                }
            }
            return paymentByMonth;
        }

        public static BigDecimal evaluateValue(LocalDate localDate, BigDecimal bigDecimal) {
            if (localDate.getDayOfMonth() < 7 && localDate.getDayOfMonth() > 0) {
                int jobDays = increment(1, localDate.getDayOfMonth(), localDate);
                return bigDecimal.divide(jobDaysCount).multiply(new BigDecimal(jobDays));
            }
            int lastDayOfMonth = LocalDate.of(localDate.getYear(), localDate.getMonthValue() + 1, 1).minus(1, ChronoUnit.DAYS).getDayOfMonth();
            int diff = lastDayOfMonth - localDate.getDayOfMonth();
            if (diff < 7 && diff > 0) {
                int jobDays = increment(localDate.getDayOfMonth() + 1, lastDayOfMonth, localDate);
                return paymentByWeek.get(localDate.plus(1, ChronoUnit.WEEKS)).divide(jobDaysCount).multiply(new BigDecimal(jobDays)).add(bigDecimal);
            }
            return bigDecimal;
        }

        private static int increment(int fromValue, int toValue, LocalDate localDate) {
            int jobDaysCounter = 0;
            for (int i = fromValue; i <= toValue; i++) {
                int day = LocalDate.of(localDate.getYear(), localDate.getMonth(), i).getDayOfWeek().getValue();
                if (day != 6 && day != 7) {
                    jobDaysCounter++;
                }
            }
            return jobDaysCounter;
        }

        public static void main(String[] args) {
            assert convertFromPaymentByWeekToPaymentByMonth(2).get(2).equals(new BigDecimal("645.20"));
            assert convertFromPaymentByWeekToPaymentByMonth(3).get(3).equals(new BigDecimal("2383.20"));
            assert convertFromPaymentByWeekToPaymentByMonth(4).get(4).equals(new BigDecimal("1563.60"));
            assert convertFromPaymentByWeekToPaymentByMonth(5).get(5).equals(new BigDecimal("3328.60"));
            assert convertFromPaymentByWeekToPaymentByMonth(6).get(6).equals(new BigDecimal("755.40"));
        }


}
