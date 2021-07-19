package com.java.assignment.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.assignment.domain.File;
import com.java.assignment.domain.UserDetails;
import com.java.assignment.domain.UserDetailsNonMand;
import com.java.assignment.repository.FileRepo;
import com.java.assignment.repository.FormRepository;

@Service
public class FormService {

	@Autowired
	private FormRepository formRepo;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private FileRepo dbFileRepository;

	public UserDetails registerUser(UserDetails userDetails) {
		String userPass = generatePassword(10);
		userDetails.setUserPass(userPass);
		sendEmail(userDetails.getEmail(), userPass);
		return formRepo.save(userDetails);
	}

	private void sendEmail(String to, String userPass) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreply@abccorporation.com");
		message.setTo(to);
		message.setSubject("Welcome to ABC Corporation");
		message.setText("Username: " + to + " \nPassword: " + userPass);
		emailSender.send(message);
	}

	private String generatePassword(int length) {
		RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(32, 45).build();
		return pwdGenerator.generate(length);
	}

	public List<UserDetails> getUserDetails() {
		return formRepo.findAll();
	}

	public UserDetails findUser(String email) {
		UserDetails userDetails = formRepo.findByEmail(email);
		return userDetails;
	}

	public UserDetails updateUserDetails(String email, UserDetailsNonMand userDetailsUpdate) {

		UserDetails userDetails = findUser(email);

		if (StringUtils.isNotBlank(userDetailsUpdate.getCompany()))
			userDetails.setCompany(userDetailsUpdate.getCompany());

		if (StringUtils.isNotBlank(userDetailsUpdate.getTitle()))
			userDetails.setTitle(userDetailsUpdate.getTitle());

		if (userDetailsUpdate.getPhoneNumber() != null)
			userDetails.setPhoneNumber(userDetailsUpdate.getPhoneNumber());

		return formRepo.save(userDetails);
	}

	public void deleteUser(String email) {
		UserDetails userDetails = findUser(email);
		formRepo.delete(userDetails);
	}

	public File storeFile(MultipartFile multipartFile) throws IOException {

		try {

			File dbFile = new File(multipartFile.getOriginalFilename(), multipartFile.getContentType(),
					multipartFile.getBytes());

			return dbFileRepository.save(dbFile);
		} catch (IOException ex) {
			throw new IOException("Could not store file " + multipartFile.getOriginalFilename() + ". Please try again!",
					ex);
		}
	}

	public File getFile(Long fileId) throws IOException {
		return dbFileRepository.findById(fileId).orElseThrow(() -> new IOException("File not found with id " + fileId));
	}

	public File getFiles(String email) {
		UserDetails userDetails = findUser(email);
		File file = dbFileRepository.findByUserDetails(userDetails);
		return file;
	}
}
