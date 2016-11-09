package edu.uiowa.cs;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class AirlineCustomerClient {

    private Connection cnx;

    public AirlineCustomerClient() {
        Map<String, String> env = System.getenv();
        String passwd = env.get("DBPASSWORD");

        try {
            // Establish a connection to your database
            cnx = DriverManager.getConnection("jdbc:mysql://dbdev.divms.uiowa.edu:3306/db_hawkid",
                    "hawkid",
                    passwd);

            // For this connection, set isolation level to SERIALIZABLE
            cnx.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Turn off autocommit (i.e., commit after every executed statement)
            // When autocommit is off, you must call commit to execute your statement(s).
            // In the java.sql (JDBC) API, you do not need to begin a transaction. A new
            // transaction begins when the connection is established and after each commit().
            cnx.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("failed to connect to database "+e);
            System.exit(1);
            try {
                cnx.close();
            } catch (Exception e2) {}
        }
    }

    /**
     * Clean up Client
     */
    public void close() {
        try {
            cnx.close();
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        AirlineCustomerClient a = new AirlineCustomerClient();

        // reserve a seat
        int seatGotten = a.reserveFlight();

        System.out.println("reserved seat? " + seatGotten);

        a.close();
    }

    /**
     * Try to reserve the first seat you see.
     *
     * If reserving the first seat fails then return -1 (does not retry automatically with the next seat)
     * If no seats are available then return -1
     */
    public int reserveFlight() {
        try {
            // Query the database for open flights
            Statement seeSeats = cnx.createStatement();
            ResultSet resultSet = seeSeats.executeQuery("select seat, status from Flights");
            int seat = -1;
            boolean openSeat = false;

            // iterate through the rows of the result until
            // we find a seat that is open
            while (resultSet.next()) {
                seat = resultSet.getInt("seat");
                int isAvailable = resultSet.getInt("status");
                if (isAvailable == 0) {
                    openSeat = true;
                    break;
                }
            }


            // if we found an available seat
            if (openSeat) {
                // wait for the user to confirm that they want the seat
                System.out.println("Found seat " + seat+ ". Press <ENTER> to continue.");
                try {
                    System.in.read();
                } catch (IOException e) {
                    System.out.println("failed response");
                    cnx.rollback();
                    return -1;
                }

                // update the seat to indicate it is taken
                PreparedStatement reserveSeat = cnx.prepareStatement("update Flights set status = 1 where seat = ?");
                reserveSeat.setInt(1, seat);
                reserveSeat.executeUpdate();

                // commit our transaction
                cnx.commit();

                // return the seat number
                return seat;
            } else {
                // no available seat so rollback the transaction and indicate failure
                cnx.rollback();
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: "+ e);
            try { cnx.rollback(); } catch (SQLException e2) {/* silence*/}
            return -1;
        }
    }
}
