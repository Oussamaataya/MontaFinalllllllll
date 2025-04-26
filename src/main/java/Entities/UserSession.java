package Entities;

import java.io.Serializable;

public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private static UserSession instance;
    private User user;
    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Vérifie si un utilisateur est actuellement connecté
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean isLoggedIn() {
        return this.user != null;
    }

    public void logout() {
        // Vider le panier de l'utilisateur lors de la déconnexion
      Cart.getInstance().clearCart();

        this.id = null;
        this.user = null;
        this.instance = null;
    }
}
