from django.contrib import admin
from BillPredict.models import CustomerProfile,Electricity,Water

class CustomerProfileAdmin(admin.ModelAdmin):
	list_display=('customer_no_water','customer_no_electricity','service_provider_water','service_provider_electricity')

class ElectricityAdmin(admin.ModelAdmin):
	list_display=('get_customer','meter_reading','reading_date','cycle_start_reading','cycle_start_date','location')

	def get_customer(self, obj):
		return '%s'%(obj.customer.customer_no_electricity)
	get_customer.short_description = 'Customer'
	get_customer.admin_order_field = 'customer_no'


class WaterAdmin(admin.ModelAdmin):
	list_display=('get_customer','meter_reading','reading_date','cycle_start_reading','cycle_start_date','location')

	def get_customer(self, obj):
		return '%s'%(obj.customer.customer_no_water)
	get_customer.short_description = 'Customer'
	get_customer.admin_order_field = 'customer_no'

# Register your models here.
admin.site.register(CustomerProfile,CustomerProfileAdmin)
admin.site.register(Electricity,ElectricityAdmin)
admin.site.register(Water,WaterAdmin)