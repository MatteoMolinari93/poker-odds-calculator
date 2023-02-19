package it.molinari.matteo.oddscalculator;

import it.molinari.matteo.oddscalculator.persistence.HibernateUtil;
import org.hibernate.*;
import org.hibernate.query.Query;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OddsCalculator {

    private static final int iterations = 5;
    static long count = 0L;
    static final long total = (long) Math.pow(52, iterations);

    public static void main(String[] args) {
        Timer timer = new Timer(10000, e -> System.out.println(LocalDateTime.now() + " - Computed " + + OddsCalculator.count + " combinations. " + OddsCalculator.count / (float)OddsCalculator.total + "% completed..."));

        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Start calculation at " + startTime);
        timer.start();

        final var cards = getSingleCards();
        saveCombinations(cards.stream().map(Combination::new).collect(Collectors.toList()));
        count += cards.size();

        for (int i = 0; i < iterations; i++) {
            try (StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession()) {
                Query<Combination> query = session
                        .createQuery("from Combination c", Combination.class);
                query.setFirstResult((int) Math.pow(52, i) - 1);
                query.setFetchSize(1000);
                query.setReadOnly(true);
                query.setLockMode("a", LockMode.NONE);
                ScrollableResults<Combination> results = query.scroll(ScrollMode.FORWARD_ONLY);
                while (results.next()) {
                    Combination savedCombination = results.get();
                    List<Combination> newCombinations = new ArrayList<>();
                    for (Card card : cards) {
                        if (!savedCombination.contains(card)) {
                            Combination newCombination = savedCombination.newCombinationWithCard(card);
                            newCombinations.add(newCombination);
                        }
                    }
                    saveCombinations(newCombinations);
                    count += newCombinations.size();
                }
                results.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        timer.stop();
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("Ended calculation at " + endTime + ". Elapsed time: " + Duration.between(startTime, endTime).toString());
    }

    private static List<Card> getSingleCards() {
        ArrayList<Card> result = new ArrayList<>();
        for (SeedsEnum seedEnum : SeedsEnum.values()) {
            for (CardValuesEnum valueEnum : CardValuesEnum.values()) {
                result.add(new Card(seedEnum, valueEnum));
            }
        }
        return result;
    }

    private static void saveCombinations(List<Combination> combinations) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            combinations.forEach(session::persist);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
