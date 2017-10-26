from dbrun import *
db.create_all()

# Person
pid = 1
email = "apti@gmail.com"
password = "abcd"
pname = "Apti"
plocation = "Bihar"
precruitment = "y"
page = 21
pgender = "M"
pexpertise = "Beginner"

cid = 1
# a = Person(email='ab@gmail.com',password='pass',name='Mudit',location='Delhi',recruitment='y',age=18,gender='m',expertise='beg')
a = Person(email=email,password=password,name=pname,location=plocation,recruitment=precruitment,age=page,gender=pgender,expertise=pexpertise)
b = Cooks(name="Paneer",preptime=20,ctype="Indian",cost=200,region="Delhi",public=1,personid=pid)
c = Stages(comment="Cook Well",ingredient="Masala",time=5,cookid=cid)
c1 = Stages(comment="Cook End",ingredient="Paneer",time=5,cookid=cid)



db.session.add(a)
db.session.add(b)
db.session.add(c)
db.session.add(c1)
db.session.commit()
