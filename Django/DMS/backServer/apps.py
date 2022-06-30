from django.apps import AppConfig


class BackserverConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'backServer'
    # def ready(self):
    #         from backServer.mqtt import publisher
    #         import threading
    #         th=threading.Thread(target=publisher.startMqtt())
    #         th.start()
    #         pass