from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////'+os.getcwd()+'/testdata.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)


# class User(db.Model):
#     id = db.Column(db.Integer, primary_key=True)
#     username = db.Column(db.String(80), unique=True, nullable=False)
#     email = db.Column(db.String(120), unique=True, nullable=False)

#     def __repr__(self):
#         return '<User %r>' % self.username

class Person(db.Model):
	id = db.Column(db.Integer , primary_key = True )
	email = db.Column(db.String(80), unique=True)
	password = db.Column(db.String(30),nullable=False)
	name = db.Column(db.String(30),nullable=False)
	location = db.Column(db.String(30),nullable=False)
	recruitment = db.Column(db.String(1),nullable=False)
	age = db.Column(db.Integer,nullable=False)
	gender = db.Column(db.String(1),nullable=False)
	expertise = db.Column(db.String(15),nullable=False)
	mycooks = db.relationship('Cooks', backref='person', lazy=True)


class Cooks(db.Model):
	# img add.
	id = db.Column(db.Integer , primary_key = True, autoincrement=True)
	name = db.Column(db.String(30),nullable=False)
	preptime = db.Column(db.Integer,nullable=False)
	ctype = db.Column(db.String(30),nullable=False)
	cost = db.Column(db.Integer,nullable=False)
	region = db.Column(db.String(30),nullable=False)
	public = db.Column(db.String(1),nullable=False)
	stages = db.relationship('Stages',backref='cook',lazy=True)
	personid = db.Column(db.Integer,db.ForeignKey('person.id'),nullable=False)



class Stages(db.Model):
	id = db.Column(db.Integer , primary_key = True, autoincrement=True)
	comment = db.Column(db.String(150),nullable=False)
	ingredient = db.Column(db.String(80),nullable=False)
	time = db.Column(db.Integer)
	cookid = db.Column(db.Integer , db.ForeignKey('cooks.id'),nullable=False)


