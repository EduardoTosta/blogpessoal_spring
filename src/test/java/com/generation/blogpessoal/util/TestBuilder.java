package com.generation.blogpessoal.util;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;

public class TestBuilder {
	public static Usuario criarUsuario(Long id, String nome, String email, String senha) {
		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNome(nome);
		usuario.setUsuario(email);
		usuario.setSenha(senha);
		usuario.setFoto("-");
		return usuario;	
	}
	
	public static Usuario criarUsuarioRoot() {
		return criarUsuario(null, "Root", "root@email.com", "rootroot");
	}
	
	public static UsuarioLogin criarUsuarioLogin(String usuarioEmail, String senha) {
        //Criar Usuario Fake para teste
        UsuarioLogin usuarioLogin = new UsuarioLogin();
        //Objeto Usuario login
        usuarioLogin.setUsuario(usuarioEmail);
        //UsuarioLogin fake email
        usuarioLogin.setSenha(senha);
        //UsuarioLogin fake senha
        // Se UsuarioLogin tiver outros campos que você precise preencher para testes,
        // como 'token', 'nome', 'foto', etc., você pode adicioná-los aqui
        // ou criar sobrecargas do método se a lógica for diferente para cada cenário.
        return usuarioLogin;

    }
}
