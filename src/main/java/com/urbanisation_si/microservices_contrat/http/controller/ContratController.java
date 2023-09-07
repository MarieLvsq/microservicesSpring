package com.urbanisation_si.microservices_contrat.http.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.urbanisation_si.microservices_assure.dao.AssureRepository;
import com.urbanisation_si.microservices_contrat.dao.ContratRepository;
import com.urbanisation_si.microservices_contrat.model.Contrat;
import com.urbanisation_si.microservices_produit.dao.ProduitRepository;
import com.urbanisation_si.microservices_produit.http.controller.ResourceNotFoundException;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/contrats")
public class ContratController {

	@Autowired
	private ContratRepository contratRepository;

	@Autowired
	private AssureRepository assureRepository; // Inject AssureRepository

	@Autowired
	private ProduitRepository produitRepository; // Inject ProduitRepository

	@ApiOperation(value = "Ajoute un Contrat.")
	@PostMapping(path = "/ajouterContrat")
	public ResponseEntity<Void> creerContrat(@Valid @RequestBody Contrat contrat) {
		Contrat contratAjoute = contratRepository.save(contrat);

		if (contratAjoute == null)
			return ResponseEntity.noContent().build();

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(contratAjoute.getId())
				.toUri();

		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value = "Affiche la liste des contrats.")
	@GetMapping(path = "/listerContrats")
	public @ResponseBody Iterable<Contrat> getAllContrats() {
		return contratRepository.findAll();
	}

	@ApiOperation(value = "Trouve un contrat par numéro de contrat.")
	@GetMapping(path = "/trouverContrat/{numeroContrat}")
	public ResponseEntity<Contrat> findContratByNumeroContrat(@PathVariable Long numeroContrat) {
		Contrat contrat = contratRepository.findByNumeroContrat(numeroContrat);

		if (contrat != null) {
			return ResponseEntity.ok(contrat);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@ApiOperation(value = "Trouve un contrat par numéro d'assuré.")
	@GetMapping(path = "/trouverContratAssure/{numeroAssure}")
	public ResponseEntity<?> findContratByNumeroAssure(@PathVariable Long numeroAssure) {
	    try {
	        Iterable<Contrat> produits = contratRepository.findByNumeroAssure(numeroAssure);
	        return ResponseEntity.ok(produits);
	    } catch (ResourceNotFoundException ex) {
	        return ResponseEntity
	            .status(HttpStatus.NOT_FOUND) // Use 404 Not Found status code
	            .body("Produit not found with numeroProduit: " + numeroAssure);
	    }
	}

	@ApiOperation(value = "Trouve un contrat par numéro de produit.")
	@GetMapping(path = "/trouverContratProduit/{numeroProduit}")
	public ResponseEntity<?> findContratByNumeroProduit(@PathVariable Long numeroProduit) {
	    try {
	        Iterable<Contrat> contrats = contratRepository.findByNumeroProduit(numeroProduit);
	        return ResponseEntity.ok(contrats);
	    } catch (ResourceNotFoundException ex) {
	        return ResponseEntity
	            .status(HttpStatus.NOT_FOUND) // Use 404 Not Found status code
	            .body("Produit not found with numeroProduit: " + numeroProduit);
	    }
	}

	// Helper method to check if Assure exists
	private boolean assureExists(Long numeroAssure) {
		// Use AssureRepository to check if an Assure with the given numeroAssure exists
		return assureRepository.existsByNumeroAssure(numeroAssure);
	}

	// Helper method to check if Produit exists
	private boolean produitExists(Long numeroProduit) {
		// Use ProduitRepository to check if a Produit with the given numeroProduit
		// exists
		return produitRepository.existsByNumeroProduit(numeroProduit);
	}
}
