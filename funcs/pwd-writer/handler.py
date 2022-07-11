import mysql.connector
from mysql.connector import Error

def handle(password):
    if not password: return "No data specified"
    connection = None
    try :
        connection = mysql.connector.connect(user='root', password='1234',
                                host='127.0.0.1',
                                database='db')
        insert_into_db(connection, password)
    except Error as e:
        error = "Error while connecting to MySQL:" + e.msg
        print(error)
        raise e
    finally:
        if connection is not None and connection.is_connected():
            connection.close()
            print("MySQL connection is closed")

    return f"Successfully written password {password} in the database"


def insert_into_db(connection, data):
    cursor = connection.cursor()
    cursor.execute("INSERT INTO passwords VALUES(%s)", (data,))
    print("Written to database:", data)
    connection.commit()
    cursor.close()
