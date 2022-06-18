package com.smg.springboot.apirest.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smg.springboot.apirest.app.models.entity.Cliente;
import com.smg.springboot.apirest.app.models.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {

		return clienteService.findAll();
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) { // PathVariable permite implementar una variable pedida en el
															// path {id}
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.findById(id);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (clienteService == null) {
			response.put("mensaje",
					"El cliente con el ID:  ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(clienteService.findById(id), HttpStatus.OK);

	}

	@PostMapping("/clientes")
	public ResponseEntity<?> create(@RequestBody Cliente cliente) { // Recibe la respuesta como JSON
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		try {
			clienteNew = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el INSERT en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido creado con exito!");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); // Lo mapea y lo persiste en la
																						// base de datos
	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@RequestBody Cliente cliente, Long id) {
		Cliente clienteActual = clienteService.findById(id);

		Cliente clienteUpdate = null;
		Map<String, Object> response = new HashMap<>();

		if (clienteActual == null) {
			response.put("mensaje", "Error: no se pudo editar!, el cliente con el ID:  "
					.concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());

			clienteUpdate = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido actualizado con exito");
		response.put("cliente", clienteUpdate);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido eliminado con exito!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
