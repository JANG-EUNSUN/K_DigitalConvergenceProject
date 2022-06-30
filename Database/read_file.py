import mariadb
import sys
import csv
import re
import random
from pathlib import Path
from connector import *
from query import *

# 경로 지정
path = './user_loan_info.csv'
data =[]

# CSV 헤더와 테이블 컬럼 맞추기

# 맵핑 함수
def read_csv_mapping(path):
    file = open(path,'r',encoding="UTF-8-SIG")
    r_data = csv.reader(file)
    header= next(r_data)
    header=header[8:]
    header+=['name','id','phone','password']
    for i in r_data:
        i = i[8:]
        for index,field in enumerate(i):
            if index==0:
                i[index]=float(field)
            elif index==1:
                i[index]=int(float(field))
            else:
                i[index]=int(float(field))*20+random.randint(0,19)
            if (field is None) or (field==""):
                i[index]=None
        i+=createrandomUser()
        dict1 = dict(zip(header,i))
        data.append(dict1)

    header = list(dict1.keys())

    return data, header

# csv 파일을 dict로 읽어오는 함수
def read_csv_as_dict_list(file_path: Path):
    data = []
    with open(file_path) as file:
        reader = csv.DictReader(file)
        for row in reader:
            data.append(row)
    return data

def createrandomUser():
    import random
    random.seed()
    name=''
    for i in range(random.randint(3,49)):
        name+=chr(96+random.randint(1,26))
    email=''
    emailsep=[]
    emailsep.append(random.randint(5,60))
    emailsep.append(random.randint(5,65-emailsep[0]))
    for i in range(emailsep[0]):
        email+=chr(96+random.randint(1,26))
    email+='@'
    for i in range(emailsep[1]):
        email+=chr(96+random.randint(1,26))
    email+=random.choice(['.net','.com','.co.kr'])
    phone='010-'
    for i in range(4):
        phone+=str(random.randint(0,9))
    phone+='-'
    for i in range(4):
        phone += str(random.randint(0, 9))
    password=''
    keyboard=[x for x in range(48,58)]
    keyboard+=[x for x in range(65,91)]
    keyboard+=[x for x in range(97,123)]
    keyboard+=[33,35,36,37,64,95,45,42,46,47]
    for i in range(30):
        password+=chr(random.choice(keyboard))
    user=[name,email,phone,password]
    return user

# 맵핑
result = read_csv_mapping(path)

# 컬럼과 데이터 추출
lst1 =result[0]
header = result[1]
#DB연결
conn = db_connect()
cur = conn.cursor()
# 데이터 입력
insert_many(cur, 'AIdata', header, lst1)

# DB연결 종료
cur.close()
conn.commit()
conn.close()