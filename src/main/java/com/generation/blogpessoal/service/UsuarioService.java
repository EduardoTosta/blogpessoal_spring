package com.generation.blogpessoal.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioDTO;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service
public class UsuarioService {
	
	//Usuarios verificados no sistema
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	//Faz o token
	@Autowired
	private JwtService jwtService;
	
	//Banco de dados
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public List<UsuarioDTO> listarUsuarios() {
	    List<Usuario> usuarios = usuarioRepository.findAll();
	    return usuarios.stream()
	                   .map(usuario -> new UsuarioDTO(
	                       usuario.getId(),
	                       usuario.getNome(),
	                       usuario.getUsuario(),
	                       usuario.getFoto()))
	                   .collect(Collectors.toList());
	}

	public Optional<UsuarioDTO> buscarUsuarioId(Long id) {
		 return usuarioRepository.findById(id)
	                   .map(usuario -> new UsuarioDTO(
	                       usuario.getId(),
	                       usuario.getNome(),
	                       usuario.getUsuario(),
	                       usuario.getFoto()));
	}


	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
	    if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
	        return Optional.empty(); // Impede o cadastro se já existir
	    }

	    usuario.setSenha(criptografarSenha(usuario.getSenha()));
	    return Optional.of(usuarioRepository.save(usuario));
	}
	
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario){
		
		if(usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			usuario.setSenha(criptografarSenha(usuario.getSenha()));
			
			return Optional.ofNullable(usuarioRepository.save(usuario));
		}
		return Optional.empty();
		
	}
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		var credenciais = new UsernamePasswordAuthenticationToken(
				usuarioLogin.get().getUsuario(),
				usuarioLogin.get().getSenha()
		);
		
		Authentication authentication = authenticationManager.authenticate(credenciais);
		
		if(authentication.isAuthenticated()) {
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario()) ;
			
			if(usuario.isPresent()) {
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setSenha("");
				usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
				
				return usuarioLogin;
			}
		}
		return Optional.empty();
	}
	
	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}
	
	
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(senha);
	}
	
}
