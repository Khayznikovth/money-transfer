package ru.khayz.db;

/**
 * @author Cinimex
 * @version 1.0
 * @since 9/7/2019 5:04 PM
 */
public class DbException extends Exception {
    public DbException(Throwable e) {
        super(e);
    }

    public DbException(String message) {
        super(message);
    }
}
