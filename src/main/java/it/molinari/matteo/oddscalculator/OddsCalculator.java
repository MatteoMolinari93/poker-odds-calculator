package it.molinari.matteo.oddscalculator;

import it.molinari.matteo.oddscalculator.persistence.HibernateUtil;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OddsCalculator {

    private static final int iterations = 7;
    static final int total = 133784560;

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Start calculation at " + startTime);

        final var cards = getSingleCards();
        saveCombinations(cards.stream().map(Combination::new).collect(Collectors.toList()));

        for (int i = 1; i < iterations; i++) {
            try (StatelessSession session = HibernateUtil.getSessionFactory().openStatelessSession()) {
                ScrollableResults<Combination> results = getCombinationScrollableResults(session);
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
                }
                results.close();

                deleteOldCombinations(i);
                int computedCount = getSavedCombinationsCount().intValue();
                System.out.println(LocalDateTime.now() + " - Computed combinations with " + (i+1) + "cards: " + computedCount + " combinations of " + total + ", " + computedCount / OddsCalculator.total + "% completed...");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("Ended calculation at " + endTime + ". Elapsed time: " + Duration.between(startTime, endTime).toString());
    }

    private static ScrollableResults<Combination> getCombinationScrollableResults(StatelessSession session) {
        Query<Combination> query = session
                .createQuery("from Combination c", Combination.class);
        query.setFetchSize(1000);
        query.setReadOnly(true);
        query.setLockMode("a", LockMode.NONE);
        ScrollableResults<Combination> results = query.scroll(ScrollMode.FORWARD_ONLY);
        return results;
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

    private static void deleteOldCombinations(int cardNumber) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query q = session.createQuery("delete Combination c " +
                    "where card" + cardNumber +" is null");
            q.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    private static Long getSavedCombinationsCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(*) from Combination c", Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

}
