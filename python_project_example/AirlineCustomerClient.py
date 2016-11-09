import mysql.connector
import sys
import os


class AirlineCustomerClient(object):
    def __init__(self):
        passwd = os.environ['DBPASSWORD']

        # establish a connection to your database
        self.cnx = mysql.connector.connect(user='hawkid',
                                      host='dbdev.divms.uiowa.edu',
                                      database='db_hawkid',
                                      password=passwd)

        # in mysql.connector, use a cursor to execute all statements
        cur = self.cnx.cursor()

        # set the isolation level to serializable
        cur.execute("SET SESSION TRANSACTION ISOLATION LEVEL serializable")

        # need to commit for the command above to execute
        self.cnx.commit()

    def close(self):
        """
        Clean up
        """
        self.cnx.close()

    def reserve_seat(self):
        """
        Reserve the first seat you find or fail.

        Does not retry automatically if the first attempted seat gets taken before reservation goes through.

        :return:  seat number on success OR None if fail to reserve it OR None if no seats remain
        """
        try:
            # start a new transaction
            self.cnx.start_transaction()
            cur = self.cnx.cursor()

            # iterate through the rows of the result until
            # we find a seat that is open
            cur.execute("select seat, status from Flights")
            found = None
            for row in cur.fetchall():
                if row[1] == 0:
                    found = row[0]
                    break

            # if we found an available seat
            if found is not None:
                # wait for user to confirm they want the seat
                print "seat ", found, " is open. <Enter> to continue."
                sys.stdin.readline()

                # update that the seat is taken
                cur.execute("update Flights set status = 1 where seat = %s", (found,))
                self.cnx.commit()
                return found
            else:
                # if failed to reserve that seat then rollback and return None to indicate failure
                self.cnx.rollback()
                return None
        except mysql.connector.InternalError as e:
            print "failed to reserve: ", e
            try:
                self.cnx.rollback()
            except mysql.connector.InternalError as e:
                # silence
                pass
            return None


if __name__ == "__main__":
    # start an application client
    a = AirlineCustomerClient()

    # attempt to reserve a seat
    seatGotten = a.reserve_seat()
    print "reserved seat? ", seatGotten

    a.close()
