# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='CustomerProfile',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('customer_no', models.CharField(max_length=200)),
                ('service_provider_water', models.CharField(max_length=200)),
                ('service_provider_electricity', models.CharField(max_length=200)),
                ('location', models.CharField(max_length=200)),
            ],
            options={
                'db_table': 'customer_profile',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Electricity',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('meter_reading', models.FloatField(max_length=200)),
                ('reading_date', models.DateField(max_length=200)),
                ('cycle_start_reading', models.FloatField(max_length=200)),
                ('cycle_start_date', models.DateField(max_length=200)),
                ('customer', models.ForeignKey(to='BillPredict.CustomerProfile')),
            ],
            options={
                'db_table': 'electricity',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Water',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('meter_reading', models.FloatField(max_length=200)),
                ('reading_date', models.DateField(max_length=200)),
                ('cycle_start_reading', models.FloatField(max_length=200)),
                ('cycle_start_date', models.DateField(max_length=200)),
                ('customer', models.ForeignKey(to='BillPredict.CustomerProfile')),
            ],
            options={
                'db_table': 'water',
            },
            bases=(models.Model,),
        ),
    ]
