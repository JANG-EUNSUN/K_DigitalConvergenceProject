# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models
from backServer.validator import phoneNumValidator
from datetime import date,timedelta

#python manage.py inspectdb > models.py


class BookList(models.Model):
    id = models.IntegerField(blank=True,primary_key=True)
    isbn13 = models.CharField(max_length=15, blank=True, null=True)
    title = models.TextField(blank=True, null=True)
    author = models.TextField(blank=True, null=True)
    publisher = models.TextField(blank=True, null=True)
    add_code = models.CharField(max_length=10, blank=True, null=True)
    img_url = models.TextField(blank=True, null=True)
    kdc_class_no = models.CharField(max_length=20, blank=True, null=True)
    title_replace = models.TextField(blank=True, null=True)
    author_replace = models.TextField(blank=True, null=True)
    barcode = models.CharField(max_length=50, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'book_list'

class User(models.Model):
    name = models.CharField(max_length=50)
    gender = models.BooleanField(default=None) #true-male, false-female
    id = models.EmailField(max_length=250,primary_key=True)
    age = models.SmallIntegerField(default=None)
    phone = models.CharField(max_length=13, validators=[phoneNumValidator],default=None)
    password=models.CharField(max_length=100,default=None)
    class Meta:
        db_table='backServer_user'




class Aidata(models.Model):
    kdc_class_no = models.FloatField(blank=True, null=True)
    sex = models.IntegerField(blank=True, null=True)
    age = models.IntegerField(blank=True, null=True)
    name = models.CharField(max_length=50, blank=True, null=True)
    id = models.CharField(max_length=100, primary_key=True)
    phone = models.CharField(max_length=20, blank=True, null=True)
    password = models.CharField(max_length=100, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'AIdata'

# 테이블 생성했습니다. 데이터도 10개 넣어놨어요
# Table Name : test_book_list
class TestBookList(models.Model):
    isbn13 = models.CharField(max_length=15, blank=True, null=True)
    title = models.TextField(blank=True, null=True)
    author = models.TextField(blank=True, null=True)
    publisher = models.TextField(blank=True, null=True)
    add_code = models.CharField(max_length=10, blank=True, null=True)
    img_url = models.TextField(blank=True, null=True)
    kdc_class_no = models.CharField(max_length=20, blank=True, null=True)
    title_replace = models.TextField(blank=True, null=True)
    author_replace = models.TextField(blank=True, null=True)
    barcode = models.CharField(max_length=50, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'test_book_list'

class Rent(models.Model):
    id=models.AutoField(primary_key=True)
    user=models.ForeignKey(User,on_delete=models.CASCADE)
    book=models.OneToOneField(TestBookList,on_delete=models.CASCADE)
    rent_date=models.DateField(auto_now_add=True)
    return_due=models.DateField(default=date.today()+timedelta(days=10))
    returned=models.DateField(default=None,null=True)
    class Meta:
        db_table='backServer_rent'
    # def save(self,*args,**kwargs):
    #     self.return_due=self.rent_date.date()+datetime.timedelta(days=10)
    #     super(Rent,self).save(*args,**kwargs)