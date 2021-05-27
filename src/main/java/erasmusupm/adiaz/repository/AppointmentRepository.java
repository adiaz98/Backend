package erasmusupm.adiaz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import erasmusupm.adiaz.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    Appointment findById(long id);
}


