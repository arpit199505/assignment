package com.java.assignment.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.assignment.domain.File;
import com.java.assignment.domain.UserDetails;
import com.java.assignment.domain.UserDetailsNonMand;
import com.java.assignment.service.FormService;

@RestController
@RequestMapping("/api/user")
public class FormController {

	@Autowired
	private FormService formService;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestPart UserDetails userDetails,
			@RequestPart(value = "attachment") MultipartFile attachment) throws IOException {
		if (attachment != null) {
			File file = formService.storeFile(attachment);
			file.setUserDetails(userDetails);
			userDetails.setFile(file);
		}
		UserDetails userDet = formService.registerUser(userDetails);

		return new ResponseEntity<UserDetails>(userDet, HttpStatus.CREATED);
	}

	@GetMapping("")
	public ResponseEntity<?> getUsers() {
		List<UserDetails> userDet = formService.getUserDetails();
		return new ResponseEntity<List<UserDetails>>(userDet, HttpStatus.OK);
	}

	@GetMapping("/{email}")
	public ResponseEntity<?> findUser(@PathVariable String email) {
		UserDetails userDet = formService.findUser(email);
		return new ResponseEntity<UserDetails>(userDet, HttpStatus.OK);
	}

	@PatchMapping("/{email}")
	public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody UserDetailsNonMand userDetails) {
		UserDetails userDet = formService.updateUserDetails(email, userDetails);
		return new ResponseEntity<UserDetails>(userDet, HttpStatus.OK);
	}

	@DeleteMapping("/{email}")
	public ResponseEntity<?> deleteUser(@PathVariable String email) {
		formService.deleteUser(email);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/downloadFile/{email}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String email) throws IOException {

		File dbFile = formService.getFiles(email);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(dbFile.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
				.body(new ByteArrayResource(dbFile.getData()));
	}
}
