from query import insert_dict
from connector import db_connect
import csv
import requests

if __name__ == '__main__':
    f=open('db.csv','r')
    data=csv.reader(f)
    next(data)
    for line in data:
        row={col:val for col,val in zip(csv.reader(f)[0],line)}
        insert_dict(db_connect(),'test',row)

