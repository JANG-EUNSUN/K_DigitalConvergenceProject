import re
from django.core.exceptions import ValidationError

def phoneNumValidator(number):
    p=re.compile('[0-9]{3,3}-[0-9]{4,4}-[0-9]{4,4}')
    if not p.match(number) or not p.match(number).group()==number:
        raise ValidationError('not korean mobile phone number format. xxx-xxxx-xxxx format needed')