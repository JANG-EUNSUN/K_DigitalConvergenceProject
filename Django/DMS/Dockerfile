FROM python:3.10.5

COPY ./DMS /home/django/

WORKDIR /home/django/

RUN pip install -r requirements.txt

CMD python manage.py migrate && \
python manage.py runserver 0:8000

EXPOSE 8000

