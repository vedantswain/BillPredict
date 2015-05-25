from django.db import models

# Create your models here.
class CustomerProfile(models.Model):
    customer_no = models.CharField(max_length=200)
    service_provider_water = models.CharField(max_length=200)
    service_provider_electricity = models.CharField(max_length=200)
    location = models.CharField(max_length=200)
    
    class Meta:
        db_table = u'customer_profile'

class Electricity(models.Model):
	customer = models.ForeignKey(CustomerProfile)
	meter_reading = models.FloatField(max_length=200)
	reading_date = models.DateField(max_length=200)
	cycle_start_reading = models.FloatField(max_length=200)
	cycle_start_date = models.DateField(max_length=200)	

	class Meta:
		db_table = u'electricity'

class Water(models.Model):
	customer = models.ForeignKey(CustomerProfile)
	meter_reading = models.FloatField(max_length=200)
	reading_date = models.DateField(max_length=200)
	cycle_start_reading = models.FloatField(max_length=200)
	cycle_start_date = models.DateField(max_length=200)	

	class Meta:
		db_table = u'water'