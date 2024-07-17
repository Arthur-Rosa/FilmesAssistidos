package com.sabado.filme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sabado.filme.model.Filme;
import com.sabado.filme.repo.FilmeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Optional;

@Controller
@RequestMapping("/filme")
public class FilmeController {

	@Autowired
	private FilmeRepository filmeRepo;

	private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/";

	@GetMapping("/")
	public String inicio(Model model) {
		model.addAttribute("filmes", filmeRepo.findAll());
		return "index";
	}

	@GetMapping("/form")
	public String form(Model model) {
		model.addAttribute("filme", new Filme());
		return "form";
	}

	@GetMapping("/form/{id}")
	public String form(@PathVariable("id") Long id, Model model) {
		Optional<Filme> filme = filmeRepo.findById(id);
		if (filme.isPresent()) {
			model.addAttribute("filme", filme.get());
		} else {
			model.addAttribute("filme", new Filme());
		}
		return "form";
	}

	@PostMapping("/add")
	public String addFilme(@RequestParam("id") Optional<Long> id, @RequestParam("nome") String nome,
			@RequestParam("data") String data, @RequestParam("imagem") MultipartFile imagem) {
		Filme filme;
		if (id.isPresent()) {
			filme = filmeRepo.findById(id.get()).orElse(new Filme());
		} else {
			filme = new Filme();
		}
		filme.setNome(nome);
		filme.setData(Date.valueOf(data));

		filmeRepo.save(filme);

		if (!imagem.isEmpty()) {
			try {
				String fileName = "filme_" + filme.getId() + "_" + imagem.getOriginalFilename();
				Path path = Paths.get(UPLOAD_DIR + fileName);
				Files.write(path, imagem.getBytes());
				filme.setImagem("/" + fileName);

				filmeRepo.save(filme);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "redirect:/filme/";
	}

	@GetMapping("/delete/{id}")
	public String deleteFilme(@PathVariable("id") Long id) {
		Optional<Filme> filme = filmeRepo.findById(id);
		if (filme.isPresent()) {
			Filme filmeToDelete = filme.get();
			String imagePath = UPLOAD_DIR + filmeToDelete.getImagem().substring("/uploads/".length());
			try {
				Files.deleteIfExists(Paths.get(imagePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			filmeRepo.deleteById(id);
		}
		return "redirect:/filme/";
	}

	@GetMapping("/{filename:.+}")
	@ResponseBody
	public Resource serveFile(@PathVariable String filename) {
		return new FileSystemResource(UPLOAD_DIR + filename);
	}
}
