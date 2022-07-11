import mysql.connector
import json
from mysql.connector import Error

def handle(req):
    connection = None 
    try :
        connection = mysql.connector.connect(user='root', password='1234',
                                host='127.0.0.1',
                                database='db')
        return read_passwords_from_db(connection)
    except Error as e:
        error = "Error while connecting to MySQL:" + e.msg 
        print(error)
        raise e
    finally:
        if connection is not None and connection.is_connected():
            connection.close()
            print("MySQL connection is closed")


def read_passwords_from_db(connection):
    cursor = connection.cursor()
    cursor.execute("SELECT * FROM passwords")
    pwds = [{"pwd": i[0]} for i in cursor.fetchall()]
    cursor.close()
    return json.dumps(pwds)