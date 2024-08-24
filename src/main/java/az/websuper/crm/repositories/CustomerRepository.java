package az.websuper.crm.repositories;

import az.websuper.crm.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Page<Customer> findByCompanyId(Pageable pageable,Long companyId);
}
