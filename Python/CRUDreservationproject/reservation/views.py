from rest_framework import response, viewsets, permissions, status
from pydantic import ValidationError
from django.utils import timezone
from django.db.models import Count

from .models import Resource, Reservation
from reservation import models
from .serializers import ResourceSerializer, ReservationSerializer
from .task import send_reservation_notification
import datetime
import json


class ResourceViewSet(viewsets.ModelViewSet):
    queryset = Resource.objects.all()
    serializer_class = ResourceSerializer
    permission_classes = [permissions.IsAuthenticated]

class ReservationViewSet(viewsets.ModelViewSet):
    queryset = Reservation.objects.all()
    serializer_class = ReservationSerializer
    permission_classes = [permissions.IsAuthenticated]

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        
        if serializer.is_valid(raise_exception=True):
            reservation = serializer.save()

            try:
                reservation.full_clean()
            except ValidationError as e:
                return response.Response(e.messages, status=400)
            
            reservation.save()

            #should configure SMPT server
            #delay_seconds = (reservation.start_time.replace(tzinfo=None) - datetime.datetime.now()).seconds - 3600
            #if delay_seconds > 0:
                #send_reservation_notification.apply_async((reservation.id,), countdown=delay_seconds)

        headers = self.get_success_headers(serializer.data)
        return response.Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

class ReservationReportViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def list(self, request):
        return self.get_report()

    def get_report(self):
        reports = {
            'reservations_count': self.get_reservations_count(),
            'most_reserved_resources': self.get_most_reserved_resources(),
            'average_reservation_time': self.get_average_reservation_time()
        }
        return response.Response(reports)

    def get_reservations_count(self):
        today = timezone.now().date()
        weekly_start = today - datetime.timedelta(days=today.weekday())
        monthly_start = today.replace(day=1)
        
        daily_count = Reservation.objects.filter(start_time__date=today).count()
        weekly_count = Reservation.objects.filter(start_time__date__range=[weekly_start, today]).count()
        monthly_count = Reservation.objects.filter(start_time__date__range=[monthly_start, today]).count()
        
        return {
            'daily': daily_count,
            'weekly': weekly_count,
            'monthly': monthly_count
        }

    def get_most_reserved_resources(self):
        resource_counts = Resource.objects.annotate(
            reservation_count=Count('reservation')
        ).order_by('-reservation_count')[:5]
        
        return [{'name': resource.name, 'count': resource.reservation_count} for resource in resource_counts]

    def get_average_reservation_time(self):
        reservations = Reservation.objects.all()
        total_duration = datetime.timedelta()

        for reservation in reservations:
            duration = reservation.end_time - reservation.start_time
            total_duration += duration

        average_duration = total_duration / len(reservations) if reservations else datetime.timedelta()
        return str(average_duration)
