from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^register/$', views.register, name='register'),
    url(r'^history-store/$', views.history_store, name='history store'),
    url(r'^store/$', views.store, name='store'),
    url(r'^update-cycle/$', views.update_cycle, name='update-cycle'),
]