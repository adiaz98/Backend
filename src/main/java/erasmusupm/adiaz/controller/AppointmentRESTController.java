package erasmusupm.adiaz.controller;

import erasmusupm.adiaz.model.Patient;
import erasmusupm.adiaz.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import erasmusupm.adiaz.model.Appointment;
import erasmusupm.adiaz.repository.AppointmentRepository;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/restApi/appointments")
public class AppointmentRESTController {

    private AppointmentRepository appointmentRepository;
    private PatientRepository patientRepository;

    @Autowired
    public AppointmentRESTController(AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

//    @RequestMapping(method = RequestMethod.GET/*, produces = "application/xml"*/)
//    //@GetMapping
//    public List<Appointment> findAllAppointments() {
//        return appointmentRepository.findAll();
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    //@PostMapping
    public ResponseEntity<Appointment> addAppointment(@RequestBody Appointment appointment, @PathVariable("id") long id) {
        Patient patient = patientRepository.findById(id);
        appointment.setPatient(patient);
        appointmentRepository.save(appointment);
        return new ResponseEntity<Appointment>(appointment, HttpStatus.CREATED);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    //@DeleteMapping("/{id}")
    public ResponseEntity<Appointment> deleteAppointment (@PathVariable("id") long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            System.out.println("Appointment not found!");
            return new ResponseEntity<Appointment>(HttpStatus.NOT_FOUND);
        }

        appointmentRepository.deleteById(id);
        return new ResponseEntity<Appointment>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    //@PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@RequestBody Appointment appointment, @PathVariable("id") long id) {
        appointment.setId(id);
        appointmentRepository.save(appointment);
        return new ResponseEntity<Appointment>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PATCH)
    //@PatchMapping("/{id}")
    public ResponseEntity<Appointment> updatePartOfAppointment(@RequestBody Map<String, Object> updates, @PathVariable("id") long id) {
        Appointment appointment = appointmentRepository.findById(id);
        if (appointment == null) {
            System.out.println("Appointment not found!");
            return new ResponseEntity<Appointment>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(appointment,updates);
        return new ResponseEntity<Appointment>(HttpStatus.NO_CONTENT);
    }

    private void partialUpdate(Appointment appointment, Map<String, Object> updates) {
        if (updates.containsKey("date")) {
            appointment.setDate((String) updates.get("date"));
        }
        if (updates.containsKey("department")) {
            appointment.setDepartment((String) updates.get("department"));
        }
        if (updates.containsKey("room")) {
            appointment.setRoom((String) updates.get("room"));
        }
        if (updates.containsKey("reason")) {
            appointment.setReason((String) updates.get("reason"));
        }
        if (updates.containsKey("recipe")) {
            appointment.setRecipe((String) updates.get("recipe"));
        }
        appointmentRepository.save(appointment);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    //@GetMapping("/{id}")
    public List<Appointment> getAppointmentsByUser (@PathVariable ("id") long id){
        List<Appointment> appointmentList = patientRepository.findById(id).getAppointmentList();
        if (appointmentList == null){
            System.out.println("Appointment not found!");
            new ResponseEntity<Appointment>(HttpStatus.NOT_FOUND);
        }
        return appointmentList;
    }
}


