from django.contrib import admin
from BillPredict.models import CustomerProfile,Electricity,Water

class CustomerProfileAdmin(admin.ModelAdmin):
	list_display=('customer_no','service_provider_water','service_provider_electricity','location')

class ElectricityAdmin(admin.ModelAdmin):
	list_display=('customer','meter_reading','reading_date','cycle_start_reading','cycle_start_date')

class WaterAdmin(admin.ModelAdmin):
	list_display=('customer','meter_reading','reading_date','cycle_start_reading','cycle_start_date')	

# Register your models here.
admin.site.register(CustomerProfile,CustomerProfileAdmin)
admin.site.register(Electricity,ElectricityAdmin)
admin.site.register(Water,WaterAdmin)