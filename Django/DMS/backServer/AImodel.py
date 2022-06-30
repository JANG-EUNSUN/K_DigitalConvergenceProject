import pandas as pd
import sklearn
from   sklearn.model_selection import train_test_split
from   sklearn.linear_model    import LinearRegression
from .models import Aidata


user_loan_info=pd.DataFrame(Aidata.objects.values())
temp=user_loan_info['age']/20
user_X=pd.concat([user_loan_info['sex'].astype(int),temp.astype(int)],axis=1)
user_Y = user_loan_info['kdc_class_no']
X_train, X_test, y_train, y_test = train_test_split(user_X, user_Y,test_size = 0.2,random_state = 100)
user_model = LinearRegression().fit(X_train.values, y_train)