from rest_framework import serializers
from .models import *

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model=User
        fields='__all__'

class RentSerializer(serializers.ModelSerializer):
    class Meta:
        model=Rent
        fields='__all__'

class BookSerializer(serializers.ModelSerializer):
    class Meta:
        model=BookList
        fields='__all__'