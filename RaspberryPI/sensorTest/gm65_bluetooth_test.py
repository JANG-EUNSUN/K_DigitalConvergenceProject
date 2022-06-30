from bluetooth import *
import struct

socket = BluetoothSocket(RFCOMM)
socket.connect(("98:DA:20:03:82:47", 1))
print("bluetooth connected!")

# 블루투스로 받아올 데이터 저장 배열
barcode = []

while True:
    data = socket.recv(1024)
    print(data)
    # 0~10까지의 값을 받아온다.
    for d in data:
        barcode.append(chr(d))
        if d in (0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10):
            print("값 : ", d)
    # print("Received: %d" %data)

    # 받아온 블루투스 배열의 마지막 빈칸 제거
    for b in barcode:
        print(b, end="")
    barcode = barcode[:-1]
    barcodeStr = "".join(barcode)
    print(barcodeStr)
    print(barcode)
    if (data == "q"):
        print("Quit")
        break

socket.close()