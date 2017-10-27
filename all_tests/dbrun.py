
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////'+os.getcwd()+'/testdata.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False


db = SQLAlchemy(app)

class Cooks(db.Model):
	id = db.Column(db.Integer , primary_key = True, autoincrement=True)
	name = db.Column(db.String(30),nullable=False)
	image = db.Column(db.String(300))
	public = db.Column(db.String(1),nullable=False)