from dbrun import *
db.create_all()


a = Person(id=1,name="Mudit",location="Delhi",recruitment=0,age=18,gender="M",expertise="Novice")
b = Cooks(id=1,name="Paneer",preptime=20,ctype="Indian",cost=200,region="Delhi",public=1,personid=1)
c = Stages(id=1,comment="Cook Well",ingredient="Masala",time=5,cookid=1)
c1 = Stages(id=2,comment="Cook End",ingredient="Paneer",time=5,cookid=1)



db.session.add(a)
db.session.add(b)
db.session.add(c)
db.session.add(c1)
db.session.commit()

