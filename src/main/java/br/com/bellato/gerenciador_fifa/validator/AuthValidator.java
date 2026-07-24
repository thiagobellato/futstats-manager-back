package br.com.bellato.gerenciador_fifa.validator;

/**
 * Validações de autenticação e cadastro de usuário.
 */
public final class AuthValidator {

    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 50;
    private static final int PASSWORD_MIN = 6;

    private AuthValidator() {
    }

    public static void validarRegistro(String username, String password) {
        validarUsername(username);
        validarPassword(password);
    }

    public static void validarLogin(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
    }

    public static void validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }
        String trimmed = username.trim();
        if (trimmed.length() < USERNAME_MIN || trimmed.length() > USERNAME_MAX) {
            throw new IllegalArgumentException(
                    "O nome de usuário deve ter entre " + USERNAME_MIN + " e " + USERNAME_MAX + " caracteres.");
        }
        if (!trimmed.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                    "O nome de usuário pode conter apenas letras, números, ponto, hífen e underscore.");
        }
    }

    public static void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (password.length() < PASSWORD_MIN) {
            throw new IllegalArgumentException("A senha deve ter no mínimo " + PASSWORD_MIN + " caracteres.");
        }
    }
}
