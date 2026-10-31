package util;

import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database connection objects for UniMIS (Singleton: one object per DB account).
 *
 * The connection objects are created HERE, once, and every other class just
 * uses them directly - nobody else creates, fetches or closes a connection:
 *
 *   DBConnection.AUTH_CON    -> AuthService
 *   DBConnection.ADMIN_CON   -> Admin DAO classes
 *   DBConnection.LEC_CON     -> Lecturer DAO classes
 *   DBConnection.TO_CON      -> Technical Officer DAO classes
 *   DBConnection.UG_CON      -> Undergraduate DAO classes
 *
 * Example (in a DAO):
 *
 *   try (PreparedStatement ps = DBConnection.ADMIN_CON.prepareStatement(sql)) { ... }
 *
 * Notes
 *  - Each object always stays the same object. The real MySQL session behind it
 *    is opened the first time that account is actually used (so the admin
 *    account only connects when an admin DAO runs) and is re-opened
 *    automatically if MySQL drops it.
 *  - close() on these objects does nothing, so a DAO can never close the shared
 *    connection. If a DAO leaves a transaction open, close() rolls it back.
 *  - All sessions are closed automatically when the application exits.
 */
public final class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/unimis?allowPublicKeyRetrieval=true&useSSL=false";

    private static final String ADMIN_USER = "unimisadmin";
    private static final String ADMIN_PW = "Admin@Unimis@123";

    private static final String LEC_USER = "unimislec";
    private static final String LEC_PW = "Lec@Unimis@123";

    private static final String TO_USER = "unimisto";
    private static final String TO_PW = "To@Unimis@123";

    private static final String UG_USER = "unimisug";
    private static final String UG_PW = "Ug@Unimis@123";

    private static final String AUTH_USER = "unimisauth";
    private static final String AUTH_PW = "Auth@Unimis@123";

    // Must stay above the connection objects below (they register themselves here).
    private static final List<Link> LINKS = new ArrayList<>();

    // ------------------------------------------------------------------
    // The connection objects (created once, used everywhere)
    // ------------------------------------------------------------------
    public static final Connection AUTH_CON  = create(AUTH_USER, AUTH_PW);
    public static final Connection ADMIN_CON = create(ADMIN_USER, ADMIN_PW);
    public static final Connection LEC_CON   = create(LEC_USER, LEC_PW);
    public static final Connection TO_CON    = create(TO_USER, TO_PW);
    public static final Connection UG_CON    = create(UG_USER, UG_PW);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closeAll, "unimis-db-shutdown"));
    }

    private DBConnection() {
        // Singleton: no instances, the five objects above are the only ones.
    }

    private static Connection create(String user, String password) {
        Link link = new Link(user, password);
        LINKS.add(link);
        return (Connection) Proxy.newProxyInstance(
                DBConnection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                link);
    }

    /** Closes every open MySQL session (also runs automatically on exit). */
    public static void closeAll() {
        for (Link link : LINKS) {
            link.closeReal();
        }
    }

    /**
     * Holds the real MySQL session behind one connection object: opens it on
     * first use, re-opens it if it was closed or went stale, and ignores close().
     */
    private static final class Link implements InvocationHandler {

        private static final long IDLE_CHECK_MS = 30_000;

        private final String user;
        private final String password;
        private Connection real;
        private long lastUsed;

        Link(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                switch (method.getName()) {
                    case "equals":   return proxy == args[0];
                    case "hashCode": return System.identityHashCode(proxy);
                    default:         return "DBConnection[" + user + "]";
                }
            }

            if ("close".equals(method.getName()) && method.getParameterCount() == 0) {
                endOpenTransaction();
                return null;   // the shared connection stays open
            }

            Connection c = open();
            try {
                return method.invoke(c, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        private Connection open() throws SQLException {
            long now = System.currentTimeMillis();
            boolean stale = real == null
                    || real.isClosed()
                    || (now - lastUsed > IDLE_CHECK_MS && !real.isValid(2));
            if (stale) {
                closeReal();
                real = DriverManager.getConnection(URL, user, password);
            }
            lastUsed = now;
            return real;
        }

        private void endOpenTransaction() throws SQLException {
            if (real != null && !real.isClosed() && !real.getAutoCommit()) {
                real.rollback();
                real.setAutoCommit(true);
            }
        }

        synchronized void closeReal() {
            if (real != null) {
                try {
                    real.close();
                } catch (SQLException ignored) {
                    // nothing useful to do
                }
                real = null;
            }
        }
    }
}