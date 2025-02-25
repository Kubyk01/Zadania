# Generated by Django 4.2.3 on 2025-02-03 15:57

import datetime
from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Resource',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('resource_type', models.CharField(max_length=100)),
                ('location', models.CharField(max_length=255)),
                ('availability', models.TextField()),
                ('min_reservation_time', models.DurationField(default=datetime.timedelta(seconds=1800))),
                ('max_reservation_time', models.DurationField(default=datetime.timedelta(seconds=28800))),
            ],
        ),
        migrations.CreateModel(
            name='Reservation',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('start_time', models.DateTimeField()),
                ('end_time', models.DateTimeField()),
                ('resource', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='reservation.resource')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
    ]
