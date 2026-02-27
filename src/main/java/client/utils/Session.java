package client.utils;

import common.entities.User;

public class Session {
    private static final Session INSTANCE = new Session();
    private User currentUser;

    private Session() {}

    public static Session getInstance() {
        return INSTANCE;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int getCurrentUserId() {
        if (currentUser != null) {
            return currentUser.getId();
        }
        throw new IllegalStateException("Пользователь не авторизован");
    }
}