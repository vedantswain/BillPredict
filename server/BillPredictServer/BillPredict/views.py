from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from models import CustomerProfile,Water,Electricity
import json
import dateutil.parser

# Create your views here.
@csrf_exempt
def register(request):
    response = "Registered user successfully"

    if request.method == 'POST':
        store_user(request)
    else:
       return HttpResponse(status=405)
    
    return HttpResponse(response)

def store_user(request):
	print(request.body)
	customer=json.loads(request.body)
	customerEntry=CustomerProfile(customer_no_water = customer.get('customer_no_water'),
		customer_no_electricity = customer.get('customer_no_electricity'),
		service_provider_water = customer.get('service_water'),
		service_provider_electricity = customer.get('service_electricity'))
	customerEntry.save()

@csrf_exempt
def update_cycle(request):
    if request.method == 'PUT':
    	return update_cycle_data(request)
    else:
    	return HttpResponse(status=405)

def update_cycle_data(request):
	# print(request.body)
	json_data=json.loads(request.body)
	if(json_data.get('type')=="water"):
		return update_cycle_water(json_data,json_data.get('customer_no'))
	elif(json_data.get('type')=="electricity"):
		return update_cycle_electricity(json_data,json_data.get('customer_no'))

	return HttpResponse(status=400)

def update_cycle_water(entry,cno):
	response = "Updated water cycle data successfully"
	try:
		customer=CustomerProfile.objects.get(customer_no_water=cno)
		old_entry=Water.objects.get(customer=customer,meter_reading=entry.get('old_cycle_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	Water.objects.filter(customer=customer,cycle_start_reading=entry.get('old_cycle_reading')).update(cycle_start_reading=entry.get('new_cycle_reading'))
	
	old_entry.meter_reading=entry.get('new_cycle_reading')
	old_entry.cycle_start_reading=entry.get('new_cycle_reading')
	old_entry.save()

	return HttpResponse(response)

def update_cycle_electricity(entry,cno):
	response = "Updated electricity cycle data successfully"
	try:
		customer=CustomerProfile.objects.get(customer_no_electricity=cno)
		old_entry=Electricity.objects.get(customer=customer,meter_reading=entry.get('old_cycle_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	Electricity.objects.filter(customer=customer,cycle_start_reading=entry.get('old_cycle_reading')).update(cycle_start_reading=entry.get('new_cycle_reading'))

	old_entry.meter_reading=entry.get('new_cycle_reading')
	old_entry.cycle_start_reading=entry.get('new_cycle_reading')
	old_entry.save()

	return HttpResponse(response)

@csrf_exempt
def store(request):
    if request.method == 'POST':
        return store_data(request)
    elif request.method == 'PUT':
    	return update_data(request)
    else:
       return HttpResponse(status=405)

def store_data(request):
	# print(request.body)
	json_data=json.loads(request.body)
	if(json_data.get('type')=="water"):
		return store_water(json_data,json_data.get('customer_no'))
	elif(json_data.get('type')=="electricity"):
		return store_electricity(json_data,json_data.get('customer_no'))

	return HttpResponse(status=400)

def store_water(entry,cno):
	response = "Stored water data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_water=cno)
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	reading_date=dateutil.parser.parse(entry.get('reading_date'))

	objs=Water.objects.filter(customer=customer,meter_reading = entry.get('meter_reading'),reading_date = reading_date);
	
	if not objs:
		print "New Entry"
	elif(objs.count()>0):
		return HttpResponse(status=409)

	cs_date=dateutil.parser.parse(entry.get('cycle_start_date'))
	waterEntry=Water(customer=customer,meter_reading = entry.get('meter_reading'),
		reading_date = reading_date,cycle_start_reading = entry.get('cycle_start_reading'),
		cycle_start_date = cs_date,location = entry.get('location')	)
	waterEntry.save()

	return HttpResponse(response)

def store_electricity(entry,cno):
	response = "Stored electricity data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_electricity=cno)
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	reading_date=dateutil.parser.parse(entry.get('reading_date'))
	objs=Electricity.objects.filter(customer=customer,meter_reading = entry.get('meter_reading'),reading_date = reading_date);
	
	if not objs:
		print "New Entry"
	elif(objs.count()>0):
		return HttpResponse(status=409)

	cs_date=dateutil.parser.parse(entry.get('cycle_start_date'))
	electricityEntry=Electricity(customer=customer,meter_reading = entry.get('meter_reading'),
		reading_date = reading_date,cycle_start_reading = entry.get('cycle_start_reading'),
		cycle_start_date = cs_date,location = entry.get('location')	)
	electricityEntry.save()

	return HttpResponse(response)

def update_data(request):
	# print(request.body)
	json_data=json.loads(request.body)
	if(json_data.get('type')=="water"):
		return update_water(json_data,json_data.get('customer_no'))
	elif(json_data.get('type')=="electricity"):
		return update_electricity(json_data,json_data.get('customer_no'))

	return HttpResponse(status=400)

def update_water(entry,cno):
	response = "Updated water data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_water=cno)
		old_entry=Water.objects.get(customer=customer,meter_reading=entry.get('old_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	except Water.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	old_entry.meter_reading=entry.get('new_reading')
	old_entry.save()

	return HttpResponse(response)

def update_electricity(entry,cno):
	response = "Updated electricity data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_electricity=cno)
		old_entry=Electricity.objects.get(customer=customer,meter_reading=entry.get('old_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	except Electricity.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	old_entry.meter_reading=entry.get('new_reading')
	old_entry.save()

	return HttpResponse(response)

@csrf_exempt
def delete(request):
    if request.method == 'PUT':
    	return delete_data(request)
    else:
    	return HttpResponse(status=405)

def delete_data(request):
	# print(request.body)
	json_data=json.loads(request.body)
	if(json_data.get('type')=="water"):
		return delete_water(json_data,json_data.get('customer_no'))
	elif(json_data.get('type')=="electricity"):
		return delete_electricity(json_data,json_data.get('customer_no'))

	return HttpResponse(status=400)

def delete_water(entry,cno):
	response = "Deleted water data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_water=cno)
		old_entry=Water.objects.get(customer=customer,meter_reading=entry.get('meter_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	except Water.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	old_entry.delete()

	return HttpResponse(response)

def delete_electricity(entry,cno):
	response = "Deleted electricity data successfully"
	print(entry)
	try:
		customer=CustomerProfile.objects.get(customer_no_electricity=cno)
		old_entry=Electricity.objects.get(customer=customer,meter_reading=entry.get('meter_reading'))
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)
	except Electricity.DoesNotExist:
		return HttpResponse(status=400)

	# print(entry.get('reading_date'))
	old_entry.delete()

	return HttpResponse(response)

@csrf_exempt
def history_store(request):
    if request.method == 'POST':
        return store_old_data(request)
    else:
       return HttpResponse(status=405)

def store_old_data(request):
	# print(request.body)
	json_data=json.loads(request.body)
	if(json_data.get('type')=="water"):
		return store_old_water(json_data.get('data'),json_data.get('customer_no'))
	elif(json_data.get('type')=="electricity"):
		return store_old_electricity(json_data.get('data'),json_data.get('customer_no'))

	return HttpResponse(status=400)

def store_old_water(data,cno):
	response = "Stored water history successfully"
	print(data)
	try:
		customer=CustomerProfile.objects.get(customer_no_water=cno)
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)

	for entry in data:
		# print(entry.get('reading_date'))
		reading_date=dateutil.parser.parse(entry.get('reading_date'))
		objs=Water.objects.filter(customer=customer,meter_reading = entry.get('meter_reading'),reading_date = reading_date);
		
		if not objs:
			print "New Entry"
		elif(objs.count()>0):
			continue

		cs_date=dateutil.parser.parse(entry.get('cycle_start_date'))
		waterEntry=Water(customer=customer,meter_reading = entry.get('meter_reading'),
			reading_date = reading_date,cycle_start_reading = entry.get('cycle_start_reading'),
			cycle_start_date = cs_date,location = entry.get('location')	)
		waterEntry.save()

	return HttpResponse(response)


def store_old_electricity(data,cno):
	response = "Stored electricity history successfully"
	print(data)
	try:
		customer=CustomerProfile.objects.get(customer_no_electricity=cno)
	except CustomerProfile.DoesNotExist:
		return HttpResponse(status=400)

	for entry in data:
		# print(entry.get('reading_date'))
		reading_date=dateutil.parser.parse(entry.get('reading_date'))
		reading_date=dateutil.parser.parse(entry.get('reading_date'))
		objs=Electricity.objects.filter(customer=customer,meter_reading = entry.get('meter_reading'),reading_date = reading_date);
		
		if not objs:
			print "New Entry"
		elif(objs.count()>0):
			continue

		cs_date=dateutil.parser.parse(entry.get('cycle_start_date'))
		electricityEntry=Electricity(customer=customer,meter_reading = entry.get('meter_reading'),
			reading_date = reading_date,cycle_start_reading = entry.get('cycle_start_reading'),
			cycle_start_date = cs_date,location = entry.get('location')	)
		electricityEntry.save()

	return HttpResponse(response)