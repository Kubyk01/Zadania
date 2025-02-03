from celery import shared_task
from django.core.mail import send_mail
from .models import Reservation

@shared_task
def send_reservation_notification(reservation_id):
    reservation = Reservation.objects.get(id=reservation_id)
    subject = 'Your Reservation Reminder'
    message = f'Reminder: Your reservation for {reservation.resource.name} is scheduled on {reservation.start_time}.'
    email_from = 'your_email@example.com'
    recipient_list = [reservation.user.email]
    send_mail(subject, message, email_from, recipient_list)


@shared_task
def notify_on_reservation_changes(reservation_id):
    current_reservation = Reservation.objects.get(id=reservation_id)
    
    try:
        previous_reservation = Reservation.history.filter(pk=reservation_id).order_by('-history_date')[1]
    except IndexError:
        return
    
    if current_reservation.start_time != previous_reservation.start_time or \
       current_reservation.end_time != previous_reservation.end_time or \
       current_reservation.resource_id != previous_reservation.resource_id:
        
        subject = 'Reservation Change Notification'
        message = (
            f'Your reservation for {current_reservation.resource.name} has been updated:\n\n'
            f'Previous Start Time: {previous_reservation.start_time}, '
            f'New Start Time: {current_reservation.start_time}\n\n'
            f'Previous End Time: {previous_reservation.end_time}, '
            f'New End Time: {current_reservation.end_time}\n\n'
            f'Previous Resource: {previous_reservation.resource.name}, '
            f'New Resource: {current_reservation.resource.name}'
        )
        email_from = 'your_email@example.com'
        recipient_list = [current_reservation.user.email]
        
        send_mail(subject, message, email_from, recipient_list)