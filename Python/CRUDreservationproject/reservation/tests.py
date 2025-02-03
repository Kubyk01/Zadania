from django.test import TestCase, Client
from django.urls import reverse
from django.contrib.auth.models import User
from reservation.models import Resource, Reservation
import datetime

class TestReservation(TestCase):
    def setUp(self):
        self.client = Client()
        self.user = User.objects.create_user(username='testuser', password='12345')
        self.resource = Resource.objects.create(
            name='Test Resource',
            resource_type='sala konferencyjna',
            location='Room 101',
            availability='Poniedziałek-Piątek, 9:00-18:00',
            min_reservation_time=datetime.timedelta(minutes=30),
            max_reservation_time=datetime.timedelta(hours=8)
        )
        
    def test_create_reservation(self):
        self.client.login(username='testuser', password='12345')
        url = reverse('reservations-list')
        data = {
            'resource': self.resource.pk,
            'start_time': datetime.timezone.now() + datetime.timedelta(minutes=60),
            'end_time': datetime.timezone.now() + datetime.timedelta(hours=1)
        }
        
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, 201)
        self.assertEqual(Reservation.objects.count(), 1)
        
    def test_conflict_reservation(self):
        self.client.login(username='testuser', password='12345')
        url = reverse('reservations-list')
        start_time = datetime.timezone.now() + datetime.timedelta(minutes=60)
        end_time = datetime.timezone.now() + datetime.timedelta(hours=1)
        Reservation.objects.create(
            user=self.user,
            resource=self.resource,
            start_time=start_time,
            end_time=end_time
        )
        
        data = {
            'resource': self.resource.pk,
            'start_time': start_time - datetime.timedelta(minutes=30),
            'end_time': end_time + datetime.timedelta(minutes=30)
        }
        
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, 400)
    
    def test_get_most_reserved_resources(self):
        self.client.login(username='testuser', password='12345')
        Reservation.objects.create(
            user=self.user,
            resource=self.resource,
            start_time=datetime.timezone.now() + datetime.timedelta(minutes=60),
            end_time=datetime.timezone.now() + datetime.timedelta(hours=1)
        )
        
        url = reverse('reservationreport-list')
        response = self.client.get(url, format='json')
        self.assertEqual(response.status_code, 200)
        most_reserved_resources = response.json()['most_reserved_resources']
        self.assertEqual(len(most_reserved_resources), 1)
        self.assertEqual(most_reserved_resources[0]['name'], 'Test Resource')
        self.assertEqual(most_reserved_resources[0]['count'], 1)