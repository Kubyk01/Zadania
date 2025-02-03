from django.db.models.signals import post_save
from django.dispatch import receiver
from .models import Reservation
from .task import notify_on_reservation_changes

#@receiver(post_save, sender=Reservation)
#def handle_post_save(sender, instance, created, **kwargs):
    #if not created:
        #notify_on_reservation_changes(instance.id)