import firebase_admin
import uuid


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
            }
        }
    )
    print(f"{doc_ref.id} is created at {create_time}")
    quota_id  = doc_ref.id

    #Create the market
    coll_ref = db.collection("market")
    create_time, doc_ref = coll_ref.add(
        {
            "name": "Sath Market",
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

# create_farm()
# create_market()
# create_user()
# create_inventory()
# create_charity()