package client.auth;

public interface AuthFactory<T> {
    public T get(final String... args);
}
