import mariadb
import sys

pop_db = 'kor_population'
admin_div_table = 'admin_division'


def db_connect():
    func_name = 'db_connect'
    # connection information in db.connector_info

    try:
        conn = mariadb.connect(
            user='admin',
            password='11LPS_DB',
            host='database-1.ct9om9hdtdmb.us-east-2.rds.amazonaws.com',
            port=int(3306),
            database='LibraryData'
        )
        conn.autocommit = False

    except mariadb.Error as e:
        print(f">>> {func_name} -  MariaDB Error: {e}")
        sys.exit(1)

    return conn


def db_execute(cur: mariadb.connection.cursor, query: str, python_data=None):
    func_name = 'db_execute'
    # python_data is a tuple

    try:
        cur.execute(query, python_data)
        query_outcome = True

    except mariadb.Error as e:
        print(python_data)
        query_outcome = False
        print(f">>> {func_name} -  MariaDB Error: {e}")

    return query_outcome


def db_executemany(cur: mariadb.connection.cursor, query: str, python_data: list):
    func_name = 'db_executemany'
    # python_data is a list of tuples

    try:
        cur.executemany(query, python_data)
        query_outcome = True
        print(f">>> {func_name} -  {cur.rowcount} records have been inserted/updated.")

    except mariadb.Error as e:
        query_outcome = False
        print(f">>> {func_name} -  MariaDB Error: {e}")
        print(cur.statement,"\trow=", cur.rowcount)
        for i in range(-1,2,1):
            print(python_data[cur.rowcount+i])
        print(cur.next())

    return query_outcome
