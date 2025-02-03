from django.db import models
import datetime
from pydantic import ValidationError
from simple_history.models import HistoricalRecords

RESOURCE_TYPE_CHOICES = [
    ('sala konferencyjna', 'Sala Konferencyjna'),
    ('pojazd', 'Pojazd'),
    ('sprzęt IT', 'Sprzęt IT')
]

class Resource(models.Model):
    name = models.CharField(max_length=255)
    resource_type = models.CharField(
        max_length=100,
        choices=RESOURCE_TYPE_CHOICES,  
        default='sala konferencyjna'
    )
    location = models.CharField(max_length=255)
    availability = models.TextField() #musi wygladać jako "Poniedziałek-Piątek, 9:00-18:00"
    min_reservation_time = models.DurationField(default=datetime.timedelta(minutes=30))
    max_reservation_time = models.DurationField(default=datetime.timedelta(hours=8))

class Reservation(models.Model):
    user = models.ForeignKey('auth.User', on_delete=models.CASCADE)
    resource = models.ForeignKey(Resource, on_delete=models.CASCADE)
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()

    def clean(self):
        if self.end_time - self.start_time < self.resource.min_reservation_time:
            raise ValidationError("Reservation must be at least the minimum allowed time.")
        if self.end_time - self.start_time > self.resource.max_reservation_time:
            raise ValidationError("Reservation cannot exceed the maximum allowed time.")

        day_translation = {
            'Poniedziałek': 'Monday',
            'Wtorek': 'Tuesday',
            'Środa': 'Wednesday',
            'Czwartek': 'Thursday',
            'Piątek': 'Friday',
            'Sobota': 'Saturday',
            'Niedziela': 'Sunday'
        }

        days_part, time_part = self.resource.availability.split(', ')
        days = days_part.split('-')

        ordered_days = list(day_translation.keys())
        start_index = ordered_days.index(days[0])    
        end_index = ordered_days.index(days[1])
        result = ordered_days[start_index:end_index + 1]

        start_day_of_week = self.start_time.weekday()
        end_day_of_week = self.end_time.weekday()

        start_day_name = list(day_translation.keys())[start_day_of_week]
        end_day_name = list(day_translation.keys())[end_day_of_week]

        if start_day_name not in result or end_day_name not in result:
                raise ValidationError("Reservation days are not within the allowed availability.")
        
        if Reservation.objects.filter(
            resource=self.resource,
            start_time__lt=self.end_time,
            end_time__gt=self.start_time
        ).exclude(id=self.id).exists():
            raise ValidationError("This time slot is already reserved.")
        
