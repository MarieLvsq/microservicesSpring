package com.urbanisation_si.microservices_produit.http.controller;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}