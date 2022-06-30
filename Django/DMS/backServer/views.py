from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView
from .serializers import *
from backServer.models import *
from backServer.mqtt import publisher
from datetime import date
import random
from rest_framework import serializers
from . import AImodel


class UserListAPI(APIView):
    def post(self,request):
        print(request.data,request.content_type)
        user=User.objects.get(id=request.data['id'])
        serializer=UserSerializer(user)
        return Response(serializer.data)
# Create your views here.
class CreateRent(APIView):
    def get(self,request):
        user=User.objects.get(id='hong@gildong.com')
        Rent.objects.create(user=user,book=TestBookList.objects.get(barcode='0000'))
        for i in range(1000,10000,1000):
            book=TestBookList.objects.get(barcode=str(i))
            Rent.objects.create(user=user,book=book)
        rents=Rent.objects.all()
        return Response(RentSerializer(rents,many=True).data)

class RentListAPI(APIView):
    def post(self,request):
        user=User.objects.filter(id=request.data['user_id'])
        rents=Rent.objects.filter(user=user[0],returned=None)
        id_list=[]
        for rent in rents:
            id_list.append(rent.book.id)
        books=TestBookList.objects.filter(id__in=id_list)
        return Response(BookSerializer(books,many=True).data)

class StartBarcode(APIView):
    def get(self,request):
        publisher.startMqtt()
        return Response({'message':"connection success"})

class GetBookRecommendation(APIView):
    def post(self,request):
        user=User.objects.get(id=request.data['user_id'])
        rcmd_kdc_no=AImodel.user_model.predict([[user.gender,int(user.age/20)]])
        books=BookList.objects.filter(kdc_class_no__icontains=str(int(rcmd_kdc_no))+'.')
        book=random.choice(books)
        return Response(BookSerializer(book).data)