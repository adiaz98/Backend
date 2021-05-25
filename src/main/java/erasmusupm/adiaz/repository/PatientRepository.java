package erasmusupm.adiaz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import erasmusupm.adiaz.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {
    Patient findById(long id);
}


