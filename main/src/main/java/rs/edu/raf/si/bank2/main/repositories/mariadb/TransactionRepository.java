package rs.edu.raf.si.bank2.main.repositories.mariadb;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.si.bank2.main.models.mariadb.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserEmailAndCurrencyCurrencyCode(String userEmail, String currencyCode);

    List<Transaction> findAllByOrderId(Long id);
}
