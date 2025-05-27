package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioDTO;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String USUARIO_ROOT_EMAIL = "root@email.com";
	private static final String USUARIO_ROOT_SENHA = "rootroot";
	private static final String BASE_URL_USUARIOS = "/usuarios";
	
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
	}
	
	@Test
	@DisplayName("Deve cadastrar um novo usuário com sucesso")
	public void deveCadastrarUsuario() {
		
		//Give
		Usuario usuario = TestBuilder.criarUsuario(null, "Cristiano Ronaldo", "cristiano_ronaldo@email.com", "123456789");	
		
		//When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class
				);
		//Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals("Cristiano Ronaldo", resposta.getBody().getNome());
		assertEquals("cristiano_ronaldo@email.com", resposta.getBody().getUsuario());
	}
	
	@Test
	@DisplayName("Não deve permitir a duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		//Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Lionel Messi", "lionel_messi@email.com", "123456789");	
		usuarioService.cadastrarUsuario(usuario);
		
		//When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<String> resposta = testRestTemplate.exchange(
				BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, String.class
				);
		
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		
	}
	
	// Método de teste para verificar a atualização de um usuário existente.
			@Test
			@DisplayName("Deve atualizar um usuário existente") // Nome legível para o teste
			public void deveAtualizarUmUsuario() {
				
				//Given (Dado): Prepara os dados.
				// Cria um usuário inicial para ser cadastrado e depois atualizado.
				Usuario usuario = TestBuilder.criarUsuario(null, "Juliana Andrews", "juliana_andrews@email.com.br",
						"juliana123");
				// Cadastra o usuário e obtém o Optional<Usuario> retornado pelo serviço.
				Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
				
				// Cria um objeto Usuario com os dados atualizados.
				// É importante usar o ID do usuário recém-cadastrado para a atualização.
				Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Juliana Ramos", 
						"juliana_ramos@email.com.br", "juliana123");

				//When (Quando): Executa a ação.
				// Cria uma HttpEntity com os dados do usuário atualizado.
				HttpEntity<Usuario> requisicao = new HttpEntity<>(usuarioUpdate);

				// Envia uma requisição PUT para a URL de atualização de usuários.
				// Usa autenticação básica com as credenciais do usuário root.
				ResponseEntity<Usuario> resposta = testRestTemplate
						.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
						.exchange(BASE_URL_USUARIOS + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);

				//Then (Então): Verifica os resultados.
				// AssertEquals: Verifica se o código de status da resposta é OK (200).
				assertEquals(HttpStatus.OK, resposta.getStatusCode());
				// AssertEquals: Verifica se o nome do usuário na resposta foi atualizado corretamente.
				assertEquals("Juliana Ramos", resposta.getBody().getNome());
				// AssertEquals: Verifica se o email do usuário na resposta foi atualizado corretamente.
				assertEquals("juliana_ramos@email.com.br", resposta.getBody().getUsuario());
			}
	
	@Test
	@DisplayName("Deve listar todos os usuários com sucesso")
	public void deveListarTodosUsuarios() {
		
		//Given 
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Tiquiho Soares", "tiquinho_soares@email.com", "123456789"));	
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Igor Jesus", "igor_jesus@email.com", "123456789"));		

		//When
		ResponseEntity<UsuarioDTO[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_USUARIOS + "/listarUsuarios", HttpMethod.GET, null, UsuarioDTO[].class
				);
		
		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("Deve buscar um usuário por ID")
	public void deveBuscarPorId() {
	    // Given
	    Usuario usuario = TestBuilder.criarUsuario(null, "Vinicius Jr", "vinicius_jr@email.com", "123456789");
	    Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);

	    Long id = usuarioCadastrado.get().getId();

	    // When
	    ResponseEntity<UsuarioDTO> resposta = testRestTemplate
	        .withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
	        .exchange(BASE_URL_USUARIOS + "/buscarPorId/" + id, HttpMethod.GET, null, UsuarioDTO.class);

	    // Then
	    assertEquals(HttpStatus.OK, resposta.getStatusCode());
	    assertEquals("Vinicius Jr", resposta.getBody().getNome());
	}


	
	@Test
	@DisplayName("Deve autenticar um usuário com sucesso")
	public void deveAutenticarLogin() {
	    // Given
	    usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Alisson Becker", "alisson@email.com", "123456789"));

	    // Criar login com os dados do usuário cadastrado
	    UsuarioLogin login = new UsuarioLogin();
	    login.setUsuario("alisson@email.com");
	    login.setSenha("123456789");

	    HttpEntity<UsuarioLogin> requisicao = new HttpEntity<>(login);

	    // When
	    ResponseEntity<UsuarioLogin> resposta = testRestTemplate.exchange(
	        BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicao, UsuarioLogin.class
	    );

	    // Then
	    assertEquals(HttpStatus.OK, resposta.getStatusCode());
	    assertNotNull(resposta.getBody().getToken()); // ou outro dado retornado
	}


}
