from dbrun import *
from flask import render_template , url_for , redirect , request, session , abort , flash , g
from sqlalchemy import exc
# g in flask exists globally

app.secret_key = os.urandom(24)
db.create_all()


def check_auth(givenemail, password): 
	result = Person.query.filter_by(email=givenemail).first()
	if result is None :
		return (False,False,-1)
	if result.password == password :
		return (True,True,result.id)
	else :
		return (True,False,-1)


@app.route('/')
def home():
	if g.user :
		return redirect(url_for('protected'))

	return render_template('home.html',)

@app.route('/userhome')
def userhome():
	return 'UserHome'

@app.route('/signup' , methods=['POST'])
def handle_signup():
	email = request.form['email']
	password = request.form['password']
	name = request.form['name']
	location = request.form['location']
	gender = request.form['gender']
	age = request.form['age']
	recruitment = request.form['recruitment']
	expertise = request.form['expertise']

	newperson = Person(email=email,
						password=password,
						name=name,
						location=location,
						recruitment=recruitment,
						age=age,
						gender=gender,
						expertise=expertise)
	try:
		db.session.add(newperson)
		db.session.commit()
	except exc.SQLAlchemyError :
		return 'Error in Database Commit.'

	return 'Signup Complete.'


@app.route('/authlogin', methods=["GET","POST"])
def handle_login():
	session.pop('user',None)
	givenemail = str(request.form['emaillogin'])
	password = str(request.form['passwordlogin'])
	e,p,i= check_auth(givenemail,password)
	error = 'email' + str(e) + 'and password' + str(p) 
	if e is True and p is True :
		session['user'] = i
		return redirect(url_for('protected'))
		# return 'Logged In !'
	else :
		return error 


@app.route('/dropsession')
def dropsession():
	session.pop('user',None)
	return redirect(url_for('home'))


@app.route('/protected')
def protected():
	if g.user:
		return 'Logged In!'
	return redirect(url_for('home'))

@app.before_request
def before_request():
	g.user = None
	if 'user' in session :
		g.user = session['user']







# -------------------------  API  -------------------------

# @api.route('/')



# @app.route('/api-signup' , methods=['POST'])
# def api_handle_signup():
# 	email = request.form['email']
# 	password = request.form['password']
# 	name = request.form['name']
# 	location = request.form['location']
# 	gender = request.form['gender']
# 	age = request.form['age']
# 	recruitment = request.form['recruitment']
# 	expertise = request.form['expertise']

# 	newperson = Person(email=email,
# 						password=password,
# 						name=name,
# 						location=location,
# 						recruitment=recruitment,
# 						age=age,
# 						gender=gender,
# 						expertise=expertise)
# 	try:
# 		db.session.add(newperson)
# 		db.session.commit()
# 	except exc.SQLAlchemyError :
# 		return 'Error in Database Commit.'

# 	return 'Signup Complete.'


# @app.route('/apiauthlogin', methods=["GET","POST"])
# def api_handle_login():
# 	session.pop('user',None)
# 	givenemail = str(request.form['emaillogin'])
# 	password = str(request.form['passwordlogin'])
# 	e,p,i= check_auth(givenemail,password)
# 	error = 'email' + str(e) + 'and password' + str(p) 
# 	if e is True and p is True :
# 		session['user'] = i
# 		return redirect(url_for('protected'))
# 		# return 'Logged In !'
# 	else :
# 		return error 











if __name__ == '__main__':
	app.run(debug = True)
