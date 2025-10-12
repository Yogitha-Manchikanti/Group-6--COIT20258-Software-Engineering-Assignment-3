package com.mycompany.coit20258assignment2;

/**
 * In-memory session holder for the current user.
 */
public final class Session {
    private static User current;

    private Session() {}

    public static void set(User u) { current = u; }
    public static void logout()    { current = null; }
    public static User get()       { return current; }
    public static String id()      { return current == null ? null : current.getId(); }

    public static boolean isPatient() { return current instanceof Patient; }
    public static boolean isDoctor()  { return current instanceof Doctor; }
    public static boolean isAdmin()   { return current instanceof Administrator; }
}
