# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('BillPredict', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='customerprofile',
            old_name='customer_no',
            new_name='customer_no_electricity',
        ),
        migrations.RenameField(
            model_name='customerprofile',
            old_name='location',
            new_name='customer_no_water',
        ),
        migrations.AddField(
            model_name='electricity',
            name='location',
            field=models.CharField(default=b'', max_length=200),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='water',
            name='location',
            field=models.CharField(default=b'', max_length=200),
            preserve_default=True,
        ),
    ]
