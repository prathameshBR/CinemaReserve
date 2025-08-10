import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static int loggedInUserId = -1;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== CinemaReserve ===");
            if (loggedInUserId == -1) {
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Choice: ");
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 1) login();
                else if (choice == 2) register();
                else break;
            } else {
                System.out.println("1. View Movies");
                System.out.println("2. Book Ticket");
                System.out.println("3. My Bookings");
                System.out.println("4. Logout");
                System.out.print("Choice: ");
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> viewMovies();
                    case 2 -> bookTicket();
                    case 3 -> viewMyBookings();
                    case 4 -> loggedInUserId = -1;
                    default -> System.out.println("Invalid choice");
                }
            }
        }
        System.out.println("Bye!");
    }

    static void login() {
        System.out.print("Username: ");
        String uname = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine().trim();
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uname);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    loggedInUserId = rs.getInt("id");
                    System.out.println("Login successful! (user id = " + loggedInUserId + ")");
                } else {
                    System.out.println("Invalid credentials!");
                }
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void register() {
        System.out.print("Choose username: ");
        String uname = sc.nextLine().trim();
        System.out.print("Choose password: ");
        String pass = sc.nextLine().trim();

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uname);
            ps.setString(2, pass);
            ps.executeUpdate();
            System.out.println("✅ Registration successful! You can now log in.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Username already exists! Try another.");
        } catch (Exception e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
    }

    static void viewMovies() {
        String sql = "SELECT id, title, total_seats, available_seats FROM movies";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- Movies ---");
            while (rs.next()) {
                System.out.printf("%d. %s | total: %d | available: %d%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("total_seats"),
                        rs.getInt("available_seats"));
            }
        } catch (Exception e) {
            System.out.println("Error viewing movies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void bookTicket() {
        System.out.print("Enter Movie ID: ");
        int mid = Integer.parseInt(sc.nextLine());
        System.out.print("Enter number of seats: ");
        int seats = Integer.parseInt(sc.nextLine());
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(
                    "SELECT available_seats FROM movies WHERE id = ? FOR UPDATE")) {
                check.setInt(1, mid);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Movie not found!");
                        con.rollback();
                        return;
                    }
                    int available = rs.getInt("available_seats");
                    if (available < seats) {
                        System.out.println("Not enough seats available!");
                        con.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement update = con.prepareStatement(
                    "UPDATE movies SET available_seats = available_seats - ? WHERE id = ?")) {
                update.setInt(1, seats);
                update.setInt(2, mid);
                update.executeUpdate();
            }

            try (PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO bookings (user_id, movie_id, seats_booked) VALUES (?, ?, ?)")) {
                insert.setInt(1, loggedInUserId);
                insert.setInt(2, mid);
                insert.setInt(3, seats);
                insert.executeUpdate();
            }

            con.commit();
            System.out.println("Booking successful!");
        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }
            System.out.println("Booking failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    static void viewMyBookings() {
        String sql = "SELECT b.id, m.title, b.seats_booked, b.booking_time " +
                     "FROM bookings b JOIN movies m ON b.movie_id = m.id " +
                     "WHERE b.user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loggedInUserId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- My Bookings ---");
                while (rs.next()) {
                    System.out.printf("Booking %d | %s | seats: %d | at: %s%n",
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getInt("seats_booked"),
                            rs.getTimestamp("booking_time").toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}