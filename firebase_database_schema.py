import firebase_admin
import uuid
import datetime


from firebase_admin import credentials
from firebase_admin import firestore

# Use the application default credentials.
cred = credentials.ApplicationDefault()
firebase_admin.initialize_app(cred)
db = firestore.client()

FARM_ID = "XIefGtyqhiyEaoQmGl2j"
USER_ID_UUID_ECE_452 = "CvhOFj4FolNbwADp2BlxtrO405x1"

def create_farm():
    coll_ref = db.collection("farm")
    create_time, doc_ref = coll_ref.add(
        {
            "name": "Rishan Farm",
            "users": [],
            "markets":[],
            "charities":[]
        }
    )

    print(f"{doc_ref.id} is created at {create_time}")

def create_market():
    #Create the associated quota first
    coll_ref = db.collection("quotas")
    create_time, doc_ref = coll_ref.add(
        {
            "produce": {
                "apple":10,
                "orange":23,
                "bananas": 90,
                "mangoes":23,
                "pineapples":12
            },
            "sale":{
                "apple":2,
                "orange":3,
                "banana": 8,
                "mangoes":5,
                "strawberry":13
            }
        }
    )
    print(f"{doc_ref.id} is created at {create_time}")
    quota_id  = doc_ref.id

    #Create the market
    coll_ref = db.collection("market")
    create_time, doc_ref = coll_ref.add(
        {
            "name": "Soumil Market",
            "quota_id": quota_id,
            "prices": {
                "apple":1,
                "orange":2,
                "bananas": 4,
                "mangoes":3,
                "pineapples":3
            },
            "sale_count": 0
        }
    )

    print(f"{doc_ref.id} is created at {create_time}")
    market_id = doc_ref.id

    doc_ref = db.collection("farm").document(FARM_ID)
    doc_ref.update({"markets": [market_id]})

def create_user():
    doc_ref = db.collection("users").document(USER_ID_UUID_ECE_452)
    doc_ref.set(
        {
           "farmID": FARM_ID,
           "admin":True
        }
    )

    doc_ref = db.collection("farm").document(FARM_ID)
    doc_ref.update({"users": [USER_ID_UUID_ECE_452]})

def create_inventory():
    doc_ref = db.collection("inventory").document(FARM_ID)
    doc_ref.set({
        "produce":{
                "apple":10,
                "orange":23,
                "bananas": 90,
                "mangoes":23,
                "pineapples":12,
                "soumil":30,
                "preyansh":12,
                "neel":1
            }
        })
def create_charity():
    coll_ref = db.collection("charity")
    create_time, doc_ref = coll_ref.add(
        {
            "name": "Mei's Charity",
            "donations":{
                "Apple": 12,
                "Mango":122
            }
        }
    )

    charity_id = doc_ref.id

    doc_ref = db.collection("farm").document(FARM_ID)
    doc_ref.update({"charities": [charity_id]})

def create_specfic_quota():
    doc_ref = db.collection("quotas").document("kUjSCofinDQXX9jkDoYP")
    doc_ref.set(
        {
           "produce":{
                "apple":10,
                "orange":23,
                "bananas": 90,
                "mangoes":23,
                "pineapples":12,
                "soumil":30,
                "preyansh":12,
                "neel":1
            },
            "sale":{
                "apple":0,
                "orange":3,
                "bananas":0,
                "mangoes":3,
                "pineapples":2,
                "soumil":3,
                "preyansh":2,
                "neel":0
            }
        }
    )

def create_transactions():
    date = datetime.datetime.now().strftime("%m/%d/%Y")
    time = datetime.datetime.now(tz=datetime.timezone.utc)

    doc_ref = db.collection("transactions").document("XIefGtyqhiyEaoQmGl2j")
    doc_ref.set({
        #date/time/action/produce/market/count/price
         "t1": {
              "date":date,
              "timestamp": time,
               "type": "SELL",
               "produce": "apple",
               "destination": "Rishan Market",
               "count": 2,
               "price": 12
             },
            "t2": {
              "date":date,
              "timestamp": time,
               "type": "HARVEST",
               "produce": "oranges",
               "destination": "Rishan Farm",
               "count": 3,
               "price": 15
             },
         "t3": {
             "date": date,
             "timestamp": time,
             "type":"DONATE",
             "produce": "bananas",
             "destination":"Sath Charity",
             "count":8,
             "price": 15},
        "t4": {
             "date": date,
             "timestamp": time,
             "type":"HARVEST",
             "produce": "mangos",
             "destination":"Rishan Farm",
             "count":8,
             "price": 15},
    })

create_transactions()
# rando()
# create_farm()
# create_market()
# create_user()
# create_inventory()
# create_charity()