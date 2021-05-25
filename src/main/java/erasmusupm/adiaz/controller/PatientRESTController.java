package erasmusupm.adiaz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import erasmusupm.adiaz.model.Patient;
import erasmusupm.adiaz.repository.PatientRepository;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/restApi/patients")
public class PatientRESTController {

    private PatientRepository patientRepository;

    @Autowired
    public PatientRESTController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @RequestMapping(method = RequestMethod.GET/*, produces = "application/xml"*/)
    //@GetMapping
    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    //@PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        patientRepository.save(patient);
        return new ResponseEntity<Patient>(patient, HttpStatus.CREATED);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    //@DeleteMapping("/{id}")
    public ResponseEntity<Patient> deletePatient (@PathVariable("id") long id) {
        Patient patient = patientRepository.findById(id);
        if (patient == null) {
            System.out.println("Patient not found!");
            return new ResponseEntity<Patient>(HttpStatus.NOT_FOUND);
        }

        patientRepository.deleteById(id);
        return new ResponseEntity<Patient>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
    //@PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@RequestBody Patient patient, @PathVariable("id") long id) {
        patient.setId(id);
        patientRepository.save(patient);
        return new ResponseEntity<Patient>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.PATCH)
    //@PatchMapping("/{id}")
    public ResponseEntity<Patient> updatePartOfPatient(@RequestBody Map<String, Object> updates, @PathVariable("id") long id) {
        Patient patient = patientRepository.findById(id);
        if (patient == null) {
            System.out.println("Patient not found!");
            return new ResponseEntity<Patient>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(patient,updates);
        return new ResponseEntity<Patient>(HttpStatus.NO_CONTENT);
    }

    private void partialUpdate(Patient patient, Map<String, Object> updates) {
        if (updates.containsKey("firstname")) {
            patient.setFirstname((String) updates.get("firstname"));
        }
        if (updates.containsKey("lastname")) {
            patient.setLastname((String) updates.get("lastname"));
        }
        if (updates.containsKey("email")) {
            patient.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("telephone")) {
            patient.setTelephone((String) updates.get("telephone"));
        }
        patientRepository.save(patient);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    //@GetMapping("/{id}")
    public Patient getPatient (@PathVariable ("id") long id){
        Patient patient = patientRepository.findById(id);
        if (patient == null){
            System.out.println("Patient not found!");
            new ResponseEntity<Patient>(HttpStatus.NOT_FOUND);
        }
        return patient;
    }
}


