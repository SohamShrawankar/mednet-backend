package com.mednet.controller;

import com.mednet.entity.Patient;
import com.mednet.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> findAll() {
        return patientService.findAll();
    }

    @PostMapping
    public Patient create(@RequestBody Patient patient) {
        return patientService.save(patient);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        patientService.deleteById(id);
    }
}
