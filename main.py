from dbrun import *
from flask import render_template , url_for , redirect , request, session , abort , flash , g
from sqlalchemy import exc
# g in flask exists globally
from functools import wraps


app.secret_key = os.urandom(24)
db.create_all()


def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if g.user is None:
        	flash('Login Required!')
        	return redirect(url_for('home'))
        return f(*args, **kwargs)
    return decorated_function


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
	return render_template('home.html')

# @app.route('/userhome')
# def userhome():
# 	return 'UserHome'

@app.route('/signup' , methods=['POST'])
def handle_signup():
	email = (request.form['email'])
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
		flash('Database Faliure. Apologies.')
		return redirect(url_for('home'))

	flash ('Signup Complete '+name.split(' ')[0]+'. You may now Kitch In')
	return redirect(url_for('home'))


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
		flash('Invalid Email/Password')
		return redirect(url_for('home'))


@app.route('/dropsession')
@login_required
def dropsession():
	session.pop('user',None)
	flash('Logged Out!')
	return redirect(url_for('home'))

@app.route('/success/<id>')
@login_required
def user_page(id):
	if g.user != int(id) :
		return redirect(url_for('user_page',id=g.user))

	return render_template("after_login/index.html")
	flash('No Session Running!')
	return redirect(url_for('home'))


@app.route('/protected')
@login_required
def protected():
	return  redirect(url_for('user_page',id=g.user)) 


@app.route('/protected-recipe-add')
@login_required
def recipe_page():
	return render_template("after_login/AddRecipe.html")

@app.route('/protected-stage-add')
@login_required
def stages_page():
	return render_template("after_login/CommitIndex.html")

@app.before_request
def before_request():
	g.user = None
	if 'user' in session :
		g.user = session['user']

@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html' , errortext='Dish Not Found',errornumber='404'), 404


@app.after_request
def add_header(response):   
	response.headers['Cache-Control'] = 'no-cache, no-store, must-revalidate'
	response.headers['Pragma'] = 'no-cache'
	response.headers['X-UA-Compatible'] = 'IE=Edge,chrome=1'
	if ('Cache-Control' not in response.headers):
		response.headers['Cache-Control'] = 'public, max-age=600'
	return response





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
	app.run(debug=True)
